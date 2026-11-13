package com.mahra.ashenexpanse.client;

import com.mahra.ashenexpanse.AshenExpanse;
import com.mahra.ashenexpanse.entity.AshStalker;
import com.mahra.ashenexpanse.entity.AshenTyrant;
import com.mahra.ashenexpanse.entity.CinderCrawler;
import com.mahra.ashenexpanse.entity.MagmaBrute;
import com.mahra.ashenexpanse.entity.SoulWisp;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/** Renderers reuse vanilla body shapes (enderman, spider, humanoid, ghast) with custom skins. */
public final class Renderers {
    private Renderers() {}

    static ResourceLocation tex(String name) {
        return new ResourceLocation(AshenExpanse.MOD_ID, "textures/entity/" + name + ".png");
    }

    public static class AshStalkerRenderer extends MobRenderer<AshStalker, EndermanModel<AshStalker>> {
        private static final ResourceLocation TEXTURE = tex("ash_stalker");

        public AshStalkerRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new EndermanModel<>(ctx.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
        }

        @Override
        public ResourceLocation getTextureLocation(AshStalker entity) {
            return TEXTURE;
        }
    }

    public static class CinderCrawlerRenderer extends MobRenderer<CinderCrawler, SpiderModel<CinderCrawler>> {
        private static final ResourceLocation TEXTURE = tex("cinder_crawler");

        public CinderCrawlerRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new SpiderModel<>(ctx.bakeLayer(ModelLayers.SPIDER)), 0.8F);
        }

        @Override
        protected float getFlipDegrees(CinderCrawler entity) {
            return 180.0F;
        }

        @Override
        protected int getBlockLightLevel(CinderCrawler entity, BlockPos pos) {
            return Math.max(10, super.getBlockLightLevel(entity, pos));
        }

        @Override
        public ResourceLocation getTextureLocation(CinderCrawler entity) {
            return TEXTURE;
        }
    }

    public static class MagmaBruteRenderer extends HumanoidMobRenderer<MagmaBrute, HumanoidModel<MagmaBrute>> {
        private static final ResourceLocation TEXTURE = tex("magma_brute");

        public MagmaBruteRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 0.7F);
        }

        @Override
        protected void scale(MagmaBrute entity, PoseStack pose, float partialTick) {
            pose.scale(1.4F, 1.4F, 1.4F);
        }

        @Override
        protected int getBlockLightLevel(MagmaBrute entity, BlockPos pos) {
            return Math.max(12, super.getBlockLightLevel(entity, pos));
        }

        @Override
        public ResourceLocation getTextureLocation(MagmaBrute entity) {
            return TEXTURE;
        }
    }

    public static class SoulWispRenderer extends MobRenderer<SoulWisp, GhastModel<SoulWisp>> {
        private static final ResourceLocation TEXTURE = tex("soul_wisp");

        public SoulWispRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new GhastModel<>(ctx.bakeLayer(ModelLayers.GHAST)), 0.3F);
        }

        @Override
        protected void scale(SoulWisp entity, PoseStack pose, float partialTick) {
            pose.scale(0.4F, 0.4F, 0.4F);
        }

        @Override
        protected int getBlockLightLevel(SoulWisp entity, BlockPos pos) {
            return 15;
        }

        @Override
        public ResourceLocation getTextureLocation(SoulWisp entity) {
            return TEXTURE;
        }
    }

    public static class AshenTyrantRenderer extends HumanoidMobRenderer<AshenTyrant, HumanoidModel<AshenTyrant>> {
        private static final ResourceLocation TEXTURE = tex("ashen_tyrant");

        public AshenTyrantRenderer(EntityRendererProvider.Context ctx) {
            super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.ZOMBIE)), 1.2F);
        }

        @Override
        protected void scale(AshenTyrant entity, PoseStack pose, float partialTick) {
            pose.scale(2.2F, 2.2F, 2.2F);
        }

        @Override
        protected int getBlockLightLevel(AshenTyrant entity, BlockPos pos) {
            return Math.max(8, super.getBlockLightLevel(entity, pos));
        }

        @Override
        public ResourceLocation getTextureLocation(AshenTyrant entity) {
            return TEXTURE;
        }
    }
}
