package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriFluidCondition;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.codecs.AgriSoilVariant;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
								.add(AgriApi.AGRIPLANTS, DatagenEventHandler::registerPlants)
								.add(AgriApi.AGRISOILS, DatagenEventHandler::registerSoils),
						// Generate dynamic registry objects for this mod
						Set.of(AgriApi.MOD_ID)
				)
		);
		generator.addProvider(event.includeClient(), new CropModelProvider(event.getGenerator(), event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new SeedModelProvider(event.getGenerator(), event.getExistingFileHelper()));
	}

	private static void registerPlant(BootstapContext<AgriPlant> context, String plantId, AgriPlant plant) {
		context.register(
				ResourceKey.create(AgriApi.AGRIPLANTS, new ResourceLocation(AgriApi.MOD_ID, plantId)),
				plant
		);
	}

	private static void registerSoil(BootstapContext<AgriSoil> context, String soilId, AgriSoil soil) {
		context.register(
				ResourceKey.create(AgriApi.AGRISOILS, new ResourceLocation(AgriApi.MOD_ID, soilId)),
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
		registerPlant(context, "allium", AgriPlant.builder()
				.defaultMods()
				.seeds()  // AgriSeed.builder().chances(0.0, 1.0, 0.0).build()
				.stages(2, 4, 6, 8, 10, 12, 14, 16)
				.harvest(4)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:magenta_dye").count(1, 1, 0.75).build())
				.clips(AgriProduct.builder().item("minecraft:allium").count(0, 1, 0.5).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.DAMP, AgriSoilCondition.Type.EQUAL, 0.2)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.VERY_HIGH, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.2)
						.light(10, 16, 0.5)
						.seasons("spring")
						.build())
				.build());

		registerPlant(context, "kelp", AgriPlant.builder()
				.defaultMods()
				.seeds()  // AgriSeed.builder().chances(0.0, 1.0, 0.0).build()
				.stages(6, 6, 12, 12, 12, 18, 18, 24)
				.harvest(4)
				.chances(0.65, 0.025, 0.1)
				.products(AgriProduct.builder().item("minecraft:kelp").count(1, 1, 0.75).build())
				.requirement(AgriRequirement.builder()
						.humidity(AgriSoilCondition.Humidity.WATERY, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.4)
						.acidity(AgriSoilCondition.Acidity.SLIGHTLY_ACIDIC, AgriSoilCondition.Type.EQUAL, 0.2)
						.nutrients(AgriSoilCondition.Nutrients.MEDIUM, AgriSoilCondition.Type.EQUAL_OR_HIGHER, 0.1)
						.light(5, 16, 0.5)
						.seasons("spring", "summer", "autumn", "winter")
						.fluid(new AgriFluidCondition(new ExtraCodecs.TagOrElementLocation(new ResourceLocation("minecraft:water"), false), List.of()))
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

	private static class CropModelProvider extends ModelProvider<BlockModelBuilder> {

		public CropModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
			super(generator.getPackOutput(), AgriApi.MOD_ID, "crop", BlockModelBuilder::new, existingFileHelper);
		}


		@Override
		protected void registerModels() {
			this.withExistingParent("bamboo_stage0", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/bamboo_stage0");
			cropPlus("bamboo", 1, 2, 3);
			tallCropPlus("bamboo", 4, 5, 6, 7);
			this.with4TexturesIn7stage("cactus_stage", "agricraft:crop/crop_hash", "agricraft:block/cactus_stage");
			this.with4TexturesIn7stage("carrot_stage", "agricraft:crop/crop_hash", "minecraft:block/carrots_stage");
			this.with4TexturesIn7stage("potato_stage", "agricraft:crop/crop_hash", "minecraft:block/potatoes_stage");
			this.with4TexturesIn7stage("sugar_cane_stage", "agricraft:crop/crop_hash", "agricraft:block/sugar_cane_stage");
			for (int stage : List.of(0, 1, 2, 3, 4, 5, 6, 7)) {
				this.withExistingParent("wheat_stage"+stage, "agricraft:crop/crop_hash").texture("crop", "minecraft:block/wheat_stage"+stage);
			}
			this.with4TexturesIn7stage("allium_stage", "agricraft:crop/crop_hash", "agricraft:block/allium_stage");
			this.withExistingParent("kelp_stage0", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage1", "agricraft:crop/crop_plus").texture("crop", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage2", "agricraft:crop/crop_plus").texture("crop", "agricraft:block/kelp");
			this.withExistingParent("kelp_stage3", "agricraft:crop/crop_plus").texture("crop", "agricraft:block/kelp");
			this.withExistingParent("kelp_stage4", "agricraft:crop/crop_plus").texture("crop", "agricraft:block/kelp");
			this.withExistingParent("kelp_stage5", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage6", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "minecraft:block/kelp");
			this.withExistingParent("kelp_stage7", "agricraft:crop/tall_crop_plus").texture("crop", "minecraft:block/kelp_plant").texture("crop_top", "agricraft:block/kelp");
		}

		private void cropPlus(String name, int... stages) {
			for (int stage : stages) {
				this.withExistingParent(name + "_stage" + stage, "agricraft:crop/crop_plus")
						.texture("crop", "agricraft:block/" + name + "_stage" + stage);
			}
		}
		private void tallCropPlus(String name, int... stages) {
			for (int stage : stages) {
				this.withExistingParent(name + "_stage" + stage, "agricraft:crop/tall_crop_plus")
						.texture("crop", "agricraft:block/"+name+"_base")
						.texture("crop_top", "agricraft:block/" + name + "_stage" + stage);

			}
		}

		private void with4TexturesIn7stage(String name, String parent, String baseTexture) {
			this.withExistingParent(name + "0", parent).texture("crop", baseTexture + "0");
			this.withExistingParent(name + "1", parent).texture("crop", baseTexture + "0");
			this.withExistingParent(name + "2", parent).texture("crop", baseTexture + "0");
			this.withExistingParent(name + "3", parent).texture("crop", baseTexture + "1");
			this.withExistingParent(name + "4", parent).texture("crop", baseTexture + "1");
			this.withExistingParent(name + "5", parent).texture("crop", baseTexture + "2");
			this.withExistingParent(name + "6", parent).texture("crop", baseTexture + "2");
			this.withExistingParent(name + "7", parent).texture("crop", baseTexture + "3");

		}

		@Override
		public String getName() {
			return "Crop Models: " + this.modid;
		}

	}

	private static class SeedModelProvider extends ModelProvider<ItemModelBuilder> {

		public SeedModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
			super(generator.getPackOutput(), AgriApi.MOD_ID, "seed", ItemModelBuilder::new, existingFileHelper);
		}


		@Override
		protected void registerModels() {
			List.of("bamboo", "cactus", "carrot", "potato", "sugar_cane", "allium", "kelp", "unknown")
					.forEach(this::seed);
			this.withExistingParent("wheat", "minecraft:item/wheat_seeds");
		}

		private void seed(String name) {
			this.withExistingParent(name, "minecraft:item/generated").texture("layer0", "agricraft:seed/" + name);

		}

		@Override
		public String getName() {
			return "Seed Models: " + this.modid;
		}

	}

}
