package com.infinityraider.agricraft.impl.v1.requirement;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoilCondition;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class GrowthReqInitializer {
    public static IAgriGrowthRequirement initGrowthRequirement(AgriPlant plant) {
        // Run null check
        if (plant == null) {
            return AgriGrowthRequirement.getNone();
        }

        // Fetch a new growth requirement builder
        IAgriGrowthRequirement.Builder builder = AgriApi.getGrowthRequirementBuilder();

        // Define requirements
        defineHumidityReq(plant,builder);
        defineAcidityReq(plant, builder);
        defineNutritionReq(plant, builder);
        defineLightReq(plant, builder);
        defineNearbyBlocksReq(plant, builder);
        defineBiomeReq(plant, builder);
        defineDimensionReq(plant, builder);
        defineSeasonReq(plant, builder);
        defineFluidReq(plant, builder);

        // Build the growth requirement
        IAgriGrowthRequirement req = builder.build();

        // Log warning if no soils exist for this requirement combination
        if (noSoilsMatch(req)) {
            AgriCore.getLogger("agricraft").warn("Plant: \"{0}\" has no valid soils to plant on for any strength level!", plant.getId());
        }

        // Return the growth requirements
        return req;
    }

    private static void defineHumidityReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        String humidityString = plant.getRequirement().getHumiditySoilCondition().getCondition();
        IAgriSoil.Humidity humidity = IAgriSoil.Humidity.fromString(humidityString).orElse(IAgriSoil.Humidity.INVALID);
        handleSoilCriterion(
                humidity,
                builder::defineHumidity,
                plant.getRequirement().getHumiditySoilCondition().getType(),
                plant.getRequirement().getHumiditySoilCondition().getToleranceFactor(),
                () -> AgriCore.getLogger("agricraft")
                        .warn("Plant: \"{0}\" has an invalid humidity criterion (\"{1}\")!", plant.getId(), humidityString));
    }

    private static void defineAcidityReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        String acidityString = plant.getRequirement().getAciditySoilCondition().getCondition();
        IAgriSoil.Acidity acidity = IAgriSoil.Acidity.fromString(acidityString).orElse(IAgriSoil.Acidity.INVALID);
        handleSoilCriterion(
                acidity,
                builder::defineAcidity,
                plant.getRequirement().getAciditySoilCondition().getType(),
                plant.getRequirement().getAciditySoilCondition().getToleranceFactor(),
                () -> AgriCore.getLogger("agricraft")
                        .warn("Plant: \"{0}\" has an invalid acidity criterion (\"{1}\")!", plant.getId(), acidityString));
    }

    private static void defineNutritionReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        String nutrientString = plant.getRequirement().getNutrientSoilCondition().getCondition();
        IAgriSoil.Nutrients nutrients = IAgriSoil.Nutrients.fromString(nutrientString).orElse(IAgriSoil.Nutrients.INVALID);
        handleSoilCriterion(
                nutrients,
                builder::defineNutrients,
                plant.getRequirement().getNutrientSoilCondition().getType(),
                plant.getRequirement().getNutrientSoilCondition().getToleranceFactor(),
                () -> AgriCore.getLogger("agricraft")
                        .warn("Plant: \"{0}\" has an invalid nutrients criterion (\"{1}\")!", plant.getId(), nutrientString));
    }

    private static void defineLightReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        final double f = plant.getRequirement().getLightToleranceFactor();
        final int minLight = plant.getRequirement().getMinLight();
        final int maxLight = plant.getRequirement().getMaxLight();
        builder.defineLightLevel((strength, light) -> {
            int lower = minLight - (int) (f * strength);
            int upper = maxLight + (int) (f * strength);
            return light >= lower && light <= upper ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.INFERTILE;
        });
    }

    private static void defineNearbyBlocksReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        plant.getRequirement().getConditions().forEach(obj -> {
            BlockPos min = new BlockPos(obj.getMinX(), obj.getMinY(), obj.getMinZ());
            BlockPos max = new BlockPos(obj.getMaxX(), obj.getMaxY(), obj.getMaxZ());
            builder.addCondition(builder.blockStatesNearby(obj.convertAll(BlockState.class), obj.getAmount(), min, max));
        });
    }

    private static void defineBiomeReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        builder.defineBiome(new BiFunction<>() {
            private Set<Biome> cache;

            @Override
            public IAgriGrowthResponse apply(Integer str, Biome biome) {
                if (plant.getRequirement().getBiomeIgnoreStrength() >= str) {
                    return IAgriGrowthResponse.FERTILE;
                }
                if (this.cache == null) {
                    this.cache = plant.getRequirement().getBiomes().stream()
                            .map(ResourceLocation::new)
                            .map(ForgeRegistries.BIOMES::getValue)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                }
                return plant.getRequirement().isBiomeBlackList()
                        ? cache.contains(biome) ? IAgriGrowthResponse.INFERTILE : IAgriGrowthResponse.FERTILE
                        : cache.contains(biome) ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.INFERTILE;
            }
        });
    }

    private static void defineDimensionReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        builder.defineDimension(new BiFunction<>() {
            private Set<DimensionType> cache;

            @Override
            public IAgriGrowthResponse apply(Integer str, DimensionType dimension) {
                if (plant.getRequirement().getBiomeIgnoreStrength() >= str) {
                    return IAgriGrowthResponse.FERTILE;
                }
                if (this.cache == null) {
                    this.cache = AgriCraft.instance.proxy().getMinecraftServer().registryAccess().registry(Registry.DIMENSION_TYPE_REGISTRY)
                            .map(registry -> plant.getRequirement().getDimensions().stream()
                                    .map(ResourceLocation::new)
                                    .map(registry::get)
                                    .collect(Collectors.toSet()))
                            .orElseGet(() -> {
                                AgriCraft.instance.getLogger().error("Could not fetch dimension type registry");
                                return Collections.emptySet();
                            });
                }
                return plant.getRequirement().isDimensionBlackList()
                        ? cache.contains(dimension) ? IAgriGrowthResponse.INFERTILE : IAgriGrowthResponse.FERTILE
                        : cache.contains(dimension) ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.INFERTILE;
            }
        });
    }

    private static void defineSeasonReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        List<AgriSeason> seasons = plant.getRequirement().getSeasons().stream()
                .map(AgriSeason::fromString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
        if (seasons.size() > 0) {
            builder.defineSeasonality((str, season) -> {
                if (str >= AgriApi.getStatRegistry().strengthStat().getMax() || seasons.stream().anyMatch(season::matches)) {
                    return IAgriGrowthResponse.FERTILE;
                } else {
                    return IAgriGrowthResponse.INFERTILE;
                }
            });
        }
    }

    private static void defineFluidReq(AgriPlant plant, IAgriGrowthRequirement.Builder builder) {
        List<Fluid> fluids = plant.getRequirement().getFluid().convertAll(FluidState.class).stream()
                .map(FluidState::getType)
                .distinct()
                .collect(Collectors.toList());
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response = (strength, fluid) -> {
            if (fluids.size() > 0) {
                if (fluids.contains(fluid)) {
                    return IAgriGrowthResponse.FERTILE;
                }
                return fluid.equals(Fluids.LAVA) ? IAgriGrowthResponse.KILL_IT_WITH_FIRE : IAgriGrowthResponse.LETHAL;
            } else {
                if (fluid.equals(Fluids.LAVA)) {
                    return IAgriGrowthResponse.KILL_IT_WITH_FIRE;
                }
                return fluid.equals(Fluids.EMPTY) ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.LETHAL;
            }
        };
        builder.defineFluid(response);
    }

    private static <T extends Enum<T> & IAgriSoil.SoilProperty> void handleSoilCriterion(
            T criterion, Consumer<BiFunction<Integer, T, IAgriGrowthResponse>> consumer, AgriSoilCondition.Type type, double f, Runnable invalidCallback) {
        if(!criterion.isValid()) {
            invalidCallback.run();
        }
        consumer.accept((strength, humidity) -> {
            if(humidity.isValid() && criterion.isValid()) {
                int lower = type.lowerLimit(criterion.ordinal() - (int) (f * strength));
                int upper = type.upperLimit(criterion.ordinal() + (int) (f * strength));
                if(humidity.ordinal() <= upper && humidity.ordinal() >= lower) {
                    return IAgriGrowthResponse.FERTILE;
                }
            }
            return IAgriGrowthResponse.INFERTILE;
        });
    }

    private static boolean noSoilsMatch(IAgriGrowthRequirement requirement) {
        return AgriApi.getSoilRegistry().stream().noneMatch(soil -> {
            int min = AgriApi.getStatRegistry().strengthStat().getMin();
            int max = AgriApi.getStatRegistry().strengthStat().getMax();
            for(int strength = min; strength <= max; strength++) {
                boolean humidity = requirement.getSoilHumidityResponse(soil.getHumidity(), strength).isFertile();
                boolean acidity = requirement.getSoilAcidityResponse(soil.getAcidity(), strength).isFertile();
                boolean nutrients = requirement.getSoilNutrientsResponse(soil.getNutrients(), strength).isFertile();
                if(humidity && acidity && nutrients) {
                    return true;
                }
            }
            return false;
        });
    }

    private GrowthReqInitializer() {
        throw new IllegalStateException("Can't initialize the initializer");
    }
}
