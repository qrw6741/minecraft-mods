package com.mahra.reapersarsenal.item;

import com.mahra.reapersarsenal.ReapersArsenal;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ReapersArsenal.MOD_ID);

    /** Ordered type-by-type so the creative tab groups each weapon family together. */
    public static final Map<WeaponType, Map<WeaponMaterial, RegistryObject<Item>>> WEAPONS = new LinkedHashMap<>();

    static {
        for (WeaponType type : WeaponType.values()) {
            Map<WeaponMaterial, RegistryObject<Item>> byMaterial = new EnumMap<>(WeaponMaterial.class);
            for (WeaponMaterial material : WeaponMaterial.values()) {
                String name = material.prefix + "_" + type.id;
                byMaterial.put(material, ITEMS.register(name, () -> {
                    Item.Properties props = new Item.Properties();
                    if (material.fireResistant) props.fireResistant();
                    return new WeaponItem(material.tier, type, props);
                }));
            }
            WEAPONS.put(type, byMaterial);
        }
    }

    public static Item get(WeaponType type, WeaponMaterial material) {
        return WEAPONS.get(type).get(material).get();
    }

    private ModItems() {}
}
