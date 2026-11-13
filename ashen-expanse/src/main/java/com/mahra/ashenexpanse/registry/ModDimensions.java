package com.mahra.ashenexpanse.registry;

import com.mahra.ashenexpanse.AshenExpanse;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class ModDimensions {
    public static final ResourceKey<Level> ASHLANDS =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(AshenExpanse.MOD_ID, "ashlands"));

    private ModDimensions() {}
}
