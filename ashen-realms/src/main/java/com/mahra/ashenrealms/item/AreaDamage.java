package com.mahra.ashenrealms.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

/** Picks the living things a player's area ability should hit. */
public final class AreaDamage {
    private AreaDamage() {}

    public static List<LivingEntity> around(Player player, double radius) {
        AABB box = player.getBoundingBox().inflate(radius, 2.0, radius);
        return player.level().getEntitiesOfClass(LivingEntity.class, box, e ->
                e != player && e.isAlive()
                        && e.distanceToSqr(player) <= radius * radius
                        && !player.isAlliedTo(e)
                        && !(e instanceof ArmorStand stand && stand.isMarker())
                        && !(e instanceof TamableAnimal pet && pet.isOwnedBy(player)));
    }
}
