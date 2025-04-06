package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.datagen.farmingforblockheads.MarketRecipeBuilder;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@EventBusSubscriber(modid = AgriApi.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DatagenEventHandler {

	public static final boolean farmingforblockheads = true;
	private static final boolean biomesoplenty = true;
	private static final boolean croptopia = true;
	private static final boolean farmersdelight = true;
	private static final boolean immersiveengineering = false;
	private static final boolean mysticalagriculture = true;
	private static final boolean pamhc2crops = true;

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeServer(), (DataProvider.Factory<ItemTagsProvider>) output -> new ModItemTagProvider(output, event.getLookupProvider(), CompletableFuture.completedFuture(null)));
		generator.addProvider(event.includeServer(), (DataProvider.Factory<BlockTagsProvider>) output -> new ModBlockTagProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
		generator.addProvider(event.includeServer(), (DataProvider.Factory<ModDataMapsProvider>) output -> new ModDataMapsProvider(output, event.getLookupProvider()));
		generator.addProvider(
				event.includeServer(),
				(DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
						output,
						event.getLookupProvider(),
						// The objects to generate
						new RegistrySetBuilder()
								.add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerPlants)
								.add(AgriSoil.REGISTRY_KEY, SoilsDatagen::registerSoils)
								.add(AgriMutation.REGISTRY_KEY, MutationsDatagen::registerMutations)
								.add(AgriFertilizer.REGISTRY_KEY, FertilizersDatagen::registerFertilizers)
								.add(AgriWeed.REGISTRY_KEY, WeedsDatagen::registerWeeds),
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
			addExtraDataPackProvider("biomesoplenty", new RegistrySetBuilder().add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerBiomesOPlenty).add(AgriMutation.REGISTRY_KEY, MutationsDatagen::registerBiomesOPlenty), ModelsDatagen::registerBiomesOPlentyPlant, ModelsDatagen::registerBiomesOPlentySeed, LangDatagen::biomesoplenty, event);
		}
		if (farmersdelight) {
			addExtraDataPackProvider("farmersdelight", new RegistrySetBuilder().add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerFarmersDelight).add(AgriMutation.REGISTRY_KEY, MutationsDatagen::registerFarmersDelight).add(AgriSoil.REGISTRY_KEY, SoilsDatagen::registerFarmersDelight), ModelsDatagen::registerFarmersDelightPlant, ModelsDatagen::registerFarmersDelightSeed, LangDatagen::farmersdelight, event);
		}
		if (immersiveengineering) {
			addExtraDataPackProvider("immersiveengineering", new RegistrySetBuilder().add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerImmersiveEngineering).add(AgriMutation.REGISTRY_KEY, MutationsDatagen::registerImmersiveEngineering), ModelsDatagen::registerImmersiveEngineeringPlant, ModelsDatagen::registerImmersiveEngineeringSeed, LangDatagen::immersiveengineering, event);
		}
		if (mysticalagriculture) {
			addExtraDataPackProvider("mysticalagriculture", new RegistrySetBuilder().add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerMysticalAgriculture).add(AgriSoil.REGISTRY_KEY, SoilsDatagen::registerMysticalAgriculture).add(AgriFertilizer.REGISTRY_KEY, FertilizersDatagen::registerMysticalAgriculture), ModelsDatagen::registerMysticalAgriculturePlant, ModelsDatagen::registerMysticalAgricultureSeed, LangDatagen::mysticalagriculture, event);
		}
		if (pamhc2crops) {
			addExtraDataPackProvider("pamhc2crops", new RegistrySetBuilder().add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerPamsHarvestCraft2).add(AgriMutation.REGISTRY_KEY, MutationsDatagen::registerPamsHarvestCraft2), ModelsDatagen::registerPamsHarvestCraft2Plant, ModelsDatagen::registerPamsHarvestCraft2Seed, LangDatagen::pamhc2crops, event);
		}
		if (croptopia) {
			addExtraDataPackProvider("croptopia", new RegistrySetBuilder().add(AgriPlant.REGISTRY_KEY, PlantsDatagen::registerCroptopia).add(AgriMutation.REGISTRY_KEY, MutationsDatagen::registerCroptopia), ModelsDatagen::registerCroptopiaPlant, ModelsDatagen::registerCroptopiaSeed, LangDatagen::croptopia, event);
		}

		generator.addProvider(event.includeServer(), (DataProvider.Factory<RecipeProvider>) output -> new ModRecipeProvider(output, event.getLookupProvider()));
		generator.addProvider(event.includeServer(), (DataProvider.Factory<GlobalLootModifierProvider>) output -> new ModGlobalLootModifierProvider(output, event.getLookupProvider()));
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

		if (DatagenEventHandler.pamhc2crops) {
			generator.addProvider(event.includeServer(), (DataProvider.Factory<RecipeProvider>) output -> new RecipeProvider(dataOutput, event.getLookupProvider()) {
				@Override
				protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
					super.buildRecipes(recipeOutput, holderLookup);
					if (PlantsDatagen.GENERATED_PLANTS.containsKey(modid)) {
						ResourceLocation category = ResourceLocation.fromNamespaceAndPath("agricraft", "seeds");
						ResourceLocation preset = ResourceLocation.fromNamespaceAndPath("agricraft", "any");
						for (String id : PlantsDatagen.GENERATED_PLANTS.get(modid)) {
							new MarketRecipeBuilder(AgriSeedItem.toStack(new AgriGenome(ResourceLocation.fromNamespaceAndPath(modid, id))), category, preset)
									.save(recipeOutput, ResourceLocation.fromNamespaceAndPath("agricraft", "market/agricraft/" + modid + "/" + id));
						}
					}
				}

				@Override
				public String getName() {
					return "Recipes for " + modid;
				}
			});
		}
	}

}
