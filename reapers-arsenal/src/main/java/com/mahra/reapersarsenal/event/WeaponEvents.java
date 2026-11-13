package com.mahra.reapersarsenal.event;

import com.mahra.reapersarsenal.ReapersArsenal;
import com.mahra.reapersarsenal.effect.ModEffects;
import com.mahra.reapersarsenal.item.WeaponItem;
import com.mahra.reapersarsenal.item.WeaponType;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ReapersArsenal.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class WeaponEvents {
    /** Attack charge (0..1) captured before vanilla resets it, read back when the hit lands. */
    private static final Map<UUID, Float> CHARGE = new HashMap<>();
    /** Stops area hits (scythe reap, hammer shockwave) from chaining into more area hits. */
    private static boolean applyingAreaDamage = false;

    private WeaponEvents() {}

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (player.getMainHandItem().getItem() instanceof WeaponItem) {
            CHARGE.put(player.getUUID(), player.getAttackStrengthScale(0.5F));
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (applyingAreaDamage) return;
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player) || source.getDirectEntity() != player) return;
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof WeaponItem weapon)) return;
        if (!(player.level() instanceof ServerLevel level)) return;

        LivingEntity target = event.getEntity();
        Float stored = CHARGE.remove(player.getUUID());
        boolean fullCharge = stored == null || stored > 0.9F;
        float amount = event.getAmount();
        WeaponType type = weapon.getWeaponType();

        hitEffects(level, player, target, type);

        switch (type) {
            case DAGGER -> {
                if (isBehind(player, target)) {
                    event.setAmount(amount * 1.75F);
                    level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.6), target.getZ(),
                            12, 0.3, 0.3, 0.3, 0.2);
                    level.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.4F);
                }
            }
            case SPEAR -> {
                if (player.isSprinting()) {
                    event.setAmount(amount * 1.5F);
                    level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.6), target.getZ(),
                            8, 0.2, 0.2, 0.2, 0.3);
                }
            }
            case KATANA -> {
                if (fullCharge) {
                    target.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 80, 0), player);
                    level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(0.6),
                            target.getZ(), 4, 0.2, 0.2, 0.2, 0.05);
                }
            }
            case WARHAMMER -> {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1), player);
                if (fullCharge) shockwave(level, player, target, amount * 0.3F);
            }
            case SCYTHE -> {
                if (fullCharge) reap(level, player, target, amount * 0.75F);
            }
            default -> {}
        }
    }

    /** Small particle + sound flourish on every hit, so each weapon feels different. */
    private static void hitEffects(ServerLevel level, Player player, LivingEntity target, WeaponType type) {
        double x = target.getX(), y = target.getY(0.55), z = target.getZ();
        switch (type) {
            case KATANA -> {
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0, 0);
                level.playSound(null, x, y, z, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.6F, 1.6F);
            }
            case DAGGER -> {
                level.sendParticles(ParticleTypes.ENCHANTED_HIT, x, y, z, 6, 0.2, 0.2, 0.2, 0.1);
                level.playSound(null, x, y, z, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.5F, 1.8F);
            }
            case SPEAR -> level.sendParticles(ParticleTypes.CRIT, x, y, z, 6, 0.1, 0.1, 0.1, 0.4);
            case WARHAMMER -> {
                BlockState ground = level.getBlockState(target.blockPosition().below());
                if (!ground.isAir()) {
                    level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground),
                            x, target.getY() + 0.1, z, 18, 0.5, 0.05, 0.5, 0.15);
                }
                level.playSound(null, x, y, z, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.PLAYERS, 0.5F, 0.7F);
            }
            case SCYTHE, HALBERD -> level.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private static boolean isBehind(Player attacker, LivingEntity target) {
        Vec3 look = Vec3.directionFromRotation(0, target.getYHeadRot());
        Vec3 toAttacker = new Vec3(attacker.getX() - target.getX(), 0, attacker.getZ() - target.getZ());
        if (toAttacker.lengthSqr() < 1.0E-4) return false;
        return look.dot(toAttacker.normalize()) < -0.3;
    }

    private static boolean canAreaHit(Player player, LivingEntity primary, LivingEntity other) {
        if (other == player || other == primary || !other.isAlive()) return false;
        if (other instanceof ArmorStand stand && stand.isMarker()) return false;
        if (player.isAlliedTo(other)) return false;
        return !(other instanceof TamableAnimal pet && pet.isOwnedBy(player));
    }

    /** Scythe: a wide arc in front of the player hits everything caught in it. */
    private static void reap(ServerLevel level, Player player, LivingEntity primary, float damage) {
        double radius = 4.0;
        Vec3 facing = Vec3.directionFromRotation(0, player.getYRot());
        AABB box = player.getBoundingBox().inflate(radius, 1.0, radius);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> canAreaHit(player, primary, e));

        applyingAreaDamage = true;
        try {
            for (LivingEntity victim : victims) {
                Vec3 offset = new Vec3(victim.getX() - player.getX(), 0, victim.getZ() - player.getZ());
                if (offset.lengthSqr() > radius * radius) continue;
                if (offset.lengthSqr() > 1.0E-4 && facing.dot(offset.normalize()) < 0.2) continue; // outside arc
                victim.knockback(0.4, player.getX() - victim.getX(), player.getZ() - victim.getZ());
                victim.hurt(player.damageSources().playerAttack(player), damage);
            }
        } finally {
            applyingAreaDamage = false;
        }

        for (int i = -2; i <= 2; i++) {
            Vec3 dir = Vec3.directionFromRotation(0, player.getYRot() + i * 25);
            level.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + dir.x * 2.2, player.getY(0.6),
                    player.getZ() + dir.z * 2.2, 1, 0, 0, 0, 0);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.7F);
    }

    /** Warhammer: slam knocks back and damages everything near the target. */
    private static void shockwave(ServerLevel level, Player player, LivingEntity primary, float damage) {
        double radius = 3.0;
        AABB box = primary.getBoundingBox().inflate(radius, 1.0, radius);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> canAreaHit(player, primary, e));

        applyingAreaDamage = true;
        try {
            for (LivingEntity victim : victims) {
                if (victim.distanceToSqr(primary) > radius * radius) continue;
                victim.knockback(0.8, primary.getX() - victim.getX(), primary.getZ() - victim.getZ());
                victim.hurt(player.damageSources().playerAttack(player), damage);
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0), player);
            }
        } finally {
            applyingAreaDamage = false;
        }

        level.sendParticles(ParticleTypes.EXPLOSION, primary.getX(), primary.getY(), primary.getZ(),
                1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.POOF, primary.getX(), primary.getY() + 0.1, primary.getZ(),
                20, radius / 2, 0.1, radius / 2, 0.05);
        level.playSound(null, primary.getX(), primary.getY(), primary.getZ(),
                SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.PLAYERS, 0.8F, 0.6F);
    }
}
