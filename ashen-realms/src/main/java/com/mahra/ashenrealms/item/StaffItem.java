package com.mahra.ashenrealms.item;

import com.mahra.ashenrealms.entity.projectile.ArcaneBolt;
import com.mahra.ashenrealms.entity.projectile.SunfireBolt;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StaffItem extends Item {
    public enum Kind { ARCANE, SUNFIRE }

    private final Kind kind;

    public StaffItem(Kind kind, Properties props) {
        super(props);
        this.kind = kind;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (kind == Kind.ARCANE) {
                ArcaneBolt bolt = new ArcaneBolt(level, player);
                bolt.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.2F, 0.5F);
                level.addFreshEntity(bolt);
            } else {
                for (int i = -1; i <= 1; i++) {
                    SunfireBolt bolt = new SunfireBolt(level, player);
                    bolt.shootFromRotation(player, player.getXRot(), player.getYRot() + i * 9.0F, 0.0F, 1.8F, 0.5F);
                    level.addFreshEntity(bolt);
                }
            }
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                kind == Kind.ARCANE ? SoundEvents.EVOKER_CAST_SPELL : SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS, 0.8F, 1.2F);
        player.getCooldowns().addCooldown(this, kind == Kind.ARCANE ? 12 : 30);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return kind == Kind.ARCANE ? repair.is(net.minecraft.world.item.Items.AMETHYST_SHARD)
                : repair.is(net.minecraft.world.item.Items.BLAZE_ROD);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ashen_realms." + (kind == Kind.ARCANE ? "arcane_staff" : "sunfire_staff"))
                .withStyle(ChatFormatting.GOLD));
    }
}
