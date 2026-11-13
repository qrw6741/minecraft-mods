package com.mahra.ashenexpanse.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public final class ModTiers {
    /** Between iron and diamond. Mines iron-level blocks. */
    public static final Tier UMBRAL = new ForgeTier(2, 750, 7.0F, 2.5F, 18,
            BlockTags.NEEDS_IRON_TOOL, () -> Ingredient.of(ModItems.UMBRAL_INGOT.get()));
    /** Netherite-level. Mines everything, including cinderite. */
    public static final Tier CINDERITE = new ForgeTier(4, 2500, 10.0F, 5.0F, 18,
            BlockTags.NEEDS_DIAMOND_TOOL, () -> Ingredient.of(ModItems.CINDERITE_SHARD.get()));

    private ModTiers() {}
}
