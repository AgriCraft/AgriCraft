package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.AgriRegistrable;
import com.agricraft.agricraft.api.AgriRegistry;
import com.agricraft.agricraft.api.codecs.AgriBlockCondition;
import com.agricraft.agricraft.api.codecs.AgriFluidCondition;
import com.agricraft.agricraft.api.codecs.AgriListCondition;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.codecs.AgriSoilCondition;
import com.agricraft.agricraft.api.codecs.AgriSoilValue;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGeneRegistry;
import com.agricraft.agricraft.api.genetic.GeneSpecies;
import com.agricraft.agricraft.api.genetic.GeneStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encapsulate the growth conditions of a crop
 */
public class AgriGrowthConditionRegistry extends AgriRegistry<AgriGrowthConditionRegistry.AgriGrowthConditionWithId> {
	private static AgriGrowthConditionRegistry INSTANCE;

	public static AgriGrowthConditionRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AgriGrowthConditionRegistry();
			INSTANCE.add(INSTANCE.humidity);
			INSTANCE.add(INSTANCE.acidity);
			INSTANCE.add(INSTANCE.nutrients);
			INSTANCE.add(INSTANCE.light);
			INSTANCE.add(INSTANCE.blocks);
			INSTANCE.add(INSTANCE.biome);
			INSTANCE.add(INSTANCE.dimension);
			INSTANCE.add(INSTANCE.fluid);
		}
		return INSTANCE;
	}

	private final AgriGrowthConditionWithId humidity;
	private final AgriGrowthConditionWithId acidity;
	private final AgriGrowthConditionWithId nutrients;
	private final AgriGrowthConditionWithId light;
	private final AgriGrowthConditionWithId blocks;
	private final AgriGrowthConditionWithId biome;
	private final AgriGrowthConditionWithId dimension;
	private final AgriGrowthConditionWithId fluid;

	private AgriGrowthConditionRegistry() {
		super();
		humidity = create("humidity", (crop, level, pos, strength) -> handleSoilCriterion(crop, crop.getPlant().requirement().soilHumidity(), AgriSoil::humidity, strength));
		acidity = create("acidity", (crop, level, pos, strength) -> handleSoilCriterion(crop, crop.getPlant().requirement().soilAcidity(), AgriSoil::acidity, strength));
		nutrients = create("nutrients", (crop, level, pos, strength) -> handleSoilCriterion(crop, crop.getPlant().requirement().soilNutrients(), AgriSoil::nutrients, strength));
		light = create("light", (crop, level, pos, strength) -> {
			double f = crop.getPlant().requirement().lightToleranceFactor();
			int minLight = crop.getPlant().requirement().minLight();
			int maxLight = crop.getPlant().requirement().maxLight();
			int lower = minLight - (int) (f * strength);
			int upper = maxLight + (int) (f * strength);
			int light = level.getMaxLocalRawBrightness(pos);
			return light >= lower && light <= upper ? AgriGrowthResponse.FERTILE : AgriGrowthResponse.INFERTILE;
		});
		blocks = create("blocks", (crop, level, pos, strength) -> {
			List<AgriBlockCondition> blockConditions = crop.getPlant().requirement().blockConditions();
			if (blockConditions.isEmpty()) {
				return AgriGrowthResponse.FERTILE;
			}
			for (AgriBlockCondition blockCondition : blockConditions) {
				if (blockCondition.strength() <= strength) {
					continue;
				}
				CompoundTag nbt = blockCondition.nbt();
				List<Block> requiredBlocks = PlatformUtils.getBlocksFromLocation(blockCondition.block());
				// regular block state requirement
				AABB area = new AABB(crop.getBlockPos().offset(blockCondition.minX(), blockCondition.minY(), blockCondition.minZ()), crop.getBlockPos().offset(blockCondition.maxX(), blockCondition.maxY(), blockCondition.maxZ()));
				if (level.getBlockStates(area)
						.filter(state -> requiredBlocks.contains(state.getBlock()))
						.filter(state -> {
							if (blockCondition.states().isEmpty()) {
								return true;
							}
							Set<String> list = state.getValues().entrySet().stream().map(StateHolder.PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.toSet());
							return list.containsAll(blockCondition.states());
						})
						.count() < blockCondition.amount()) {
					return AgriGrowthResponse.INFERTILE;
				}
				if (!nbt.isEmpty()) {
					// block entity requirement
					if (BlockPos.betweenClosedStream(area)
							.map(level::getBlockEntity)
							.map(BlockEntity::saveWithFullMetadata)
							.filter(tag -> blockCondition.nbt().getAllKeys().stream().allMatch(key -> tag.contains(key) && tag.getTagType(key) == blockCondition.nbt().getTagType(key) && tag.get(key).equals(blockCondition.nbt().get(key))))
							.count() < blockCondition.amount()) {
						return AgriGrowthResponse.INFERTILE;
					}
				}
			}
			return AgriGrowthResponse.FERTILE;
		});
		biome = create("biome", (crop, level, pos, strength) -> {
			AgriListCondition listCondition = crop.getPlant().requirement().biomes();
			Holder<Biome> holder = level.getBiome(pos);
			if (strength >= listCondition.ignoreFromStrength() || listCondition.blacklist() && listCondition.isEmpty()) {
				return AgriGrowthResponse.FERTILE;
			}
			if (listCondition.blacklist()) {
				if (listCondition.values().stream().anyMatch(holder::is)) {
					return AgriGrowthResponse.INFERTILE;
				}
			} else {
				if (listCondition.values().stream().noneMatch(holder::is)) {
					return AgriGrowthResponse.INFERTILE;
				}
			}
			return AgriGrowthResponse.FERTILE;
		});
		dimension = create("dimension", (crop, level, pos, strength) -> {
			AgriListCondition listCondition = crop.getPlant().requirement().dimensions();
			ResourceKey<DimensionType> resourceKey = level.dimensionTypeId();
			if (strength >= listCondition.ignoreFromStrength() || listCondition.blacklist() && listCondition.isEmpty()) {
				return AgriGrowthResponse.FERTILE;
			}
			if (listCondition.blacklist()) {
				if (listCondition.values().stream().anyMatch(resourceKey.location()::equals)) {
					return AgriGrowthResponse.INFERTILE;
				}
			} else {
				if (listCondition.values().stream().noneMatch(resourceKey.location()::equals)) {
					return AgriGrowthResponse.INFERTILE;
				}
			}
			return AgriGrowthResponse.FERTILE;
		});
		// season
		// TODO: @Ketheroth seasons
		fluid = create("fluid", (crop, level, pos, strength) -> {
			FluidState fluid = level.getFluidState(pos);
			AgriFluidCondition fluidCondition = crop.getPlant().requirement().fluidCondition();
			List<Fluid> requiredFluids = PlatformUtils.getFluidsFromLocation(fluidCondition.fluid());
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
				return fluid.is(Fluids.LAVA) ? AgriGrowthResponse.FERTILE : AgriGrowthResponse.LETHAL;
			}
		});

		// TODO: @Ketheroth warn if no soil matches this requirement
	}

	private static AgriGrowthResponse handleSoilCriterion(AgriCrop crop, AgriSoilCondition condition, Function<AgriSoil, AgriSoilValue> valueGetter, int strength) {
		Optional<AgriSoil> optionalSoil = crop.getSoil();
		if (optionalSoil.isEmpty()) {
			return AgriGrowthResponse.INFERTILE;
		}
		AgriSoil soil = optionalSoil.get();
		AgriSoilValue actual = valueGetter.apply(soil);
		int lower = condition.type().lowerLimit(condition.value().ordinal() - (int) (condition.toleranceFactor() * strength));
		int upper = condition.type().upperLimit(condition.value().ordinal() + (int) (condition.toleranceFactor() * strength));
		if (lower <= actual.ordinal() && actual.ordinal() <= upper) {
			return AgriGrowthResponse.FERTILE;
		}
		return AgriGrowthResponse.INFERTILE;
	}

	public static class AgriGrowthConditionWithId implements AgriGrowthCondition, AgriRegistrable {

		private final String id;
		private final AgriGrowthCondition condition;

		public AgriGrowthConditionWithId(String id, AgriGrowthCondition condition) {
			this.id = id;
			this.condition = condition;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public AgriGrowthResponse check(AgriCrop crop, Level level, BlockPos pos, int strength) {
			return condition.check(crop, level, pos, strength);
		}

	}
	public static AgriGrowthConditionWithId create(String id, AgriGrowthCondition condition) {
		return new AgriGrowthConditionWithId(id, condition);
	}
}
