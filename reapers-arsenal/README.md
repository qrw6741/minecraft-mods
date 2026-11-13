# Reaper's Arsenal

A Minecraft **1.20.1 Forge (47.4.28)** mod with six new weapon families, each in iron, gold, diamond and netherite.

| Weapon | Damage (iron/gold/diamond/netherite) | Speed | Ability |
|---|---|---|---|
| Scythe | 7 / 5 / 8 / 9 | 1.0 | +1 reach. Fully charged swings reap every enemy in a wide arc in front of you (75% damage). |
| Katana | 6 / 4 / 7 / 8 | 2.0 | Fully charged hits cause **Bleeding** (damage over time). Sweeps. |
| Dagger | 4 / 2 / 5 / 6 | 2.8 | Hitting a mob from behind deals 75% extra damage. |
| Warhammer | 10 / 8 / 11 / 12 | 0.6 | Disables shields, slows on hit. Fully charged hits cause a shockwave. |
| Spear | 6 / 4 / 7 / 8 | 1.4 | +2 reach. Sprinting strikes deal 50% extra damage. |
| Halberd | 9 / 7 / 10 / 11 | 0.8 | +1.5 reach, disables shields, sweeps. |

All weapons take sword enchantments (Sharpness, Looting, Fire Aspect, Unbreaking, Mending...).

## Recipes
`M` = iron ingot / gold ingot / diamond, `S` = stick. Netherite versions: smithing table with a netherite upgrade template + the diamond weapon + a netherite ingot.

```
Scythe   Katana   Dagger   Warhammer  Spear    Halberd
M M M    . . M    . M      M M M      . . M    . M M
. S M    . M .    S .      M S M      . S .    . S M
S . .    S . .             . S .      S . .    S . .
```

## Building
GitHub Actions builds the jar on every push to `main` and publishes it under **Releases**.
Locally: `gradle build` (Gradle 8.8, Java 17) → `build/libs/`.
