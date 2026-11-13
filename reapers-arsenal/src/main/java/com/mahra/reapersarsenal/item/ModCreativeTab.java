package com.mahra.reapersarsenal.item;

import com.mahra.reapersarsenal.ReapersArsenal;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReapersArsenal.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.reapers_arsenal"))
            .icon(() -> new ItemStack(ModItems.get(WeaponType.SCYTHE, WeaponMaterial.NETHERITE)))
            .displayItems((params, output) ->
                    ModItems.WEAPONS.values().forEach(m -> m.values().forEach(r -> output.accept(r.get()))))
            .build());

    private ModCreativeTab() {}
}
