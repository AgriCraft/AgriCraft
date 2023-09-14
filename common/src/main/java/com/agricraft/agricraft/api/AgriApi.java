package com.agricraft.agricraft.api;

import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGeneRegistry;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * The AgriCraft API v2
 */
public final class AgriApi {

	public static final String MOD_ID = "agricraft";

	public static final ResourceKey<Registry<AgriPlant>> AGRIPLANTS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "plants"));
	public static final ResourceKey<Registry<AgriSoil>> AGRISOILS = ResourceKey.createRegistryKey(new ResourceLocation(AgriApi.MOD_ID, "soils"));

	public static Optional<Registry<AgriPlant>> getPlantRegistry() {
		return PlatformUtils.getRegistry(AGRIPLANTS);
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

	public static Optional<Registry<AgriSoil>> getSoilRegistry() {
		return PlatformUtils.getRegistry(AGRISOILS);
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

}
