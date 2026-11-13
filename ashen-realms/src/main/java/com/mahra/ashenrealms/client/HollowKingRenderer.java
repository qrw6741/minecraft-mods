package com.mahra.ashenrealms.client;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.entity.boss.HollowKing;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Uses the vanilla skeleton body shape, scaled up, with a custom crowned texture. */
public class HollowKingRenderer extends HumanoidMobRenderer<HollowKing, HumanoidModel<HollowKing>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(AshenRealms.MOD_ID, "textures/entity/hollow_king.png");

    public HollowKingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.SKELETON)), 0.9F);
    }

    @Override
    protected void scale(HollowKing entity, PoseStack pose, float partialTick) {
        pose.scale(1.8F, 1.8F, 1.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(HollowKing entity) {
        return TEXTURE;
    }
}
