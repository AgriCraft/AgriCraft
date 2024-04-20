package com.agricraft.agricraft.datagen;

import com.agricraft.agricraft.api.AgriApi;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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
		addProvider("agricraft", "weed", ModelsDatagen::registerAgricraftWeed, BlockModelBuilder::new, event.getGenerator(), event.getExistingFileHelper(), event.includeClient());
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

}
