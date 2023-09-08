package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.codecs.AgriSoilVariant;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = AgriCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DatagenEventHandler {

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(
				event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
						output,
						event.getLookupProvider(),
						// The objects to generate
						new RegistrySetBuilder()
								.add(PlatformUtils.getPlantRegistryKey(), DatagenEventHandler::registerPlants)
								.add(PlatformUtils.AGRISOILS, DatagenEventHandler::registerSoils),
						// Generate dynamic registry objects for this mod
						Set.of(AgriCraft.MOD_ID)
				)
		);
		// TODO: @Ketheroth datagen model and lang files
		// TODO: @Ketheroth plant/soil id to lang fix for subfolder in resource location
		// TODO: @Ketheroth wrap PlatformUtils in AgriApi
	}

	private static void registerPlant(BootstapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		context.register(
				ResourceKey.create(PlatformUtils.getPlantRegistryKey(), new ResourceLocation(AgriCraft.MOD_ID, plantId)),
				plant
		);
	}

	private static void registerSoil(BootstapContext<AgriSoil> context, String soilId, AgriSoil soil) {
		context.register(
				ResourceKey.create(PlatformUtils.AGRISOILS, new ResourceLocation(AgriCraft.MOD_ID, soilId)),
				soil
		);
	}

	private static void registerPlants(BootstapContext<AgriPlant> context) {
		registerPlant(context, "wheat", AgriPlant.builder()
				.defaultMods()
				.seeds(AgriSeed.builder().item("minecraft:wheat_seeds").chances(0.0, 1.0, 0.0).build())
				.stages16()
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:wheat").count(1, 3, 0.95).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.WET, AgriSoilCondition.Type.EQUAL, 0.15)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.HIGH, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons("summer", "autumn")
						.build())
				.build());
		registerPlant(context, "potato", AgriPlant.builder()
				.defaultMods()
				.seeds(AgriSeed.builder().item("minecraft:potato").chances(0.0, 1.0, 0.0).build())
				.stages(2, 3, 4, 5, 6, 7, 8, 9)
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(
						AgriProduct.builder().item("minecraft:potato").count(1, 4, 0.95).build(),
						AgriProduct.builder().item("minecraft:poisonous_potato").count(1, 2, 0.02).build()
				)
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.WET, AgriSoilCondition.Type.EQUAL, 0.15)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.HIGH, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons("spring")
						.build())
				.build());
		registerPlant(context, "carrot", AgriPlant.builder()
				.defaultMods()
				.seeds(AgriSeed.builder().item("minecraft:carrot").chances(0.0, 1.0, 0.0).build())
				.stages(2, 3, 4, 5, 6, 7, 8, 9)
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:carrot").count(1, 4, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.WET, AgriSoilCondition.Type.EQUAL, 0.15)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.HIGH, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons("spring", "autumn")
						.build())
				.build());
		registerPlant(context, "sugar_cane", AgriPlant.builder()
				.defaultMods()
				.seeds(AgriSeed.builder().item("minecraft:sugar_cane").chances(0.0, 1.0, 0.0).build())
				.stages(2, 4, 6, 8, 10, 12, 14, 16)
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:sugar_cane").count(1, 2, 1000.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.WET, AgriSoilCondition.Type.EQUAL, 0.15)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.HIGH, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons("summer")
						.build())
				.build());
		registerPlant(context, "cactus", AgriPlant.builder()
				.defaultMods()
				.seeds(AgriSeed.builder().item("minecraft:cactus").chances(0.0, 1.0, 0.0).build())
				.stages(2, 4, 6, 8, 10, 12, 14, 16)
				.harvest(4)
				.chances(0.75, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:cactus").count(1, 3, 1.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.ARID, AgriSoilCondition.Type.EQUAL, 0.34)
						.acidity(AgriSoilCondition.Acidity.NEUTRAL, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.LOW, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.1)
						.light(10, 16, 0.5)
						.seasons("summer")
						.build())
				.build());
		registerPlant(context, "bamboo", AgriPlant.builder()
				.defaultMods()
				.seeds(AgriSeed.builder().item("minecraft:bamboo").chances(0.0, 1.0, 0.0).build())
				.stages(3, 6, 9, 12, 15, 18, 21, 24)
				.harvest(4)
				.chances(0.9, 0.01, 0.1)
				.products(AgriProduct.builder().item("minecraft:bamboo").count(1, 2, 1000.0).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.DAMP, AgriSoilCondition.Type.EQUAL, 0.4)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.4)
						.nutrients(AgriSoilCondition.Nutrients.MEDIUM, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.4)
						.light(10, 16, 0.5)
						.seasons("spring", "summer")
						.build())
				.build());

	}


	private static void registerSoils(BootstapContext<AgriSoil> context) {
		registerSoil(context, "farmland", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:farmland").build())
				.humidity(AgriSoilCondition.Humidity.WET)
				.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC)
				.nutrients(AgriSoilCondition.Nutrients.HIGH)
				.growthModifier(1.0)
				.build());
		registerSoil(context, "sand", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().tag("forge:sand").build(),
						AgriSoilVariant.builder().tag("minecraft:sand").build())
				.humidity(AgriSoilCondition.Humidity.ARID)
				.acidity(AgriSoilCondition.Acidity.NEUTRAL)
				.nutrients(AgriSoilCondition.Nutrients.LOW)
				.growthModifier(0.8)
				.build());
		registerSoil(context, "clay", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:clay").build())
				.humidity(AgriSoilCondition.Humidity.FLOODED)
				.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC)
				.nutrients(AgriSoilCondition.Nutrients.MEDIUM)
				.growthModifier(1.0)
				.build());
		registerSoil(context, "podzol", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:podzol").build())
				.humidity(AgriSoilCondition.Humidity.DAMP)
				.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC)
				.nutrients(AgriSoilCondition.Nutrients.VERY_HIGH)
				.growthModifier(1.1)
				.build());
		registerSoil(context, "mycelium", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:mycelium").build())
				.humidity(AgriSoilCondition.Humidity.DAMP)
				.acidity(AgriSoilCondition.Acidity.NEUTRAL)
				.nutrients(AgriSoilCondition.Nutrients.LOW)
				.growthModifier(0.8)
				.build());
		registerSoil(context, "soul_sand", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:soul_sand").build())
				.humidity(AgriSoilCondition.Humidity.ARID)
				.acidity(AgriSoilCondition.Acidity.NEUTRAL)
				.nutrients(AgriSoilCondition.Nutrients.HIGH)
				.growthModifier(0.8)
				.build());
		registerSoil(context, "soul_soil", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:soul_soil").build())
				.humidity(AgriSoilCondition.Humidity.ARID)
				.acidity(AgriSoilCondition.Acidity.NEUTRAL)
				.nutrients(AgriSoilCondition.Nutrients.VERY_HIGH)
				.growthModifier(1.0)
				.build());
		registerSoil(context, "crimson_nylium", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:crimson_nylium").build())
				.humidity(AgriSoilCondition.Humidity.ARID)
				.acidity(AgriSoilCondition.Acidity.HIGHLY_ACIDIC)
				.nutrients(AgriSoilCondition.Nutrients.LOW)
				.growthModifier(0.75)
				.build());
		registerSoil(context, "warped_nylium", AgriSoil.builder()
				.defaultMods()
				.variants(AgriSoilVariant.builder().block("minecraft:warped_nylium").build())
				.humidity(AgriSoilCondition.Humidity.ARID)
				.acidity(AgriSoilCondition.Acidity.HIGHLY_ALKALINE)
				.nutrients(AgriSoilCondition.Nutrients.LOW)
				.growthModifier(0.75)
				.build());
	}

}
