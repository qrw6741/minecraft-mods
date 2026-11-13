package com.mahra.ashenexpanse.item;

import com.mahra.ashenexpanse.registry.ModBlocks;
import com.mahra.ashenexpanse.registry.ModDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Right-click to travel to the Ashlands; right-click there to return home. */
public class AshenWaystoneItem extends Item {
    public AshenWaystoneItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel current) || !(player instanceof ServerPlayer sp)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        boolean inAshlands = current.dimension() == ModDimensions.ASHLANDS;
        ServerLevel dest = current.getServer().getLevel(inAshlands ? Level.OVERWORLD : ModDimensions.ASHLANDS);
        if (dest == null) {
            sp.displayClientMessage(Component.translatable("message.ashen_expanse.no_dimension").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        BlockPos target;
        if (inAshlands) {
            BlockPos home = sp.getRespawnPosition() != null && sp.getRespawnDimension() == Level.OVERWORLD
                    ? sp.getRespawnPosition() : dest.getSharedSpawnPos();
            target = dest.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, home);
        } else {
            target = findSafeSpot(dest, sp.blockPosition());
        }

        current.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.6F);
        sp.teleportTo(dest, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, sp.getYRot(), sp.getXRot());
        dest.playSound(null, target, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 0.4F, 1.2F);
        sp.getCooldowns().addCooldown(this, 100);
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /** Finds solid ground at the same X/Z; builds a small platform if it would land in lava or the void. */
    private static BlockPos findSafeSpot(ServerLevel level, BlockPos from) {
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(from.getX(), 0, from.getZ()));
        boolean noGround = top.getY() <= level.getMinBuildHeight() + 2;
        boolean liquid = !level.getFluidState(top.below()).isEmpty();
        if (noGround || liquid) {
            BlockPos base = noGround ? new BlockPos(from.getX(), 100, from.getZ()) : top;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    level.setBlockAndUpdate(base.offset(dx, -1, dz), ModBlocks.ASHSTONE.get().defaultBlockState());
                    level.setBlockAndUpdate(base.offset(dx, 0, dz), Blocks.AIR.defaultBlockState());
                    level.setBlockAndUpdate(base.offset(dx, 1, dz), Blocks.AIR.defaultBlockState());
                }
            }
            return base;
        }
        return top;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ashen_expanse.waystone").withStyle(ChatFormatting.GRAY));
    }
}
