package com.mahra.ashenrealms;

import com.mahra.ashenrealms.registry.ModCreativeTab;
import com.mahra.ashenrealms.registry.ModEntities;
import com.mahra.ashenrealms.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AshenRealms.MOD_ID)
public class AshenRealms {
    public static final String MOD_ID = "ashen_realms";

    public AshenRealms() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTab.TABS.register(modBus);
    }
}
