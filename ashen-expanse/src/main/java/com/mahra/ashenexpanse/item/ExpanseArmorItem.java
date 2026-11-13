package com.mahra.ashenexpanse.item;

import com.mahra.ashenexpanse.registry.ModArmorMaterials;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExpanseArmorItem extends ArmorItem {
    public ExpanseArmorItem(ArmorMaterial material, Type type, Properties props) {
        super(material, type, props);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        String key = getMaterial() == ModArmorMaterials.CINDERITE ? "tooltip.ashen_expanse.cinderite_set"
                : "tooltip.ashen_expanse.umbral_set";
        tooltip.add(Component.translatable("tooltip.ashen_expanse.set_bonus").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GOLD));
    }
}
