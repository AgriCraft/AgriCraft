package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.TagUtils;
import com.agricraft.agricraft.api.codecs.AgriBlockCondition;
import com.agricraft.agricraft.api.codecs.AgriFluidCondition;
import com.agricraft.agricraft.api.codecs.AgriListCondition;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.registries.AgriCraftGrowthConditions;
import com.agricraft.agricraft.api.registries.AgriCraftStats;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.agricraft.agricraft.api.requirement.BaseGrowthCondition;
import com.agricraft.agricraft.api.requirement.SoilGrowthCondition;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.agricraft.agricraft.common.registry.AgriRegistries.GROWTH_CONDITIONS;

/**
 * Encapsulate the growth conditions of a crop
 */
public interface ModGrowthConditions {

	// TODO: @Ketheroth warn if no soil matches this requirement

	@ApiStatus.Internal
	static void register() {
		AgriCraftGrowthConditions.HUMIDITY = GROWTH_CONDITIONS.register("humidity", () -> new SoilGrowthCondition<>("humidity", AgriRequirement::soilHumidity, AgriSoil::humidity));
		AgriCraftGrowthConditions.ACIDITY = GROWTH_CONDITIONS.register("acidity", () -> new SoilGrowthCondition<>("acidity", AgriRequirement::soilAcidity, AgriSoil::acidity));
		AgriCraftGrowthConditions.NUTRIENTS = GROWTH_CONDITIONS.register("nutrients", () -> new SoilGrowthCondition<>("nutrients", AgriRequirement::soilNutrients, AgriSoil::nutrients));
		AgriCraftGrowthConditions.LIGHT = GROWTH_CONDITIONS.register("light", () -> new BaseGrowthCondition<>("light", (plant, strength, value) -> {
			AgriRequirement requirement = plant.getGrowthRequirements();
			int lower = requirement.minLight() - (int) (requirement.lightToleranceFactor() * strength);
			int upper = requirement.maxLight() + (int) (requirement.lightToleranceFactor() * strength);
			return lower <= value && value <= upper ? AgriGrowthResponse.FERTILE : AgriGrowthResponse.INFERTILE;
		}, (level, blockPos) -> level.getMaxLocalRawBrightness(blockPos.above())));
		AgriCraftGrowthConditions.BLOCK = GROWTH_CONDITIONS.register("block", () -> new BaseGrowthCondition<>("block", (plant, strength, blockstate) -> {
			List<AgriBlockCondition> blockConditions = plant.getGrowthRequirements().blockConditions();
			if (blockConditions.isEmpty()) {
				return AgriGrowthResponse.FERTILE;
			}
			for (AgriBlockCondition blockCondition : blockConditions) {
				if (strength >= blockCondition.strength()) {
					continue;
				}
				List<Block> requiredBlocks = TagUtils.blocks(blockCondition.block());
				// regular block state requirement
				if (requiredBlocks.contains(blockstate.getBlock())) {
					if (blockCondition.states().isEmpty()) {
						return AgriGrowthResponse.FERTILE;
					}
					Set<String> list = blockstate.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.toSet());
					if (list.containsAll(blockCondition.states())) {
						return AgriGrowthResponse.FERTILE;
					}
				}
				return AgriGrowthResponse.INFERTILE;
			}
			return AgriGrowthResponse.FERTILE;
		}, (level, blockPos) -> level.getBlockState(blockPos.below(2))));
		AgriCraftGrowthConditions.BIOME = GROWTH_CONDITIONS.register("biome", () -> new BaseGrowthCondition<>("biome", (plant, strength, biome) -> {
			AgriListCondition listCondition = plant.getGrowthRequirements().biomes();
			if (strength >= listCondition.ignoreFromStrength() || (listCondition.blacklist() && listCondition.isEmpty())) {
				return AgriGrowthResponse.FERTILE;
			}
			if (listCondition.blacklist()) {
				if (listCondition.values().stream().anyMatch(biome::is)) {
					return AgriGrowthResponse.INFERTILE;
				}
			} else {
				if (listCondition.values().stream().noneMatch(biome::is)) {
					return AgriGrowthResponse.INFERTILE;
				}
			}
			return AgriGrowthResponse.FERTILE;
		}, LevelReader::getBiome));
		AgriCraftGrowthConditions.DIMENSION = GROWTH_CONDITIONS.register("dimension", () -> new BaseGrowthCondition<>("dimension", (plant, strength, dimension) -> {
			AgriListCondition listCondition = plant.getGrowthRequirements().dimensions();
			if (strength >= listCondition.ignoreFromStrength() || listCondition.blacklist() && listCondition.isEmpty()) {
				return AgriGrowthResponse.FERTILE;
			}
			if (listCondition.blacklist()) {
				if (listCondition.values().stream().anyMatch(dimension.location()::equals)) {
					return AgriGrowthResponse.INFERTILE;
				}
			} else {
				if (listCondition.values().stream().noneMatch(dimension.location()::equals)) {
					return AgriGrowthResponse.INFERTILE;
				}
			}
			return AgriGrowthResponse.FERTILE;
		}, (level, blockPos) -> level.dimension()));
		AgriCraftGrowthConditions.SEASON = GROWTH_CONDITIONS.register("season", () -> new BaseGrowthCondition<>("season", (plant, strength, season) -> {
			List<AgriSeason> seasons = plant.getGrowthRequirements().seasons();
			if (!AgriApi.get().getSeasonLogic().isActive()
					|| seasons.isEmpty()
					|| strength >= AgriCraftStats.STRENGTH.get().getMax()
					|| seasons.stream().anyMatch(season::matches)) {
				return AgriGrowthResponse.FERTILE;
			}
			return AgriGrowthResponse.INFERTILE;
		}, (level, blockPos) -> AgriApi.get().getSeasonLogic().getSeason(level, blockPos)));
		AgriCraftGrowthConditions.FLUID = GROWTH_CONDITIONS.register("fluid", () -> new BaseGrowthCondition<>("fluid", (plant, strength, fluid) -> {
			AgriFluidCondition fluidCondition = plant.getGrowthRequirements().fluidCondition();
			List<Fluid> requiredFluids = TagUtils.fluids(fluidCondition.fluid());
			if (requiredFluids.isEmpty()) {
				if (fluid.is(Fluids.LAVA)) {
					return AgriGrowthResponse.KILL_IT_WITH_FIRE;
				}
				return fluid.is(Fluids.EMPTY) ? AgriGrowthResponse.FERTILE : AgriGrowthResponse.LETHAL;
			} else {
				if (requiredFluids.contains(fluid.getType())) {
					if (fluidCondition.states().isEmpty()) {
						return AgriGrowthResponse.FERTILE;
					}
					Set<String> list = fluid.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.toSet());
					return list.containsAll(fluidCondition.states()) ? AgriGrowthResponse.FERTILE : AgriGrowthResponse.LETHAL;
				}
				return fluid.is(Fluids.LAVA) ? AgriGrowthResponse.KILL_IT_WITH_FIRE : AgriGrowthResponse.LETHAL;
			}
		}, Level::getFluidState));
	}

}
