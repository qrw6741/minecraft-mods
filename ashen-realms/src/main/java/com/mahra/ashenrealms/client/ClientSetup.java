package com.mahra.ashenrealms.client;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.registry.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AshenRealms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HOLLOW_KING.get(), HollowKingRenderer::new);
        event.registerEntityRenderer(ModEntities.FROST_COLOSSUS.get(), FrostColossusRenderer::new);
        event.registerEntityRenderer(ModEntities.EMBER_MATRIARCH.get(), EmberMatriarchRenderer::new);
        event.registerEntityRenderer(ModEntities.ICE_SHARD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.ARCANE_BOLT.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.SUNFIRE_BOLT.get(), ThrownItemRenderer::new);
    }
}
