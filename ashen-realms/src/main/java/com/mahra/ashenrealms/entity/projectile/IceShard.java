package com.mahra.ashenrealms.entity.projectile;

import com.mahra.ashenrealms.registry.ModEntities;
import com.mahra.ashenrealms.registry.ModItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class IceShard extends MagicProjectile {
    public IceShard(EntityType<? extends IceShard> type, Level level) {
        super(type, level);
    }

    public IceShard(Level level, LivingEntity owner) {
        super(ModEntities.ICE_SHARD.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ICE_SHARD.get();
    }

    @Override
    protected ParticleOptions trailParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() instanceof LivingEntity living
                && living.hurt(damageSources().thrown(this, getOwner()), 6.0F)) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            living.setTicksFrozen(Math.max(living.getTicksFrozen(), 140));
        }
    }
}
