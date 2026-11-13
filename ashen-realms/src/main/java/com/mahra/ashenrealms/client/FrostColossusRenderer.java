package com.mahra.ashenrealms.client;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.entity.boss.FrostColossus;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FrostColossusRenderer extends MobRenderer<FrostColossus, IronGolemModel<FrostColossus>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AshenRealms.MOD_ID, "textures/entity/frost_colossus.png");

    public FrostColossusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new IronGolemModel<>(ctx.bakeLayer(ModelLayers.IRON_GOLEM)), 1.4F);
    }

    @Override
    protected void scale(FrostColossus entity, PoseStack pose, float partialTick) {
        pose.scale(1.45F, 1.45F, 1.45F);
    }

    @Override
    public ResourceLocation getTextureLocation(FrostColossus entity) {
        return TEXTURE;
    }
}
