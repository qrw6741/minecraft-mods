package com.mahra.reapersarsenal.client;

import com.mahra.reapersarsenal.item.WeaponType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Client-only visuals: a custom first-person attack animation per weapon type,
 * and a two-handed third-person grip for the long/heavy weapons.
 */
public class WeaponClientExtensions implements IClientItemExtensions {
    private static final float PI = (float) Math.PI;

    /** Both hands on the shaft, held diagonally in front of the body. */
    private static final HumanoidModel.ArmPose TWO_HANDED = HumanoidModel.ArmPose.create(
            "REAPERS_ARSENAL_TWO_HANDED", true, (model, entity, arm) -> {
                boolean right = arm == HumanoidArm.RIGHT;
                ModelPart main = right ? model.rightArm : model.leftArm;
                ModelPart off = right ? model.leftArm : model.rightArm;
                float side = right ? 1.0F : -1.0F;
                float look = model.head.xRot * 0.4F;
                main.xRot = -0.55F + look;
                main.yRot = -0.15F * side;
                off.xRot = -1.0F + look;
                off.yRot = 0.6F * side;
            });

    private final WeaponType type;

    public WeaponClientExtensions(WeaponType type) {
        this.type = type;
    }

    private boolean isTwoHanded() {
        return type == WeaponType.SCYTHE || type == WeaponType.HALBERD
                || type == WeaponType.SPEAR || type == WeaponType.WARHAMMER;
    }

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (hand == InteractionHand.MAIN_HAND && isTwoHanded()) {
            return TWO_HANDED;
        }
        return null;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack,
                                           float partialTick, float equipProcess, float swingProcess) {
        float i = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float s = swingProcess;
        float e = Mth.sin(s * PI);                 // smooth 0 -> 1 -> 0
        float b = Mth.sin(Mth.sqrt(s) * PI);       // snappy 0 -> 1 -> 0 (fast start)

        // Same resting position as vanilla held items.
        pose.translate(i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);

        switch (type) {
            case SCYTHE -> {
                // Wind back to the right, then reap in a wide horizontal arc to the left.
                float sweep = e * (70.0F - 210.0F * s);
                pose.translate(i * -0.35F * e * s, 0.08F * e, -0.12F * e);
                pose.mulPose(Axis.YP.rotationDegrees(i * sweep));
                pose.mulPose(Axis.ZP.rotationDegrees(i * -35.0F * e));
                pose.mulPose(Axis.XP.rotationDegrees(-15.0F * e));
            }
            case KATANA -> {
                // Fast diagonal slash from upper right to lower left.
                pose.translate(i * -0.28F * b, 0.14F * b * (1.0F - 2.0F * s), -0.1F * b);
                pose.mulPose(Axis.YP.rotationDegrees(i * (30.0F * e - 100.0F * e * s)));
                pose.mulPose(Axis.ZP.rotationDegrees(i * -45.0F * b));
                pose.mulPose(Axis.XP.rotationDegrees(-55.0F * b));
            }
            case DAGGER -> {
                // Quick forward stab.
                pose.translate(i * -0.12F * b, 0.06F * b, -0.45F * b);
                pose.mulPose(Axis.YP.rotationDegrees(i * -20.0F * b));
                pose.mulPose(Axis.XP.rotationDegrees(-55.0F * b));
            }
            case SPEAR -> {
                // Pull back, then thrust far forward.
                float thrust = e * (0.25F - 1.25F * s);
                pose.translate(i * -0.18F * b, 0.12F * b, thrust);
                pose.mulPose(Axis.YP.rotationDegrees(i * -15.0F * b));
                pose.mulPose(Axis.XP.rotationDegrees(-65.0F * b));
            }
            case WARHAMMER -> {
                // Heave overhead, then slam straight down.
                pose.translate(i * -0.15F * e, 0.28F * e * (1.0F - 2.0F * s), -0.15F * e);
                pose.mulPose(Axis.XP.rotationDegrees(e * (65.0F - 220.0F * s)));
                pose.mulPose(Axis.ZP.rotationDegrees(i * -15.0F * e));
            }
            case HALBERD -> {
                // Raised diagonal chop.
                pose.translate(i * -0.22F * e, 0.22F * e * (1.0F - 2.0F * s), -0.15F * e);
                pose.mulPose(Axis.XP.rotationDegrees(e * (50.0F - 185.0F * s)));
                pose.mulPose(Axis.ZP.rotationDegrees(i * -35.0F * e));
                pose.mulPose(Axis.YP.rotationDegrees(i * -20.0F * e));
            }
        }
        return true;
    }
}
