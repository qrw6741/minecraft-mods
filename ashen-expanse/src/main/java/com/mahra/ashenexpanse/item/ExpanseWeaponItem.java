package com.mahra.ashenexpanse.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExpanseWeaponItem extends SwordItem {
    public enum Effect { NONE, BURN, MAUL, ASHBRINGER }

    private final Effect effect;

    public ExpanseWeaponItem(Tier tier, int damage, float speed, Effect effect, Properties props) {
        super(tier, damage, speed, props);
        this.effect = effect;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        switch (effect) {
            case BURN -> target.setSecondsOnFire(3);
            case MAUL -> {
                target.setSecondsOnFire(4);
                Vec3 push = target.position().subtract(attacker.position()).normalize();
                target.setDeltaMovement(target.getDeltaMovement().add(push.x * 0.6, 0.45, push.z * 0.6));
                target.hurtMarked = true;
            }
            case ASHBRINGER -> target.setSecondsOnFire(6);
            default -> {}
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (effect != Effect.ASHBRINGER) return super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            flameWave(server, player);
            stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(hand));
        }
        player.getCooldowns().addCooldown(this, 160);
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /** A cone of fire 8 blocks in front of the player. */
    private static void flameWave(ServerLevel level, Player player) {
        Vec3 look = player.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z).normalize();
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(8.0, 3.0, 8.0), e -> {
                    if (e == player || !e.isAlive() || player.isAlliedTo(e)) return false;
                    if (e instanceof ArmorStand || (e instanceof TamableAnimal pet && pet.isOwnedBy(player))) return false;
                    Vec3 to = new Vec3(e.getX() - player.getX(), 0, e.getZ() - player.getZ());
                    return to.lengthSqr() < 64 && (to.lengthSqr() < 1 || flat.dot(to.normalize()) > 0.5);
                });
        for (LivingEntity e : targets) {
            e.hurt(player.damageSources().playerAttack(player), 12.0F);
            e.setSecondsOnFire(8);
        }
        for (int d = 1; d <= 8; d++) {
            double spread = d * 0.35;
            level.sendParticles(ParticleTypes.FLAME, player.getX() + flat.x * d, player.getY() + 0.8,
                    player.getZ() + flat.z * d, 12, spread, 0.4, spread, 0.02);
            level.sendParticles(ParticleTypes.LAVA, player.getX() + flat.x * d, player.getY() + 0.5,
                    player.getZ() + flat.z * d, 2, spread, 0.2, spread, 0);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.2F, 0.6F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.8F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        switch (effect) {
            case BURN -> tooltip.add(Component.translatable("tooltip.ashen_expanse.burn").withStyle(ChatFormatting.GOLD));
            case MAUL -> tooltip.add(Component.translatable("tooltip.ashen_expanse.maul").withStyle(ChatFormatting.GOLD));
            case ASHBRINGER -> {
                tooltip.add(Component.translatable("tooltip.ashen_expanse.ashbringer").withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable("tooltip.ashen_expanse.ashbringer.active").withStyle(ChatFormatting.AQUA));
            }
            default -> {}
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
