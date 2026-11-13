# Ashen Realms

An RPG mod for Minecraft **1.20.1 Forge (47.4.28)**: three summonable bosses, legendary weapons and magic staffs.

## Boss progression
1. **The Hollow King** (300 HP) - summon with a **Cursed Crown** (`G G / GDG / BBB`: gold ingots, diamond, bone blocks).
   Summons skeletons, ground-slams, and fires wither skulls when enraged. Drops **Kingsbane** + **Royal Soul**.
2. **The Frost Colossus** (400 HP) - summon with a **Frozen Effigy** (packed ice, blue ice, Royal Soul).
   Throws ice shards, frost novas, and a slowing blizzard when enraged. Drops **Frostfang** + **Frost Soul**.
3. **The Ember Matriarch** (350 HP, Nether only) - summon with an **Ember Totem** (magma cream, blaze rods, Frost Soul).
   Flies, fires fireball volleys, rains meteors and summons blazes. Drops **Sunfire Staff** + **Ember Soul**.

Every boss has a boss bar and enrages at half health. Use the summon item on a block.

## Weapons
| Weapon | Damage / Speed | Source | Ability |
|---|---|---|---|
| Knight's Longsword | 7 / 1.4 | iron + sticks | - |
| Bone Cleaver | 8 / 1.0 | bone blocks + iron | Weakness on hit |
| Arcane Staff | 7 magic | amethyst + lapis | Right-click: arcane bolt |
| Kingsbane | 11 / 1.2 | Hollow King | Heals on hit; right-click Royal Slam |
| Frostfang | 10 / 1.6 | Frost Colossus | Freezes on hit; right-click Frost Nova |
| Sunfire Staff | 6 x3 + fire | Ember Matriarch | Right-click: three fire bolts |
| Eclipse Blade | 13 / 1.6 | netherite sword + all 3 souls | Burn, slow, heal; right-click Eclipse |

## Building
GitHub Actions builds the jar on push to `main` and publishes it under Releases. Locally: `gradle build` (Gradle 8.8, Java 17).
