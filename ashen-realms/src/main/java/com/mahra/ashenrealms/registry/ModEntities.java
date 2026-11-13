package com.mahra.ashenrealms.registry;

import com.mahra.ashenrealms.AshenRealms;
import com.mahra.ashenrealms.entity.boss.EmberMatriarch;
import com.mahra.ashenrealms.entity.boss.FrostColossus;
import com.mahra.ashenrealms.entity.boss.HollowKing;
import com.mahra.ashenrealms.entity.projectile.ArcaneBolt;
import com.mahra.ashenrealms.entity.projectile.IceShard;
import com.mahra.ashenrealms.entity.projectile.SunfireBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AshenRealms.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AshenRealms.MOD_ID);

    public static final RegistryObject<EntityType<HollowKing>> HOLLOW_KING = ENTITIES.register("hollow_king",
            () -> EntityType.Builder.<HollowKing>of(HollowKing::new, MobCategory.MONSTER)
                    .sized(1.1F, 3.5F).fireImmune().clientTrackingRange(10).build("hollow_king"));

    public static final RegistryObject<EntityType<FrostColossus>> FROST_COLOSSUS = ENTITIES.register("frost_colossus",
            () -> EntityType.Builder.<FrostColossus>of(FrostColossus::new, MobCategory.MONSTER)
                    .sized(2.0F, 4.0F).clientTrackingRange(10).build("frost_colossus"));

    public static final RegistryObject<EntityType<EmberMatriarch>> EMBER_MATRIARCH = ENTITIES.register("ember_matriarch",
            () -> EntityType.Builder.<EmberMatriarch>of(EmberMatriarch::new, MobCategory.MONSTER)
                    .sized(1.3F, 3.9F).fireImmune().clientTrackingRange(10).build("ember_matriarch"));

    public static final RegistryObject<EntityType<IceShard>> ICE_SHARD = ENTITIES.register("ice_shard",
            () -> EntityType.Builder.<IceShard>of(IceShard::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10).build("ice_shard"));

    public static final RegistryObject<EntityType<ArcaneBolt>> ARCANE_BOLT = ENTITIES.register("arcane_bolt",
            () -> EntityType.Builder.<ArcaneBolt>of(ArcaneBolt::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10).build("arcane_bolt"));

    public static final RegistryObject<EntityType<SunfireBolt>> SUNFIRE_BOLT = ENTITIES.register("sunfire_bolt",
            () -> EntityType.Builder.<SunfireBolt>of(SunfireBolt::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10).build("sunfire_bolt"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HOLLOW_KING.get(), HollowKing.createAttributes().build());
        event.put(FROST_COLOSSUS.get(), FrostColossus.createAttributes().build());
        event.put(EMBER_MATRIARCH.get(), EmberMatriarch.createAttributes().build());
    }

    private ModEntities() {}
}
