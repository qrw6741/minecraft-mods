package com.mahra.ashenexpanse.item;

import com.mahra.ashenexpanse.entity.AshenTyrant;
import com.mahra.ashenexpanse.registry.ModDimensions;
import com.mahra.ashenexpanse.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Use on a block in the Ashlands to summon the Ashen Tyrant. */
public class TyrantSigilItem extends Item {
    public TyrantSigilItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.dimension() != ModDimensions.ASHLANDS) {
            if (!level.isClientSide && player != null) {
                player.displayClientMessage(Component.translatable("message.ashen_expanse.sigil_wrong_place")
                        .withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }
        if (level instanceof ServerLevel server) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            AshenTyrant boss = ModEntities.ASHEN_TYRANT.get().create(server);
            if (boss == null) return InteractionResult.FAIL;
            boss.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, server.random.nextFloat() * 360F, 0);
            boss.finalizeSpawn(server, server.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
            server.addFreshEntity(boss);
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
            if (bolt != null) {
                bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                bolt.setVisualOnly(true);
                server.addFreshEntity(bolt);
            }
            for (ServerPlayer p : server.players()) {
                if (p.distanceToSqr(boss) < 64 * 64) {
                    p.sendSystemMessage(Component.translatable("message.ashen_expanse.tyrant_awakens")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                }
            }
            if (player == null || !player.getAbilities().instabuild) ctx.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ashen_expanse.sigil").withStyle(ChatFormatting.GRAY));
    }
}
