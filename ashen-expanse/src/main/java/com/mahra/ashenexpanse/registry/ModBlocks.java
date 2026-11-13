package com.mahra.ashenexpanse.registry;

import com.mahra.ashenexpanse.AshenExpanse;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AshenExpanse.MOD_ID);

    public static final RegistryObject<Block> ASHSTONE = BLOCKS.register("ashstone", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(1.5F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.BASALT)));
    public static final RegistryObject<Block> ASH_BLOCK = BLOCKS.register("ash_block", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F).sound(SoundType.SAND)));
    public static final RegistryObject<Block> ASHSTONE_BRICKS = BLOCKS.register("ashstone_bricks", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.NETHER_BRICKS)));

    public static final RegistryObject<Block> UMBRAL_ORE = BLOCKS.register("umbral_ore", () -> new DropExperienceBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops(), UniformInt.of(1, 4)));
    public static final RegistryObject<Block> DEEPSLATE_UMBRAL_ORE = BLOCKS.register("deepslate_umbral_ore", () -> new DropExperienceBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE), UniformInt.of(1, 4)));
    public static final RegistryObject<Block> CINDERITE_ORE = BLOCKS.register("cinderite_ore", () -> new DropExperienceBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(5.0F, 9.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.BASALT).lightLevel(s -> 6), UniformInt.of(3, 7)));

    public static final RegistryObject<Block> UMBRAL_BLOCK = BLOCKS.register("umbral_block", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.METAL)));
    public static final RegistryObject<Block> CINDERITE_BLOCK = BLOCKS.register("cinderite_block", () -> new Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(7.0F, 1200.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.NETHERITE_BLOCK).lightLevel(s -> 10)));

    private ModBlocks() {}
}
