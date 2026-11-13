package com.mahra.ashenexpanse.gametest;

import com.mahra.ashenexpanse.AshenExpanse;
import com.mahra.ashenexpanse.registry.ModItems;
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
@GameTestHolder(AshenExpanse.MOD_ID)
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
    public static void mobsLive(GameTestHelper helper) {
        helper.spawn(com.mahra.ashenexpanse.registry.ModEntities.ASH_STALKER.get(), new BlockPos(1, 1, 1));
        helper.spawn(com.mahra.ashenexpanse.registry.ModEntities.CINDER_CRAWLER.get(), new BlockPos(3, 1, 1));
        helper.spawn(com.mahra.ashenexpanse.registry.ModEntities.MAGMA_BRUTE.get(), new BlockPos(1, 1, 3));
        helper.spawn(com.mahra.ashenexpanse.registry.ModEntities.SOUL_WISP.get(), new BlockPos(3, 2, 3));
        helper.spawn(com.mahra.ashenexpanse.registry.ModEntities.ASHEN_TYRANT.get(), new BlockPos(2, 1, 2));
        helper.runAfterDelay(60, helper::succeed);
    }

    @GameTest(template = "empty", timeoutTicks = 1200)
    public static void ashlandsGenerates(GameTestHelper helper) {
        net.minecraft.server.level.ServerLevel ash = helper.getLevel().getServer()
                .getLevel(com.mahra.ashenexpanse.registry.ModDimensions.ASHLANDS);
        if (ash == null) {
            helper.fail("Ashlands dimension did not load");
            return;
        }
        int solid = 0;
        for (int cx = -2; cx <= 2; cx++) {
            for (int cz = -2; cz <= 2; cz++) {
                ash.getChunk(cx, cz);
                BlockPos top = ash.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING,
                        new BlockPos(cx * 16 + 8, 0, cz * 16 + 8));
                if (top.getY() > ash.getMinBuildHeight() + 5) solid++;
                org.slf4j.LoggerFactory.getLogger("AshenExpanseTest").info("Ashlands chunk {},{} surface y={} biome={} block={}",
                        cx, cz, top.getY(), ash.getBiome(top).unwrapKey().map(k -> k.location().toString()).orElse("?"),
                        ash.getBlockState(top.below()));
            }
        }
        if (solid < 20) {
            helper.fail("Ashlands terrain looks empty: only " + solid + "/25 chunks have ground");
            return;
        }
        helper.succeed();
    }
}
