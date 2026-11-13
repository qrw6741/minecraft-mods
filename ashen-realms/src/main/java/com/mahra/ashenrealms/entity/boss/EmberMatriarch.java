package com.mahra.ashenrealms.entity.boss;

import com.mahra.ashenrealms.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Boss 3 (Nether). A giant flying blaze queen: fireball volleys, meteor showers and blaze summons.
 * Below half health: bigger volleys and a burning shockwave.
 */
public class EmberMatriarch extends Blaze {
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private int volleyCooldown = 40;
    private int meteorCooldown = 160;
    private int summonCooldown = 200;
    private int burstCooldown = 100;
    private boolean enraged;

    public EmberMatriarch(EntityType<? extends EmberMatriarch> type, Level level) {
        super(type, level);
        this.xpReward = 350;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 350.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(4, new HoverNearTargetGoal());
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
        ServerLevel level = (ServerLevel) level();

        if (!enraged && getHealth() < getMaxHealth() / 2) {
            enraged = true;
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            playSound(SoundEvents.BLAZE_DEATH, 2.0F, 0.5F);
            BossHelper.announce(this, Component.translatable("boss.ashen_realms.ember_matriarch.enrage"));
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;

        // Hover a few blocks above the target.
        if (getY() < target.getY() + 3.5) {
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x, v.y + (0.35 - v.y) * 0.3, v.z);
            hasImpulse = true;
        }

        if (--volleyCooldown <= 0 && hasLineOfSight(target)) {
            volley(target, enraged ? 5 : 3);
            volleyCooldown = enraged ? 30 : 50;
        }
        if (--meteorCooldown <= 0) {
            meteors(target, enraged ? 10 : 6);
            meteorCooldown = enraged ? 160 : 240;
        }
        if (--summonCooldown <= 0) {
            summonBlazes(level, target);
            summonCooldown = 300;
        }
        if (enraged && --burstCooldown <= 0 && distanceToSqr(target) < 49) {
            BossHelper.areaAttack(this, 6.0, 7.0F, e -> e.setSecondsOnFire(6));
            for (int i = 0; i < 64; i++) {
                double a = i * Math.PI * 2 / 64;
                level.sendParticles(ParticleTypes.FLAME, getX() + Math.cos(a) * 5.5, getY() + 0.5,
                        getZ() + Math.sin(a) * 5.5, 2, 0.1, 0.2, 0.1, 0.02);
            }
            playSound(SoundEvents.GHAST_SHOOT, 2.0F, 0.5F);
            burstCooldown = 200;
        }
    }

    private void volley(LivingEntity target, int count) {
        double dx = target.getX() - getX();
        double dy = target.getY(0.5) - getY(0.5);
        double dz = target.getZ() - getZ();
        double spread = Math.sqrt(Math.sqrt(distanceToSqr(target))) * 0.4;
        for (int i = 0; i < count; i++) {
            SmallFireball fireball = new SmallFireball(level(), this,
                    dx + random.nextGaussian() * spread, dy, dz + random.nextGaussian() * spread);
            fireball.setPos(getX(), getY(0.5) + 0.5, getZ());
            level().addFreshEntity(fireball);
        }
        playSound(SoundEvents.BLAZE_SHOOT, 2.0F, 0.7F);
    }

    private void meteors(LivingEntity target, int count) {
        for (int i = 0; i < count; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 10;
            double z = target.getZ() + (random.nextDouble() - 0.5) * 10;
            SmallFireball meteor = new SmallFireball(level(), x, target.getY() + 16 + random.nextDouble() * 4, z,
                    random.nextGaussian() * 0.05, -1.0, random.nextGaussian() * 0.05);
            meteor.setOwner(this);
            level().addFreshEntity(meteor);
        }
        playSound(SoundEvents.BLAZE_AMBIENT, 2.0F, 0.5F);
    }

    private void summonBlazes(ServerLevel level, LivingEntity target) {
        int alive = level.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(24), BossHelper::isMinion).size();
        for (int i = 0; i < 2 && alive < 4; i++, alive++) {
            Blaze blaze = EntityType.BLAZE.create(level);
            if (blaze == null) continue;
            double angle = random.nextDouble() * Math.PI * 2;
            blaze.moveTo(getX() + Math.cos(angle) * 3, getY(), getZ() + Math.sin(angle) * 3, 0, 0);
            blaze.finalizeSpawn(level, level.getCurrentDifficultyAt(blaze.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            blaze.addTag(BossHelper.MINION_TAG);
            blaze.setTarget(target);
            level.addFreshEntity(blaze);
            level.sendParticles(ParticleTypes.LAVA, blaze.getX(), blaze.getY() + 1, blaze.getZ(), 10, 0.3, 0.5, 0.3, 0);
        }
    }

    /** Follows the target at range while the cooldown attacks above do the damage. */
    private class HoverNearTargetGoal extends Goal {
        HoverNearTargetGoal() {
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity t = getTarget();
            return t != null && t.isAlive() && canAttack(t);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity t = getTarget();
            if (t == null) return;
            getLookControl().setLookAt(t, 10.0F, 10.0F);
            double distSq = distanceToSqr(t);
            if (distSq > 10 * 10 || !hasLineOfSight(t)) {
                getMoveControl().setWantedPosition(t.getX(), t.getY() + 4, t.getZ(), 1.0);
            } else if (distSq < 5 * 5) {
                Vec3 away = position().subtract(t.position()).normalize().scale(6);
                getMoveControl().setWantedPosition(getX() + away.x, getY(), getZ() + away.z, 1.0);
            } else {
                getNavigation().stop();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && BossHelper.isMinion(source.getEntity())) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.SUNFIRE_STAFF.get()));
        spawnAtLocation(new ItemStack(ModItems.EMBER_SOUL.get()));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", enraged);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        enraged = tag.getBoolean("Enraged");
        if (enraged) bossEvent.setColor(BossEvent.BossBarColor.RED);
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }
}
