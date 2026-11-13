package com.mahra.ashenrealms.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

/** Fast, nearly gravity-free projectile with a particle trail. */
public abstract class MagicProjectile extends ThrowableItemProjectile {
    protected MagicProjectile(EntityType<? extends MagicProjectile> type, Level level) {
        super(type, level);
    }

    protected MagicProjectile(EntityType<? extends MagicProjectile> type, LivingEntity owner, Level level) {
        super(type, owner, level);
    }

    protected abstract ParticleOptions trailParticle();

    @Override
    protected float getGravity() {
        return 0.005F;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                level().addParticle(trailParticle(), getX() + (random.nextDouble() - 0.5) * 0.2,
                        getY() + (random.nextDouble() - 0.5) * 0.2, getZ() + (random.nextDouble() - 0.5) * 0.2,
                        0, 0, 0);
            }
        } else if (tickCount > 80) {
            discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }
}
