package com.mahra.ashenrealms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/** Custom first-person attack swings: a fast diagonal slash, or a heavy overhead slam. */
public class RpgSwingExtensions implements IClientItemExtensions {
    private static final float PI = (float) Math.PI;
    private final boolean heavy;

    public RpgSwingExtensions(boolean heavy) {
        this.heavy = heavy;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack,
                                           float partialTick, float equipProcess, float swingProcess) {
        float i = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float s = swingProcess;
        float e = Mth.sin(s * PI);
        float b = Mth.sin(Mth.sqrt(s) * PI);
        pose.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
        if (heavy) {
            pose.translate(i * -0.15F * e, 0.28F * e * (1.0F - 2.0F * s), -0.15F * e);
            pose.mulPose(Axis.XP.rotationDegrees(e * (65.0F - 220.0F * s)));
            pose.mulPose(Axis.ZP.rotationDegrees(i * -15.0F * e));
        } else {
            pose.translate(i * -0.28F * b, 0.14F * b * (1.0F - 2.0F * s), -0.1F * b);
            pose.mulPose(Axis.YP.rotationDegrees(i * (30.0F * e - 100.0F * e * s)));
            pose.mulPose(Axis.ZP.rotationDegrees(i * -45.0F * b));
            pose.mulPose(Axis.XP.rotationDegrees(-55.0F * b));
        }
        return true;
    }
}
