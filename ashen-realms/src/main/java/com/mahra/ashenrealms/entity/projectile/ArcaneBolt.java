package com.mahra.ashenrealms.entity.projectile;

import com.mahra.ashenrealms.registry.ModEntities;
import com.mahra.ashenrealms.registry.ModItems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ArcaneBolt extends MagicProjectile {
    public ArcaneBolt(EntityType<? extends ArcaneBolt> type, Level level) {
        super(type, level);
    }

    public ArcaneBolt(Level level, LivingEntity owner) {
        super(ModEntities.ARCANE_BOLT.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ARCANE_BOLT.get();
    }

    @Override
    protected ParticleOptions trailParticle() {
        return ParticleTypes.WITCH;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(damageSources().indirectMagic(this, getOwner()), 7.0F);
    }
}
