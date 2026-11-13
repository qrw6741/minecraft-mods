package com.mahra.ashenexpanse.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Hovering soul spirit that flings fireballs (uses blaze behaviour). */
public class SoulWisp extends Blaze {
    public SoulWisp(EntityType<? extends SoulWisp> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide && random.nextInt(2) == 0) {
            level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getRandomX(0.4), getRandomY(), getRandomZ(0.4), 0, -0.02, 0);
        }
    }
}
