package com.mahra.ashenrealms.entity.boss;

import com.mahra.ashenrealms.entity.projectile.IceShard;
import com.mahra.ashenrealms.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Boss 2. Built on the iron golem body (same model and smash animation), but hostile.
 * Throws ice shards and unleashes frost novas. Below half health it gains a slowing blizzard aura.
 */
public class FrostColossus extends IronGolem implements Enemy {
    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(),
            BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10).setDarkenScreen(true);
    private int shardCooldown = 60;
    private int novaCooldown = 120;
    private int auraTick = 0;
    private boolean enraged;

    public FrostColossus(EntityType<? extends FrostColossus> type, Level level) {
        super(type, level);
        this.xpReward = 400;
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
        goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
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
            bossEvent.setColor(BossEvent.BossBarColor.WHITE);
            playSound(SoundEvents.ELDER_GUARDIAN_CURSE, 1.5F, 0.6F);
            BossHelper.announce(this, Component.translatable("boss.ashen_realms.frost_colossus.enrage"));
        }

        if (enraged && ++auraTick % 20 == 0) {
            for (Player p : level.players()) {
                if (!p.isCreative() && !p.isSpectator() && p.distanceToSqr(this) < 14 * 14) {
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0), this);
                    p.setTicksFrozen(Math.min(p.getTicksFrozen() + 30, 200));
                }
            }
            level.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getY() + 2, getZ(), 40, 6, 2, 6, 0.02);
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;

        if (--shardCooldown <= 0 && hasLineOfSight(target)) {
            throwShards(target, enraged ? 5 : 3);
            shardCooldown = enraged ? 50 : 80;
        }
        if (--novaCooldown <= 0 && distanceToSqr(target) < 64) {
            frostNova(level);
            novaCooldown = enraged ? 140 : 200;
        }
    }

    private void throwShards(LivingEntity target, int count) {
        for (int i = 0; i < count; i++) {
            IceShard shard = new IceShard(level(), this);
            shard.setPos(getX(), getEyeY() - 0.6, getZ());
            double dx = target.getX() - shard.getX();
            double dy = target.getY(0.5) - shard.getY();
            double dz = target.getZ() - shard.getZ();
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            shard.shoot(dx, dy + horizontal * 0.05, dz, 1.5F, 4.0F + i * 3.0F);
            level().addFreshEntity(shard);
        }
        playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.5F, 0.5F);
    }

    private void frostNova(ServerLevel level) {
        BossHelper.areaAttack(this, 7.0, 7.0F, e -> {
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2), this);
            e.setTicksFrozen(Math.max(e.getTicksFrozen(), 200));
            double dx = e.getX() - getX(), dz = e.getZ() - getZ();
            double len = Math.max(Math.sqrt(dx * dx + dz * dz), 0.1);
            e.setDeltaMovement(e.getDeltaMovement().add(dx / len * 0.9, 0.35, dz / len * 0.9));
        });
        for (int i = 0; i < 64; i++) {
            double a = i * Math.PI * 2 / 64;
            level.sendParticles(ParticleTypes.SNOWFLAKE, getX() + Math.cos(a) * 6.5, getY() + 0.4,
                    getZ() + Math.sin(a) * 6.5, 2, 0.1, 0.2, 0.1, 0.01);
        }
        level.sendParticles(ParticleTypes.ITEM_SNOWBALL, getX(), getY() + 1, getZ(), 60, 3, 1, 3, 0.1);
        playSound(SoundEvents.GLASS_BREAK, 2.0F, 0.5F);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS; // no healing it with iron ingots
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        return true;
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
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.FROSTFANG.get()));
        spawnAtLocation(new ItemStack(ModItems.FROST_SOUL.get()));
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
        if (enraged) bossEvent.setColor(BossEvent.BossBarColor.WHITE);
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }
}
