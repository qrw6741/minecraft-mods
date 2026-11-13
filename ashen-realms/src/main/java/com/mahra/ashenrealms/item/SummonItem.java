package com.mahra.ashenrealms.item;

import com.mahra.ashenrealms.entity.boss.BossHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** Right-click a block to summon a boss on top of it. */
public class SummonItem extends Item {
    private final Supplier<? extends EntityType<? extends Mob>> bossType;
    @Nullable private final ResourceKey<Level> requiredDimension;

    public SummonItem(Supplier<? extends EntityType<? extends Mob>> bossType,
                      @Nullable ResourceKey<Level> requiredDimension, Properties props) {
        super(props);
        this.bossType = bossType;
        this.requiredDimension = requiredDimension;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (requiredDimension != null && level.dimension() != requiredDimension) {
            if (!level.isClientSide && player != null) {
                player.displayClientMessage(Component.translatable("message.ashen_realms.wrong_dimension")
                        .withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel server) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            Mob boss = bossType.get().create(server);
            if (boss == null) return InteractionResult.FAIL;
            boss.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, server.random.nextFloat() * 360.0F, 0.0F);
            boss.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
            server.addFreshEntity(boss);

            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
            if (bolt != null) {
                bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                bolt.setVisualOnly(true);
                server.addFreshEntity(bolt);
            }
            BossHelper.announce(boss, Component.translatable("message.ashen_realms.summoned", boss.getDisplayName()));
            if (player == null || !player.getAbilities().instabuild) {
                ctx.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ashen_realms.summon").withStyle(ChatFormatting.GRAY));
        if (requiredDimension == Level.NETHER) {
            tooltip.add(Component.translatable("tooltip.ashen_realms.nether_only").withStyle(ChatFormatting.RED));
        }
    }
}
