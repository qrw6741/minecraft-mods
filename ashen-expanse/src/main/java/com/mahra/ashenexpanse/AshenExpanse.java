package com.mahra.ashenexpanse;

import com.mahra.ashenexpanse.registry.ModBlocks;
import com.mahra.ashenexpanse.registry.ModCreativeTab;
import com.mahra.ashenexpanse.registry.ModEntities;
import com.mahra.ashenexpanse.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AshenExpanse.MOD_ID)
public class AshenExpanse {
    public static final String MOD_ID = "ashen_expanse";

    public AshenExpanse() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModCreativeTab.TABS.register(modBus);
    }
}
