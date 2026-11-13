package com.mahra.ashenexpanse.registry;

import com.mahra.ashenexpanse.AshenExpanse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AshenExpanse.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ashen_expanse"))
            .icon(() -> new ItemStack(ModItems.ASHBRINGER.get()))
            .displayItems((params, out) -> ModItems.TAB_ORDER.forEach(r -> out.accept(r.get())))
            .build());

    private ModCreativeTab() {}
}
