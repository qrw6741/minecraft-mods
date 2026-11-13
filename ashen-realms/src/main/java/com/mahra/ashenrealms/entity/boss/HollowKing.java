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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Boss 1. Melee brute that summons skeleton minions and slams the ground.
 * Below half health: faster, summons wither skeletons and fires wither skulls.
 */
public class HollowKing extends Monster {
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private int summonCooldown = 120;
    private int slamCooldown = 80;
    private int skullCooldown = 60;
    private boolean enraged;

    public HollowKing(EntityType<? extends HollowKing> type, Level level) {
        super(type, level);
        this.xpReward = 300;
        setPersistenceRequired();
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.KINGSBANE.get()));
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());

        if (!enraged && getHealth() < getMaxHealth() / 2) {
            enraged = true;
            bossEvent.setColor(BossEvent.BossBarColor.RED);
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.36);
            playSound(SoundEvents.WITHER_AMBIENT, 2.0F, 0.6F);
            BossHelper.announce(this, Component.translatable("boss.ashen_realms.hollow_king.enrage"));
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;

        if (--summonCooldown <= 0) {
            summonMinions(target);
            summonCooldown = enraged ? 200 : 300;
        }
        if (--slamCooldown <= 0 && distanceToSqr(target) < 36) {
            slam();
            slamCooldown = enraged ? 100 : 160;
        }
        if (enraged && --skullCooldown <= 0 && hasLineOfSight(target)) {
            shootSkull(target);
            skullCooldown = 60;
        }
    }

    private void summonMinions(LivingEntity target) {
        ServerLevel level = (ServerLevel) level();
        int alive = level.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(24), BossHelper::isMinion).size();
        EntityType<? extends Mob> type = enraged ? EntityType.WITHER_SKELETON : EntityType.SKELETON;
        for (int i = 0; i < 3 && alive < 6; i++, alive++) {
            Mob minion = type.create(level);
            if (minion == null) continue;
            double angle = random.nextDouble() * Math.PI * 2;
            minion.moveTo(getX() + Math.cos(angle) * 3, getY(), getZ() + Math.sin(angle) * 3, random.nextFloat() * 360F, 0);
            minion.finalizeSpawn(level, level.getCurrentDifficultyAt(minion.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            minion.addTag(BossHelper.MINION_TAG);
            minion.setTarget(target);
            level.addFreshEntity(minion);
            level.sendParticles(ParticleTypes.SOUL, minion.getX(), minion.getY() + 1, minion.getZ(), 15, 0.3, 0.6, 0.3, 0.02);
        }
        swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 2.0F, 0.6F);
    }

    private void slam() {
        ServerLevel level = (ServerLevel) level();
        swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        BossHelper.areaAttack(this, 5.0, enraged ? 12.0F : 9.0F,
                e -> e.setDeltaMovement(e.getDeltaMovement().add(0, 0.8, 0)));
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2, getZ(), 4, 2.0, 0.1, 2.0, 0);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY() + 0.2, getZ(), 40, 3.0, 0.1, 3.0, 0.05);
        playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.7F);
    }

    private void shootSkull(LivingEntity target) {
        double dx = target.getX() - getX();
        double dy = target.getY(0.5) - getEyeY();
        double dz = target.getZ() - getZ();
        WitherSkull skull = new WitherSkull(level(), this, dx, dy, dz);
        skull.setPos(getX(), getEyeY(), getZ());
        level().addFreshEntity(skull);
        playSound(SoundEvents.WITHER_SHOOT, 1.0F, 0.8F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && BossHelper.isMinion(source.getEntity())) return false;
        if (source.getEntity() == this) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != MobEffects.WITHER && super.canBeAffected(effect);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.KINGSBANE.get()));
        spawnAtLocation(new ItemStack(ModItems.ROYAL_SOUL.get()));
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
