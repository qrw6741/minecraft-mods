package com.mahra.reapersarsenal;

import com.mahra.reapersarsenal.effect.ModEffects;
import com.mahra.reapersarsenal.item.ModCreativeTab;
import com.mahra.reapersarsenal.item.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ReapersArsenal.MOD_ID)
public class ReapersArsenal {
    public static final String MOD_ID = "reapers_arsenal";

    public ReapersArsenal() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModCreativeTab.TABS.register(modBus);
    }
}
