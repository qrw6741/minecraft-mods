package com.mahra.ashenrealms.registry;

import com.mahra.ashenrealms.AshenRealms;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AshenRealms.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ashen_realms"))
            .icon(() -> new ItemStack(ModItems.KINGSBANE.get()))
            .displayItems((params, out) -> {
                out.accept(ModItems.KNIGHTS_LONGSWORD.get());
                out.accept(ModItems.BONE_CLEAVER.get());
                out.accept(ModItems.ARCANE_STAFF.get());
                out.accept(ModItems.KINGSBANE.get());
                out.accept(ModItems.FROSTFANG.get());
                out.accept(ModItems.SUNFIRE_STAFF.get());
                out.accept(ModItems.ECLIPSE_BLADE.get());
                out.accept(ModItems.CURSED_CROWN.get());
                out.accept(ModItems.FROZEN_EFFIGY.get());
                out.accept(ModItems.EMBER_TOTEM.get());
                out.accept(ModItems.ROYAL_SOUL.get());
                out.accept(ModItems.FROST_SOUL.get());
                out.accept(ModItems.EMBER_SOUL.get());
                out.accept(ModItems.HOLLOW_KING_SPAWN_EGG.get());
                out.accept(ModItems.FROST_COLOSSUS_SPAWN_EGG.get());
                out.accept(ModItems.EMBER_MATRIARCH_SPAWN_EGG.get());
            })
            .build());

    private ModCreativeTab() {}
}
