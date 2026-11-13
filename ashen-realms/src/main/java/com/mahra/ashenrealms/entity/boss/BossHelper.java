package com.mahra.ashenrealms.entity.boss;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Consumer;

public final class BossHelper {
    public static final String MINION_TAG = "ashen_realms_minion";

    private BossHelper() {}

    public static boolean isMinion(Entity entity) {
        return entity.getTags().contains(MINION_TAG);
    }

    /** Sends a chat line to every player within 64 blocks of the boss. */
    public static void announce(Entity boss, Component message) {
        for (Player p : boss.level().players()) {
            if (p instanceof ServerPlayer sp && sp.distanceToSqr(boss) < 64 * 64) {
                sp.sendSystemMessage(message.copy().withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD));
            }
        }
    }

    /** Hurts players, pets and other non-monsters around the boss (never its own minions or other monsters). */
    public static void areaAttack(Mob boss, double radius, float damage, Consumer<LivingEntity> extra) {
        AABB box = boss.getBoundingBox().inflate(radius, 2.0, radius);
        List<LivingEntity> victims = boss.level().getEntitiesOfClass(LivingEntity.class, box, e ->
                e != boss && e.isAlive() && !isMinion(e)
                        && (e == boss.getTarget() || !(e instanceof Enemy))
                        && !(e instanceof Player p && (p.isCreative() || p.isSpectator()))
                        && e.distanceToSqr(boss) <= (radius + boss.getBbWidth() / 2) * (radius + boss.getBbWidth() / 2));
        for (LivingEntity e : victims) {
            e.hurt(boss.damageSources().mobAttack(boss), damage);
            extra.accept(e);
            e.hurtMarked = true;
        }
    }
}
