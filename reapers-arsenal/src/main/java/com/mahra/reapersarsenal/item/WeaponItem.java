package com.mahra.reapersarsenal.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.mahra.reapersarsenal.client.WeaponClientExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Extends SwordItem so all sword enchantments (Sharpness, Looting, Fire Aspect...) apply.
 */
public class WeaponItem extends SwordItem {
    private static final UUID REACH_UUID = UUID.fromString("7f3c1a52-9d4e-4b8a-a6c2-3e5b9f1d2c84");

    private final WeaponType type;
    private Multimap<Attribute, AttributeModifier> mainHandModifiers;

    public WeaponItem(Tier tier, WeaponType type, Properties properties) {
        super(tier, type.damageModifier, type.speedModifier, properties);
        this.type = type;
    }

    public WeaponType getWeaponType() {
        return type;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND || type.reachBonus <= 0) {
            return super.getDefaultAttributeModifiers(slot);
        }
        // Built lazily: the Forge reach attribute isn't registered yet when items are constructed.
        if (mainHandModifiers == null) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.putAll(super.getDefaultAttributeModifiers(slot));
            builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_UUID, "Weapon reach",
                    type.reachBonus, AttributeModifier.Operation.ADDITION));
            mainHandModifiers = builder.build();
        }
        return mainHandModifiers;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new WeaponClientExtensions(type));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        if (action == ToolActions.SWORD_SWEEP && !type.vanillaSweep) {
            return false;
        }
        return super.canPerformAction(stack, action);
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return type.disablesShields || super.canDisableShield(stack, shield, entity, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.reapers_arsenal." + type.id).withStyle(ChatFormatting.GOLD));
        if (type.disablesShields) {
            tooltip.add(Component.translatable("tooltip.reapers_arsenal.shield_breaker").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
