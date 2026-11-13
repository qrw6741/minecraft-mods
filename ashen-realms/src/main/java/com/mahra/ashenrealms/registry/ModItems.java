package com.mahra.ashenrealms.registry;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.item.RpgSwordItem;
import com.mahra.ashenrealms.item.RpgSwordItem.Ability;
import com.mahra.ashenrealms.item.RpgSwordItem.Swing;
import com.mahra.ashenrealms.item.SoulItem;
import com.mahra.ashenrealms.item.StaffItem;
import com.mahra.ashenrealms.item.SummonItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AshenRealms.MOD_ID);

    // Craftable weapons
    public static final RegistryObject<Item> KNIGHTS_LONGSWORD = ITEMS.register("knights_longsword",
            () -> new RpgSwordItem(Tiers.IRON, 4, -2.6F, Ability.NONE, Swing.SLASH, new Item.Properties()));
    public static final RegistryObject<Item> BONE_CLEAVER = ITEMS.register("bone_cleaver",
            () -> new RpgSwordItem(Tiers.IRON, 5, -3.0F, Ability.WEAKEN, Swing.SLAM, new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_STAFF = ITEMS.register("arcane_staff",
            () -> new StaffItem(StaffItem.Kind.ARCANE, new Item.Properties().durability(250).rarity(Rarity.UNCOMMON)));

    // Boss drops
    public static final RegistryObject<Item> KINGSBANE = ITEMS.register("kingsbane",
            () -> new RpgSwordItem(Tiers.NETHERITE, 6, -2.8F, Ability.ROYAL, Swing.SLAM,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> FROSTFANG = ITEMS.register("frostfang",
            () -> new RpgSwordItem(Tiers.NETHERITE, 5, -2.4F, Ability.FROST, Swing.SLASH,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> SUNFIRE_STAFF = ITEMS.register("sunfire_staff",
            () -> new StaffItem(StaffItem.Kind.SUNFIRE, new Item.Properties().durability(500).rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ECLIPSE_BLADE = ITEMS.register("eclipse_blade",
            () -> new RpgSwordItem(Tiers.NETHERITE, 8, -2.4F, Ability.ECLIPSE, Swing.SLAM,
                    new Item.Properties().rarity(Rarity.EPIC).fireResistant()));

    // Boss souls (progression)
    public static final RegistryObject<Item> ROYAL_SOUL = ITEMS.register("royal_soul",
            () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> FROST_SOUL = ITEMS.register("frost_soul",
            () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));
    public static final RegistryObject<Item> EMBER_SOUL = ITEMS.register("ember_soul",
            () -> new SoulItem(new Item.Properties().rarity(Rarity.RARE).fireResistant()));

    // Summoning items
    public static final RegistryObject<Item> CURSED_CROWN = ITEMS.register("cursed_crown",
            () -> new SummonItem(ModEntities.HOLLOW_KING, null, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> FROZEN_EFFIGY = ITEMS.register("frozen_effigy",
            () -> new SummonItem(ModEntities.FROST_COLOSSUS, null, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> EMBER_TOTEM = ITEMS.register("ember_totem",
            () -> new SummonItem(ModEntities.EMBER_MATRIARCH, Level.NETHER, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    // Spawn eggs (creative)
    public static final RegistryObject<Item> HOLLOW_KING_SPAWN_EGG = ITEMS.register("hollow_king_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.HOLLOW_KING, 0xD8D2BF, 0x7B2FBE, new Item.Properties()));
    public static final RegistryObject<Item> FROST_COLOSSUS_SPAWN_EGG = ITEMS.register("frost_colossus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FROST_COLOSSUS, 0xBFE8F5, 0x2D7FB8, new Item.Properties()));
    public static final RegistryObject<Item> EMBER_MATRIARCH_SPAWN_EGG = ITEMS.register("ember_matriarch_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.EMBER_MATRIARCH, 0x2A1A14, 0xFF8A1E, new Item.Properties()));

    // Projectile visuals only
    public static final RegistryObject<Item> ICE_SHARD = ITEMS.register("ice_shard", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ARCANE_BOLT = ITEMS.register("arcane_bolt", () -> new Item(new Item.Properties()));

    private ModItems() {}
}
