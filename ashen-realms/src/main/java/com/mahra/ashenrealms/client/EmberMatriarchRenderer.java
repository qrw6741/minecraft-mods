package com.mahra.ashenrealms.client;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.entity.boss.EmberMatriarch;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class EmberMatriarchRenderer extends MobRenderer<EmberMatriarch, BlazeModel<EmberMatriarch>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AshenRealms.MOD_ID, "textures/entity/ember_matriarch.png");

    public EmberMatriarchRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BlazeModel<>(ctx.bakeLayer(ModelLayers.BLAZE)), 1.0F);
    }

    @Override
    protected void scale(EmberMatriarch entity, PoseStack pose, float partialTick) {
        pose.scale(2.2F, 2.2F, 2.2F);
    }

    @Override
    protected int getBlockLightLevel(EmberMatriarch entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(EmberMatriarch entity) {
        return TEXTURE;
    }
}
