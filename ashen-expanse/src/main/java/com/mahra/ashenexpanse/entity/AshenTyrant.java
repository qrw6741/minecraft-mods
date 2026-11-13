package com.mahra.ashenexpanse.entity;

import com.mahra.ashenexpanse.registry.ModEntities;
import com.mahra.ashenexpanse.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Boss of the Ashlands. Calls down flame pillars, leaps and slams, and summons Ash Stalkers.
 * Below half health: faster, and burns everything near him.
 */
public class AshenTyrant extends Monster {
    private static final String MINION_TAG = "ashen_expanse_minion";

    private final ServerBossEvent bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_12).setDarkenScreen(true);
    private final List<long[]> pendingPillars = new ArrayList<>(); // {x, y, z, triggerTick}
    private int pillarCooldown = 60;
    private int leapCooldown = 120;
    private int summonCooldown = 200;
    private boolean leaping;
    private boolean enraged;

    public AshenTyrant(EntityType<? extends AshenTyrant> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        setPersistenceRequired();
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ASHBRINGER.get()));
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, true));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 20.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        ServerLevel level = (ServerLevel) level();
        bossEvent.setProgress(getHealth() / getMaxHealth());

        if (!enraged && getHealth() < getMaxHealth() / 2) {
            enraged = true;
            getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.38);
            playSound(SoundEvents.WITHER_SPAWN, 1.0F, 0.7F);
            announce(Component.translatable("boss.ashen_expanse.ashen_tyrant.enrage"));
        }

        tickPillars(level);

        if (leaping && onGround()) {
            leaping = false;
            slam(level);
        }

        if (enraged && tickCount % 20 == 0) {
            hitAround(4.0, 4.0F, e -> e.setSecondsOnFire(4));
            level.sendParticles(ParticleTypes.FLAME, getX(), getY() + 1, getZ(), 30, 2, 1, 2, 0.02);
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) return;

        if (--pillarCooldown <= 0) {
            int count = enraged ? 5 : 3;
            for (int i = 0; i < count; i++) {
                double ox = i == 0 ? 0 : (random.nextDouble() - 0.5) * 8;
                double oz = i == 0 ? 0 : (random.nextDouble() - 0.5) * 8;
                pendingPillars.add(new long[]{(long) Math.floor(target.getX() + ox), (long) Math.floor(target.getY()),
                        (long) Math.floor(target.getZ() + oz), tickCount + 25});
            }
            pillarCooldown = enraged ? 70 : 110;
        }
        if (--leapCooldown <= 0 && onGround() && distanceToSqr(target) > 25 && distanceToSqr(target) < 400) {
            Vec3 dir = target.position().subtract(position());
            setDeltaMovement(dir.x * 0.18, 1.0, dir.z * 0.18);
            hasImpulse = true;
            leaping = true;
            playSound(SoundEvents.RAVAGER_ROAR, 1.5F, 0.6F);
            leapCooldown = enraged ? 120 : 180;
        }
        if (--summonCooldown <= 0) {
            summonStalkers(level, target);
            summonCooldown = 320;
        }
    }

    private void tickPillars(ServerLevel level) {
        pendingPillars.removeIf(p -> {
            double x = p[0] + 0.5, y = p[1], z = p[2] + 0.5;
            if (tickCount < p[3]) {
                level.sendParticles(ParticleTypes.SMOKE, x, y + 0.1, z, 3, 0.4, 0, 0.4, 0.01);
                return false;
            }
            level.sendParticles(ParticleTypes.FLAME, x, y + 1.5, z, 40, 0.3, 1.5, 0.3, 0.05);
            level.sendParticles(ParticleTypes.LAVA, x, y + 0.5, z, 6, 0.3, 0.3, 0.3, 0);
            level.playSound(null, new BlockPos((int) p[0], (int) p[1], (int) p[2]), SoundEvents.FIRECHARGE_USE,
                    net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 0.7F);
            AABB box = new AABB(x - 1.3, y - 0.5, z - 1.3, x + 1.3, y + 3.5, z + 1.3);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, this::isValidVictim)) {
                e.hurt(damageSources().mobAttack(this), enraged ? 10.0F : 8.0F);
                e.setSecondsOnFire(5);
                e.setDeltaMovement(e.getDeltaMovement().add(0, 0.6, 0));
                e.hurtMarked = true;
            }
            return true;
        });
    }

    private void slam(ServerLevel level) {
        hitAround(6.0, enraged ? 14.0F : 10.0F, e -> {
            double dx = e.getX() - getX(), dz = e.getZ() - getZ();
            double len = Math.max(Math.sqrt(dx * dx + dz * dz), 0.1);
            e.setDeltaMovement(e.getDeltaMovement().add(dx / len, 0.6, dz / len));
        });
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2, getZ(), 6, 2.5, 0.1, 2.5, 0);
        level.sendParticles(ParticleTypes.LAVA, getX(), getY() + 0.2, getZ(), 20, 3, 0.1, 3, 0);
        playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 0.6F);
        swing(InteractionHand.MAIN_HAND);
    }

    private void summonStalkers(ServerLevel level, LivingEntity target) {
        int alive = level.getEntitiesOfClass(Monster.class, getBoundingBox().inflate(24),
                e -> e.getTags().contains(MINION_TAG)).size();
        for (int i = 0; i < 2 && alive < 4; i++, alive++) {
            AshStalker stalker = ModEntities.ASH_STALKER.get().create(level);
            if (stalker == null) continue;
            double a = random.nextDouble() * Math.PI * 2;
            stalker.moveTo(getX() + Math.cos(a) * 3, getY(), getZ() + Math.sin(a) * 3, 0, 0);
            stalker.finalizeSpawn(level, level.getCurrentDifficultyAt(stalker.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            stalker.addTag(MINION_TAG);
            stalker.setTarget(target);
            level.addFreshEntity(stalker);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, stalker.getX(), stalker.getY() + 1, stalker.getZ(), 20, 0.3, 0.8, 0.3, 0.02);
        }
        playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.5F, 0.5F);
    }

    private boolean isValidVictim(LivingEntity e) {
        if (e == this || !e.isAlive() || e.getTags().contains(MINION_TAG)) return false;
        if (e instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
        return e == getTarget() || !(e instanceof Enemy);
    }

    private void hitAround(double radius, float damage, java.util.function.Consumer<LivingEntity> extra) {
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius, 2, radius),
                e -> isValidVictim(e) && e.distanceToSqr(this) <= radius * radius)) {
            e.hurt(damageSources().mobAttack(this), damage);
            extra.accept(e);
            e.hurtMarked = true;
        }
    }

    private void announce(Component msg) {
        for (Player p : level().players()) {
            if (p instanceof ServerPlayer sp && sp.distanceToSqr(this) < 64 * 64) sp.sendSystemMessage(msg);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker == this || (attacker != null && attacker.getTags().contains(MINION_TAG))) return false;
        return super.hurt(source, amount);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        spawnAtLocation(new ItemStack(ModItems.ASHBRINGER.get()));
        spawnAtLocation(new ItemStack(ModItems.CINDERITE_SHARD.get(), 8 + random.nextInt(5)));
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
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }
}
