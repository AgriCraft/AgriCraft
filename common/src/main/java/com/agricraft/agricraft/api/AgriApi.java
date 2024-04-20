package com.agricraft.agricraft.api;

import com.agricraft.agricraft.api.adapter.AgriAdapter;
import com.agricraft.agricraft.api.adapter.AgriAdapters;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGeneRegistry;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.AgriMutationHandler;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.SeasonLogic;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.util.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.stream.Stream;

public final class AgriApi {

	public static final String MOD_ID = "agricraft";

	public static final ResourceKey<Registry<AgriPlant>> AGRIPLANTS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "plants"));
	public static final ResourceKey<Registry<AgriWeed>> AGRIWEEDS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "weeds"));
	public static final ResourceKey<Registry<AgriSoil>> AGRISOILS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "soils"));
	public static final ResourceKey<Registry<AgriMutation>> AGRIMUTATIONS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "mutations"));
	public static final ResourceKey<Registry<AgriFertilizer>> AGRIFERTILIZERS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "fertilizers"));

	public static Optional<Registry<AgriPlant>> getPlantRegistry() {
		return Platform.get().getRegistry(AGRIPLANTS);
	}

	public static Optional<Registry<AgriPlant>> getPlantRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AGRIPLANTS);
	}

	public static Optional<ResourceLocation> getPlantId(AgriPlant plant) {
		return AgriApi.getPlantRegistry().map(registry -> registry.getKey(plant));
	}

	public static Optional<ResourceLocation> getPlantId(AgriPlant plant, RegistryAccess registryAccess) {
		return AgriApi.getPlantRegistry(registryAccess).map(registry -> registry.getKey(plant));
	}

	public static Optional<AgriPlant> getPlant(String plantId) {
		return AgriApi.getPlant(new ResourceLocation(plantId));
	}

	public static Optional<AgriPlant> getPlant(ResourceLocation plantId) {
		return AgriApi.getPlantRegistry().map(registry -> registry.get(plantId));
	}

	public static Optional<AgriPlant> getPlant(String plantId, RegistryAccess registryAccess) {
		return AgriApi.getPlant(new ResourceLocation(plantId), registryAccess);
	}

	public static Optional<AgriPlant> getPlant(ResourceLocation plantId, RegistryAccess registryAccess) {
		return AgriApi.getPlantRegistry(registryAccess).map(registry -> registry.get(plantId));
	}

	public static Optional<Registry<AgriWeed>> getWeedRegistry() {
		return Platform.get().getRegistry(AGRIWEEDS);
	}

	public static Optional<Registry<AgriWeed>> getWeedRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AGRIWEEDS);
	}

	public static Optional<AgriWeed> getWeed(String weedId) {
		return AgriApi.getWeed(new ResourceLocation(weedId));
	}

	public static Optional<AgriWeed> getWeed(ResourceLocation weedId) {
		return AgriApi.getWeedRegistry().map(registry -> registry.get(weedId));
	}

	public static Optional<AgriWeed> getWeed(String weedId, RegistryAccess registryAccess) {
		return AgriApi.getWeed(new ResourceLocation(weedId), registryAccess);
	}

	public static Optional<AgriWeed> getWeed(ResourceLocation weedId, RegistryAccess registryAccess) {
		return AgriApi.getWeedRegistry(registryAccess).map(registry -> registry.get(weedId));
	}

	public static Optional<Registry<AgriSoil>> getSoilRegistry() {
		return Platform.get().getRegistry(AGRISOILS);
	}

	public static Optional<Registry<AgriSoil>> getSoilRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AGRISOILS);
	}

	public static Optional<ResourceLocation> getSoilId(AgriSoil soil) {
		return AgriApi.getSoilRegistry().map(registry -> registry.getKey(soil));
	}

	public static Optional<ResourceLocation> getSoilId(AgriSoil soil, RegistryAccess registryAccess) {
		return AgriApi.getSoilRegistry(registryAccess).map(registry -> registry.getKey(soil));
	}

	public static Optional<AgriSoil> getSoil(Level level, BlockPos pos) {
		return AgriApi.getSoil(AgriApi.getSoilRegistry(level.registryAccess()), level, pos);
	}

	public static Optional<AgriSoil> getSoil(BlockGetter level, BlockPos pos) {
		return AgriApi.getSoil(AgriApi.getSoilRegistry(), level, pos);
	}

	public static Optional<AgriSoil> getSoil(BlockGetter level, BlockPos pos, RegistryAccess registryAccess) {
		return AgriApi.getSoil(AgriApi.getSoilRegistry(registryAccess), level, pos);
	}

	private static Optional<AgriSoil> getSoil(Optional<Registry<AgriSoil>> optionalRegistry, BlockGetter level, BlockPos pos) {
		if (optionalRegistry.isEmpty()) {
			return Optional.empty();
		}
		BlockState blockState = level.getBlockState(pos);
		return optionalRegistry.get().stream().filter(soil -> soil.isVariant(blockState)).findFirst();
	}

	public static Optional<Registry<AgriMutation>> getMutationRegistry() {
		return Platform.get().getRegistry(AGRIMUTATIONS);
	}

	public static Optional<Registry<AgriMutation>> getMutationRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AGRIMUTATIONS);
	}

	public static Stream<AgriMutation> getMutationsFromParents(String parent1, String parent2) {
		return AgriApi.getMutationRegistry().map(registry -> registry.stream()
				.filter(mutation -> {
					String p1 = mutation.parent1().toString();
					String p2 = mutation.parent2().toString();
					return (p1.equals(parent1) && p2.equals(parent2)) || (p1.equals(parent2) && p2.equals(parent1));
				})).orElse(Stream.empty());
	}

	public static AgriMutationHandler getMutationHandler() {
		return AgriMutationHandler.getInstance();
	}

	public static Optional<Registry<AgriFertilizer>> getFertilizerRegistry() {
		return Platform.get().getRegistry(AGRIFERTILIZERS);
	}

	public static Optional<Registry<AgriFertilizer>> getFertilizerRegistry(RegistryAccess registryAccess) {
		return registryAccess.registry(AGRIFERTILIZERS);
	}

	public static Optional<AgriFertilizer> getFertilizer(ItemStack stack) {
		return AgriApi.getFertilizerAdapter(stack).flatMap(adapter -> adapter.valueOf(stack));
	}

	public static Optional<AgriAdapter<AgriFertilizer>> getFertilizerAdapter(Object obj) {
		return AgriAdapters.FERTILIZER_ADAPTERS.stream().filter(adapter -> adapter.accepts(obj)).findFirst();
	}

	// getWeedRegistry()

	public static AgriGeneRegistry getGeneRegistry() {
		return AgriGeneRegistry.getInstance();
	}

	public static AgriStatRegistry getStatRegistry() {
		return AgriStatRegistry.getInstance();
	}

	public static Optional<AgriCrop> getCrop(BlockGetter level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof AgriCrop crop) {
			return Optional.of(crop);
		}
		return Optional.empty();
	}

	public static AgriGrowthConditionRegistry getGrowthConditionRegistry() {
		return AgriGrowthConditionRegistry.getInstance();
	}

	public static Optional<AgriAdapter<AgriGenome>> getGenomeAdapter(Object obj) {
		return AgriAdapters.GENOME_ADAPTERS.stream().filter(adapter -> adapter.accepts(obj)).findFirst();
	}

	public static SeasonLogic getSeasonLogic() {
		return SeasonLogic.INSTANCE;
	}

}
