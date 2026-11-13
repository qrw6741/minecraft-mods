package com.mahra.ashenexpanse.registry;

import com.mahra.ashenexpanse.AshenExpanse;
import com.mahra.ashenexpanse.entity.AshStalker;
import com.mahra.ashenexpanse.entity.AshenTyrant;
import com.mahra.ashenexpanse.entity.CinderCrawler;
import com.mahra.ashenexpanse.entity.MagmaBrute;
import com.mahra.ashenexpanse.entity.SoulWisp;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AshenExpanse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AshenExpanse.MOD_ID);

    public static final RegistryObject<EntityType<AshStalker>> ASH_STALKER = ENTITIES.register("ash_stalker",
            () -> EntityType.Builder.<AshStalker>of(AshStalker::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.9F).clientTrackingRange(8).build("ash_stalker"));
    public static final RegistryObject<EntityType<CinderCrawler>> CINDER_CRAWLER = ENTITIES.register("cinder_crawler",
            () -> EntityType.Builder.<CinderCrawler>of(CinderCrawler::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F).fireImmune().clientTrackingRange(8).build("cinder_crawler"));
    public static final RegistryObject<EntityType<MagmaBrute>> MAGMA_BRUTE = ENTITIES.register("magma_brute",
            () -> EntityType.Builder.<MagmaBrute>of(MagmaBrute::new, MobCategory.MONSTER)
                    .sized(0.85F, 2.7F).fireImmune().clientTrackingRange(8).build("magma_brute"));
    public static final RegistryObject<EntityType<SoulWisp>> SOUL_WISP = ENTITIES.register("soul_wisp",
            () -> EntityType.Builder.<SoulWisp>of(SoulWisp::new, MobCategory.MONSTER)
                    .sized(0.7F, 0.9F).fireImmune().clientTrackingRange(8).build("soul_wisp"));
    public static final RegistryObject<EntityType<AshenTyrant>> ASHEN_TYRANT = ENTITIES.register("ashen_tyrant",
            () -> EntityType.Builder.<AshenTyrant>of(AshenTyrant::new, MobCategory.MONSTER)
                    .sized(1.3F, 4.3F).fireImmune().clientTrackingRange(10).build("ashen_tyrant"));

    @SubscribeEvent
    public static void attributes(EntityAttributeCreationEvent event) {
        event.put(ASH_STALKER.get(), AshStalker.createAttributes().build());
        event.put(CINDER_CRAWLER.get(), CinderCrawler.createAttributes().build());
        event.put(MAGMA_BRUTE.get(), MagmaBrute.createAttributes().build());
        event.put(SOUL_WISP.get(), SoulWisp.createAttributes().build());
        event.put(ASHEN_TYRANT.get(), AshenTyrant.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ASH_STALKER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(CINDER_CRAWLER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(MAGMA_BRUTE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(SOUL_WISP.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }

    private ModEntities() {}
}
