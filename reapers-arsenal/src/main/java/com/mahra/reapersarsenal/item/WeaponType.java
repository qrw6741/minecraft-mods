package com.mahra.reapersarsenal.item;

/**
 * Each weapon family. Damage shown in-game = 1 + tier bonus + damageModifier
 * (iron +2, gold +0, diamond +3, netherite +4). Attack speed shown = 4 + speedModifier.
 */
public enum WeaponType {
    //        id           dmg   speed  reach  shieldBreak sweep
    SCYTHE   ("scythe",     4, -3.0F,  1.0,   false,      false),
    KATANA   ("katana",     3, -2.0F,  0.0,   false,      true),
    DAGGER   ("dagger",     1, -1.2F,  0.0,   false,      false),
    WARHAMMER("warhammer",  7, -3.4F,  0.0,   true,       false),
    SPEAR    ("spear",      3, -2.6F,  2.0,   false,      false),
    HALBERD  ("halberd",    6, -3.2F,  1.5,   true,       true);

    public final String id;
    public final int damageModifier;
    public final float speedModifier;
    public final double reachBonus;
    public final boolean disablesShields;
    public final boolean vanillaSweep;

    WeaponType(String id, int damageModifier, float speedModifier, double reachBonus,
               boolean disablesShields, boolean vanillaSweep) {
        this.id = id;
        this.damageModifier = damageModifier;
        this.speedModifier = speedModifier;
        this.reachBonus = reachBonus;
        this.disablesShields = disablesShields;
        this.vanillaSweep = vanillaSweep;
    }
}
