package com.mahra.reapersarsenal.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public enum WeaponMaterial {
    IRON("iron", Tiers.IRON, false),
    GOLD("golden", Tiers.GOLD, false),
    DIAMOND("diamond", Tiers.DIAMOND, false),
    NETHERITE("netherite", Tiers.NETHERITE, true);

    public final String prefix;
    public final Tier tier;
    public final boolean fireResistant;

    WeaponMaterial(String prefix, Tier tier, boolean fireResistant) {
        this.prefix = prefix;
        this.tier = tier;
        this.fireResistant = fireResistant;
    }
}
