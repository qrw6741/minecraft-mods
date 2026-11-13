package com.mahra.ashenexpanse.client;

import com.mahra.ashenexpanse.AshenExpanse;
import com.mahra.ashenexpanse.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AshenExpanse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ASH_STALKER.get(), Renderers.AshStalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.CINDER_CRAWLER.get(), Renderers.CinderCrawlerRenderer::new);
        event.registerEntityRenderer(ModEntities.MAGMA_BRUTE.get(), Renderers.MagmaBruteRenderer::new);
        event.registerEntityRenderer(ModEntities.SOUL_WISP.get(), Renderers.SoulWispRenderer::new);
        event.registerEntityRenderer(ModEntities.ASHEN_TYRANT.get(), Renderers.AshenTyrantRenderer::new);
    }
}
