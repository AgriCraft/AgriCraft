package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import net.minecraft.DetectedVersion;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DatagenEventHandler {

	private static final boolean biomesoplenty = true;
	private static final boolean immersiveengineering = true;
	private static final boolean pamhc2crops = true;

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeServer(), (DataProvider.Factory<RecipeProvider>) ModRecipeProvider::new);
		generator.addProvider(event.includeServer(), (DataProvider.Factory<ItemTagsProvider>) output -> new ModTagProvider(output, event.getLookupProvider(), CompletableFuture.completedFuture(null)));
		generator.addProvider(
				event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
						output,
						event.getLookupProvider(),
						// The objects to generate
						new RegistrySetBuilder()
								.add(AgriApi.AGRIPLANTS, PlantsDatagen::registerPlants)
								.add(AgriApi.AGRISOILS, SoilsDatagen::registerSoils)
								.add(AgriApi.AGRIMUTATIONS, MutationsDatagen::registerMutations)
								.add(AgriApi.AGRIFERTILIZERS, FertilizersDatagen::registerFertilizers)
								.add(AgriApi.AGRIWEEDS, WeedsDatagen::registerWeeds),
						// Generate dynamic registry objects for this mod
						Set.of("minecraft", AgriApi.MOD_ID)
				)
		);
		addProvider("minecraft", "crop", ModelsDatagen::registerMinecraftPlant, BlockModelBuilder::new, event.getGenerator(), event.getExistingFileHelper(), event.includeClient());
		addProvider("minecraft", "seed", ModelsDatagen::registerMinecraftSeed, ItemModelBuilder::new, event.getGenerator(), event.getExistingFileHelper(), event.includeClient());
		addProvider("agricraft", "crop", ModelsDatagen::registerAgricraftPlant, BlockModelBuilder::new, event.getGenerator(), event.getExistingFileHelper(), event.includeClient());
		addProvider("agricraft", "seed", ModelsDatagen::registerAgricraftSeed, ItemModelBuilder::new, event.getGenerator(), event.getExistingFileHelper(), event.includeClient());
		addProvider("agricraft", "weed", ModelsDatagen::registerAgricraftWeed, BlockModelBuilder::new, event.getGenerator(), event.getExistingFileHelper(), event.includeClient());

		if (biomesoplenty) {
			addExtraDataPackProvider("biomesoplenty", new RegistrySetBuilder().add(AgriApi.AGRIPLANTS, PlantsDatagen::registerBiomesOPlenty).add(AgriApi.AGRIMUTATIONS, MutationsDatagen::registerBiomesOPlenty), ModelsDatagen::registerBiomesOPlentyPlant, ModelsDatagen::registerBiomesOPlentySeed, LangDatagen::biomesoplenty, event);
		}
		if (immersiveengineering) {
			addExtraDataPackProvider("immersiveengineering", new RegistrySetBuilder().add(AgriApi.AGRIPLANTS, PlantsDatagen::registerImmersiveEngineering).add(AgriApi.AGRIMUTATIONS, MutationsDatagen::registerImmersiveEngineering), ModelsDatagen::registerImmersiveEngineeringPlant, ModelsDatagen::registerImmersiveEngineeringSeed, LangDatagen::immersiveengineering, event);
		}
		if (pamhc2crops) {
			addExtraDataPackProvider("pamhc2crops", new RegistrySetBuilder().add(AgriApi.AGRIPLANTS, PlantsDatagen::registerPamsHarvestCraft2).add(AgriApi.AGRIMUTATIONS, MutationsDatagen::registerPamsHarvestCraft2), ModelsDatagen::registerPamsHarvestCraft2Plant, ModelsDatagen::registerPamsHarvestCraft2Seed, LangDatagen::pamhc2crops, event);
		}

	}

	private static <T extends ModelBuilder<T>> void addProvider(String modid, String folder, Consumer<ModelProvider<T>> consumer, BiFunction<ResourceLocation, ExistingFileHelper, T> builderFromModId, DataGenerator generator, ExistingFileHelper existingFileHelper, boolean includeClient) {
		generator.addProvider(includeClient, new ModelProvider<T>(generator.getPackOutput(), modid, folder, builderFromModId, existingFileHelper) {
			@Override
			protected void registerModels() {
				consumer.accept(this);
			}

			@Override
			public String getName() {
				return "Models for: %s:%s".formatted(this.modid, this.folder);
			}
		});
	}

	private static void addExtraDataPackProvider(String modid, RegistrySetBuilder registrySetBuilder, Consumer<ModelProvider<BlockModelBuilder>> blockModels,
	                                             Consumer<ModelProvider<ItemModelBuilder>> seedModels, Consumer<LanguageProvider> translations, GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput dataOutput = generator.getPackOutput("datapacks/" + modid);
		generator.addProvider(event.includeServer(), (DataProvider.Factory<PackMetadataGenerator>) output -> new PackMetadataGenerator(dataOutput) {
			@Override
			public String getName() {
				return "DataPack Metadata " + modid;
			}
		}.add(PackMetadataSection.TYPE, new PackMetadataSection(Component.translatable("agricraft.datapacks." + modid), DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA), Optional.empty())));
		generator.addProvider(event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(dataOutput, event.getLookupProvider(), registrySetBuilder, Set.of(modid)) {
					@Override
					public String getName() {
						return "Registries " + modid;
					}
				}
		);
		PackOutput resourceOutput = generator.getPackOutput("resourcepacks/" + modid);
		generator.addProvider(event.includeClient(), (DataProvider.Factory<PackMetadataGenerator>) output -> new PackMetadataGenerator(resourceOutput) {
			@Override
			public String getName() {
				return "ResourcePack Metadata " + modid;
			}
		}.add(PackMetadataSection.TYPE, new PackMetadataSection(Component.translatable("agricraft.resourcepacks." + modid), DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES), Optional.empty())));
		generator.addProvider(event.includeClient(), new ModelProvider<BlockModelBuilder>(resourceOutput, modid, "crop", BlockModelBuilder::new, event.getExistingFileHelper()) {
			@Override
			protected void registerModels() {
				blockModels.accept(this);
			}

			@Override
			public String getName() {
				return "Crop Models for " + modid;
			}
		});
		generator.addProvider(event.includeClient(), new ModelProvider<ItemModelBuilder>(resourceOutput, modid, "seed", ItemModelBuilder::new, event.getExistingFileHelper()) {
			@Override
			protected void registerModels() {
				seedModels.accept(this);
			}

			@Override
			public String getName() {
				return "Seed Models for " + modid;
			}
		});
		generator.addProvider(event.includeClient(), new LanguageProvider(resourceOutput, modid, "en_us") {
			@Override
			protected void addTranslations() {
				translations.accept(this);
			}
		});
	}

}
