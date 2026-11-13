package com.mahra.ashenrealms.entity.projectile;

import com.mahra.ashenrealms.registry.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/** Sets enemies on fire but never sets blocks on fire, so it's safe to use at home. */
public class SunfireBolt extends MagicProjectile {
    public SunfireBolt(EntityType<? extends SunfireBolt> type, Level level) {
        super(type, level);
    }

    public SunfireBolt(Level level, LivingEntity owner) {
        super(ModEntities.SUNFIRE_BOLT.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }

    @Override
    protected ParticleOptions trailParticle() {
        return ParticleTypes.FLAME;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (target.hurt(damageSources().thrown(this, getOwner()), 6.0F)) {
            target.setSecondsOnFire(5);
        }
    }
}
