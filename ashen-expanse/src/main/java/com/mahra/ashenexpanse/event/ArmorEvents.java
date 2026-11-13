package com.mahra.ashenexpanse.event;

import com.mahra.ashenexpanse.AshenExpanse;
import com.mahra.ashenexpanse.registry.ModArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Full-set bonuses: Umbral = night vision + speed, Cinderite = fire immunity + burns attackers. */
@Mod.EventBusSubscriber(modid = AshenExpanse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ArmorEvents {
    private ArmorEvents() {}

    public static boolean wearsFullSet(LivingEntity entity, ArmorMaterial material) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (!(entity.getItemBySlot(slot).getItem() instanceof ArmorItem armor) || armor.getMaterial() != material) {
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide || player.tickCount % 20 != 0) return;
        if (wearsFullSet(player, ModArmorMaterials.UMBRAL)) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false, true));
        }
        if (wearsFullSet(player, ModArmorMaterials.CINDERITE)) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false, true));
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker
                && wearsFullSet(event.getEntity(), ModArmorMaterials.CINDERITE)) {
            attacker.setSecondsOnFire(4);
        }
    }
}
