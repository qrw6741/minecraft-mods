package com.mahra.ashenexpanse.registry;

import com.mahra.ashenexpanse.AshenExpanse;
import com.mahra.ashenexpanse.item.AshenWaystoneItem;
import com.mahra.ashenexpanse.item.ExpanseArmorItem;
import com.mahra.ashenexpanse.item.ExpanseWeaponItem;
import com.mahra.ashenexpanse.item.TyrantSigilItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AshenExpanse.MOD_ID);
    /** Creative-tab order. */
    public static final List<RegistryObject<Item>> TAB_ORDER = new ArrayList<>();

    private static RegistryObject<Item> reg(String name, Supplier<Item> sup) {
        RegistryObject<Item> obj = ITEMS.register(name, sup);
        TAB_ORDER.add(obj);
        return obj;
    }

    private static RegistryObject<Item> block(RegistryObject<Block> block) {
        return reg(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Blocks
    public static final RegistryObject<Item> ASHSTONE = block(ModBlocks.ASHSTONE);
    public static final RegistryObject<Item> ASH_BLOCK = block(ModBlocks.ASH_BLOCK);
    public static final RegistryObject<Item> ASHSTONE_BRICKS = block(ModBlocks.ASHSTONE_BRICKS);
    public static final RegistryObject<Item> UMBRAL_ORE = block(ModBlocks.UMBRAL_ORE);
    public static final RegistryObject<Item> DEEPSLATE_UMBRAL_ORE = block(ModBlocks.DEEPSLATE_UMBRAL_ORE);
    public static final RegistryObject<Item> CINDERITE_ORE = block(ModBlocks.CINDERITE_ORE);
    public static final RegistryObject<Item> UMBRAL_BLOCK = block(ModBlocks.UMBRAL_BLOCK);
    public static final RegistryObject<Item> CINDERITE_BLOCK = block(ModBlocks.CINDERITE_BLOCK);

    // Materials
    public static final RegistryObject<Item> RAW_UMBRAL = reg("raw_umbral", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_INGOT = reg("umbral_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CINDERITE_SHARD = reg("cinderite_shard",
            () -> new Item(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON)));

    // Umbral gear
    public static final RegistryObject<Item> UMBRAL_SWORD = reg("umbral_sword",
            () -> new ExpanseWeaponItem(ModTiers.UMBRAL, 3, -2.4F, ExpanseWeaponItem.Effect.NONE, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_PICKAXE = reg("umbral_pickaxe",
            () -> new PickaxeItem(ModTiers.UMBRAL, 1, -2.8F, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_AXE = reg("umbral_axe",
            () -> new AxeItem(ModTiers.UMBRAL, 6.0F, -3.1F, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_SHOVEL = reg("umbral_shovel",
            () -> new ShovelItem(ModTiers.UMBRAL, 1.5F, -3.0F, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_HELMET = reg("umbral_helmet",
            () -> new ExpanseArmorItem(ModArmorMaterials.UMBRAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_CHESTPLATE = reg("umbral_chestplate",
            () -> new ExpanseArmorItem(ModArmorMaterials.UMBRAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_LEGGINGS = reg("umbral_leggings",
            () -> new ExpanseArmorItem(ModArmorMaterials.UMBRAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> UMBRAL_BOOTS = reg("umbral_boots",
            () -> new ExpanseArmorItem(ModArmorMaterials.UMBRAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // Cinderite gear
    public static final RegistryObject<Item> CINDERITE_SWORD = reg("cinderite_sword",
            () -> new ExpanseWeaponItem(ModTiers.CINDERITE, 3, -2.4F, ExpanseWeaponItem.Effect.BURN,
                    new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_PICKAXE = reg("cinderite_pickaxe",
            () -> new PickaxeItem(ModTiers.CINDERITE, 1, -2.8F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_AXE = reg("cinderite_axe",
            () -> new AxeItem(ModTiers.CINDERITE, 5.0F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_SHOVEL = reg("cinderite_shovel",
            () -> new ShovelItem(ModTiers.CINDERITE, 1.5F, -3.0F, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_HELMET = reg("cinderite_helmet",
            () -> new ExpanseArmorItem(ModArmorMaterials.CINDERITE, ArmorItem.Type.HELMET, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_CHESTPLATE = reg("cinderite_chestplate",
            () -> new ExpanseArmorItem(ModArmorMaterials.CINDERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_LEGGINGS = reg("cinderite_leggings",
            () -> new ExpanseArmorItem(ModArmorMaterials.CINDERITE, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CINDERITE_BOOTS = reg("cinderite_boots",
            () -> new ExpanseArmorItem(ModArmorMaterials.CINDERITE, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant()));

    // Special weapons
    public static final RegistryObject<Item> MAGMA_MAUL = reg("magma_maul",
            () -> new ExpanseWeaponItem(ModTiers.CINDERITE, 5, -3.2F, ExpanseWeaponItem.Effect.MAUL,
                    new Item.Properties().fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ASHBRINGER = reg("ashbringer",
            () -> new ExpanseWeaponItem(ModTiers.CINDERITE, 7, -2.6F, ExpanseWeaponItem.Effect.ASHBRINGER,
                    new Item.Properties().fireResistant().rarity(Rarity.EPIC)));

    // Travel & boss
    public static final RegistryObject<Item> ASHEN_WAYSTONE = reg("ashen_waystone",
            () -> new AshenWaystoneItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> TYRANT_SIGIL = reg("tyrant_sigil",
            () -> new TyrantSigilItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    // Spawn eggs
    public static final RegistryObject<Item> ASH_STALKER_SPAWN_EGG = reg("ash_stalker_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ASH_STALKER, 0x4A4642, 0xFF8A1E, new Item.Properties()));
    public static final RegistryObject<Item> CINDER_CRAWLER_SPAWN_EGG = reg("cinder_crawler_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CINDER_CRAWLER, 0x2A1E18, 0xE0521A, new Item.Properties()));
    public static final RegistryObject<Item> MAGMA_BRUTE_SPAWN_EGG = reg("magma_brute_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MAGMA_BRUTE, 0x3A2A24, 0xFFB030, new Item.Properties()));
    public static final RegistryObject<Item> SOUL_WISP_SPAWN_EGG = reg("soul_wisp_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SOUL_WISP, 0x9AF0F5, 0x2A7A8C, new Item.Properties()));
    public static final RegistryObject<Item> ASHEN_TYRANT_SPAWN_EGG = reg("ashen_tyrant_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ASHEN_TYRANT, 0x1C1A1E, 0xFF5A10, new Item.Properties()));

    private ModItems() {}
}
