package com.mahra.ashenrealms.gametest;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.RegistryObject;

/** Smoke tests run in CI on a real server: every item can be held, every mob can live. */
@GameTestHolder(AshenRealms.MOD_ID)
@PrefixGameTestTemplate(false)
public class ModGameTests {
    @GameTest(template = "empty")
    public static void holdEveryItem(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            ItemStack stack = new ItemStack(entry.get());
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                var modifiers = stack.getAttributeModifiers(slot);
                player.getAttributes().addTransientAttributeModifiers(modifiers);
                player.getAttributes().removeAttributeModifiers(modifiers);
            }
            player.setItemSlot(EquipmentSlot.MAINHAND, stack);
            stack.getTooltipLines(player, TooltipFlag.NORMAL);
            stack.inventoryTick(helper.getLevel(), player, 0, true);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void bossesLive(GameTestHelper helper) {
        helper.spawn(com.mahra.ashenrealms.registry.ModEntities.HOLLOW_KING.get(), new BlockPos(2, 1, 2));
        helper.spawn(com.mahra.ashenrealms.registry.ModEntities.FROST_COLOSSUS.get(), new BlockPos(2, 1, 2));
        helper.spawn(com.mahra.ashenrealms.registry.ModEntities.EMBER_MATRIARCH.get(), new BlockPos(2, 1, 2));
        helper.runAfterDelay(60, helper::succeed);
    }
}
