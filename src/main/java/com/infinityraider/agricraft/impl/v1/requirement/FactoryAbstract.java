package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.Collection;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Intermediate implementation forwarding all default methods to the predicate ones
 */
public abstract class FactoryAbstract implements IDefaultGrowConditionFactory {
    protected FactoryAbstract() {}

    protected abstract <P extends IAgriSoil.SoilProperty> GrowConditionBase<P> soilProperty(
            BiFunction<Integer, P, IAgriGrowthResponse> response, Function<IAgriSoil, P> mapper, P invalid, List<Component> tooltips);

    @Override
    public GrowConditionBase<IAgriSoil.Humidity> soilHumidity(BiFunction<Integer, IAgriSoil.Humidity, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.soilProperty(response, IAgriSoil::getHumidity, IAgriSoil.Humidity.INVALID, tooltips);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Acidity> soilAcidity(BiFunction<Integer, IAgriSoil.Acidity, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.soilProperty(response, IAgriSoil::getAcidity, IAgriSoil.Acidity.INVALID, tooltips);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Nutrients> soilNutrients(BiFunction<Integer, IAgriSoil.Nutrients, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.soilProperty(response, IAgriSoil::getNutrients, IAgriSoil.Nutrients.INVALID, tooltips);
    }

    @Override
    public IAgriGrowCondition light(BiPredicate<Integer, Integer> strengthLightPredicate) {
        BiFunction<Integer, Integer, IAgriGrowthResponse> response =
                (str, light) -> strengthLightPredicate.test(str, light) ? Responses.FERTILE : Responses.INFERTILE;
        return this.light(response, ImmutableList.of(Descriptions.LIGHT));
    }

    @Override
    public IAgriGrowCondition redstone(BiPredicate<Integer, Integer> strengthRedstonePredicate) {
        BiFunction<Integer, Integer, IAgriGrowthResponse> response =
                (str, redstone) -> strengthRedstonePredicate.test(str, redstone) ? Responses.FERTILE : Responses.INFERTILE;
        return this.redstone(response, ImmutableList.of(Descriptions.REDSTONE));
    }

    @Override
    public IAgriGrowCondition fluidState(BiFunction<Integer, FluidState, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.blockStatesNearby(RequirementType.LIQUID, (str, stream) -> response.apply(str, stream.findAny().map(BlockState::getFluidState).orElse(Fluids.EMPTY.defaultFluidState())), Offsets.NONE, Offsets.NONE, tooltips);
    }

    @Override
    public IAgriGrowCondition fluidClass(BiFunction<Integer, Class<? extends Fluid>, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.fluid((str, fluid) -> response.apply(str, fluid.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition fluid(IntPredicate strength, Fluid fluid) {
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response =
                (str, aFluid) -> strength.test(str) || fluid.equals(aFluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluid(response,
                ImmutableList.of(new TextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new TextComponent(": "))
                        .append(fluid.defaultFluidState().createLegacyBlock().getBlock().getName()))
                );
    }

    @Override
    public IAgriGrowCondition fluidState(IntPredicate strength, FluidState fluid) {
        BiFunction<Integer, FluidState, IAgriGrowthResponse> response =
                (str, aFluid) -> strength.test(str) || fluid.equals(aFluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluidState(response,
                ImmutableList.of(new TextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new TextComponent(": "))
                        .append(fluid.createLegacyBlock().getBlock().getName())
                ));
    }

    @Override
    public IAgriGrowCondition fluidClass(IntPredicate strength, Class<? extends Fluid> fluid, List<Component> tooltips) {
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response =
                (str, aFluid) -> strength.test(str) || fluid.isInstance(aFluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluid(response, tooltips);
    }

    @Override
    public IAgriGrowCondition fluids(IntPredicate strength, Collection<Fluid> fluids) {
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response =
                (str, fluid) -> strength.test(str) || fluids.contains(fluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluid(response,
                ImmutableList.of(new TextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new TextComponent(": "))
                        .append(AgriToolTips.collect(fluids.stream()
                                .map(fluid -> fluid.defaultFluidState().createLegacyBlock().getBlock().getName()), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition fluidStates(IntPredicate strength, Collection<FluidState> fluids) {
        BiFunction<Integer, FluidState, IAgriGrowthResponse> response =
                (str, fluid) -> strength.test(str) || fluids.contains(fluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluidState(response,
                ImmutableList.of(new TextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new TextComponent(": "))
                        .append(AgriToolTips.collect(fluids.stream()
                                .map(fluid -> fluid.createLegacyBlock().getBlock().getName()), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition biomeCategory(BiFunction<Integer, Biome.BiomeCategory, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.biome((str, biome) -> response.apply(str, this.getCategory(biome)), tooltips);
    }

    @Override
    public IAgriGrowCondition biome(IntPredicate strength, Biome biome, Component biomeName) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, aBiome) -> strength.test(str) || biome.equals(aBiome) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.BIOME)
                .append(new TextComponent(": "))
                .append(biomeName)
        ));
    }

    @Override
    public IAgriGrowCondition biomeCategory(IntPredicate strength, Biome.BiomeCategory category, Component categoryName) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, biome) -> strength.test(str) || this.getCategory(biome).equals(category) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.BIOME_CATEGORY)
                .append(new TextComponent(": "))
                .append(categoryName)
        ));
    }

    @Override
    public IAgriGrowCondition biomes(IntPredicate strength, Collection<Biome> biomes, Function<Biome, MutableComponent> nameFunction) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, aBiome) -> strength.test(str) || biomes.contains(aBiome) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.BIOME)
                .append(new TextComponent(": "))
                .append(AgriToolTips.collect(biomes.stream().map(nameFunction), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition biomeCategories(IntPredicate strength, Collection<Biome.BiomeCategory> categories, Function<Biome.BiomeCategory, MutableComponent> nameFunction) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, biome) -> strength.test(str) || categories.contains(this.getCategory(biome)) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.BIOME)
                .append(new TextComponent(": "))
                .append(AgriToolTips.collect(categories.stream().map(nameFunction), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition climate(BiFunction<Integer, Biome.ClimateSettings, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.biome((str, biome) -> response.apply(str, this.getClimate(biome)), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(IntPredicate strength, Biome.ClimateSettings climate, List<Component> tooltips) {
        BiFunction<Integer, Biome.ClimateSettings, IAgriGrowthResponse> response =
                (str, aClimate) -> strength.test(str) || climate.equals(aClimate) ? Responses.FERTILE : Responses.INFERTILE;
        return this.climate(response, tooltips);
    }

    @Override
    public IAgriGrowCondition climates(IntPredicate strength, Collection<Biome.ClimateSettings> climates, List<Component> tooltips) {
        BiFunction<Integer, Biome.ClimateSettings, IAgriGrowthResponse> response =
                (str, aClimate) -> strength.test(str) || climates.contains(aClimate) ? Responses.FERTILE : Responses.INFERTILE;
        return this.climate(response, tooltips);
    }

    private Biome.BiomeCategory getCategory(Biome biome) {
        return ObfuscationReflectionHelper.getPrivateValue(Biome.class, biome, "f_47442_");
    }

    private Biome.ClimateSettings getClimate(Biome biome) {
        return ObfuscationReflectionHelper.getPrivateValue(Biome.class, biome, "field_26393");
    }

    @Override
    public IAgriGrowCondition dimension(IntPredicate strength, ResourceKey<Level> dimension, Component dimensionName) {
        BiFunction<Integer, ResourceKey<Level>, IAgriGrowthResponse> response =
                (str, aDimension) -> strength.test(str) || aDimension.equals(dimension) ? Responses.FERTILE : Responses.INFERTILE;
        return this.dimensionFromKey(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.DIMENSION)
                .append(new TextComponent(": "))
                .append(dimensionName)
        ));
    }

    @Override
    public IAgriGrowCondition dimension(IntPredicate strength, DimensionType dimension, Component dimensionName) {
        BiFunction<Integer, DimensionType, IAgriGrowthResponse> response =
                (str, aDimension) -> strength.test(str) || aDimension.equals(dimension) ? Responses.FERTILE : Responses.INFERTILE;
        return this.dimensionFromType(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.DIMENSION)
                .append(new TextComponent(": "))
                .append(dimensionName)
        ));
    }

    @Override
    public IAgriGrowCondition withWeed(IntPredicate strength) {
        BiFunction<Integer, IAgriWeed, IAgriGrowthResponse> response =
                (str, aWeed) -> strength.test(str) || aWeed.isWeed() ? Responses.FERTILE : Responses.INFERTILE;
        return this.weed(response, ImmutableList.of(Descriptions.WITH_WEED));
    }

    @Override
    public IAgriGrowCondition withWeed(IntPredicate strength, IAgriWeed weed) {
        BiFunction<Integer, IAgriWeed, IAgriGrowthResponse> response =
                (str, aWeed) -> strength.test(str) || aWeed.equals(weed) ? Responses.FERTILE : Responses.INFERTILE;
        return this.weed(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.WITH_WEED)
                .append(new TextComponent(": "))
                .append(weed.getWeedName())
        ));
    }

    @Override
    public IAgriGrowCondition withoutWeed(IntPredicate strength) {
        BiFunction<Integer, IAgriWeed, IAgriGrowthResponse> response =
                (str, aWeed) -> strength.test(str) || !aWeed.isWeed() ? Responses.FERTILE : Responses.INFERTILE;
        return this.weed(response, ImmutableList.of(Descriptions.WITHOUT_WEED));
    }

    @Override
    public IAgriGrowCondition withoutWeed(IntPredicate strength, IAgriWeed weed) {
        BiFunction<Integer, IAgriWeed, IAgriGrowthResponse> response =
                (str, aWeed) -> strength.test(str) || !aWeed.equals(weed) ? Responses.FERTILE : Responses.INFERTILE;
        return this.weed(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.WITHOUT_WEED)
                .append(new TextComponent(": "))
                .append(weed.getWeedName())
        ));
    }

    @Override
    public IAgriGrowCondition day(IntPredicate strength) {
        BiFunction<Integer, Long, IAgriGrowthResponse> response =
                (str, time) -> strength.test(str) || (time >= 0 && time <= 12000) ? Responses.FERTILE : Responses.INFERTILE;
        return this.time(response, Descriptions.DAY);
    }

    @Override
    public IAgriGrowCondition dusk(IntPredicate strength) {
        BiFunction<Integer, Long, IAgriGrowthResponse> response =
                (str, time) -> strength.test(str) || (time >= 12000 && time <= 13000) ? Responses.FERTILE : Responses.INFERTILE;
        return this.time(response, Descriptions.DUSK);
    }

    @Override
    public IAgriGrowCondition night(IntPredicate strength) {
        BiFunction<Integer, Long, IAgriGrowthResponse> response =
                (str, time) -> strength.test(str) || (time >= 13000 && time <= 23000) ? Responses.FERTILE : Responses.INFERTILE;
        return this.time(response, Descriptions.NIGHT);
    }

    @Override
    public IAgriGrowCondition dawn(IntPredicate strength) {
        BiFunction<Integer, Long, IAgriGrowthResponse> response =
                (str, time) -> strength.test(str) || (time >= 23000 && time <= 24000) ? Responses.FERTILE : Responses.INFERTILE;
        return this.time(response, Descriptions.DAWN);
    }

    @Override
    public IAgriGrowCondition blockBelow(BiFunction<Integer, Block, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.stateBelow((str, state) -> response.apply(str, state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition stateBelow(BiFunction<Integer, BlockState, IAgriGrowthResponse> response, List<Component> tooltips) {
        return this.blockStatesNearby(
                RequirementType.BLOCK_BELOW,
                (strength, stream) -> response.apply(strength, stream.findAny().get()),
                Offsets.BELOW,
                Offsets.BELOW,
                tooltips
        );
    }

    @Override
    public IAgriGrowCondition classBelow(BiFunction<Integer, Class<? extends Block>, IAgriGrowthResponse> response,
                                         List<Component> tooltips) {
        return this.blockBelow((str, block) -> response.apply(str, block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition blocksNearby(BiFunction<Integer, Stream<Block>, IAgriGrowthResponse> response,
                                           BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips) {
        return this.blockStatesNearby((str, stream) -> response.apply(str, stream.map(BlockState::getBlock)), minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition blockStatesNearby(BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response,
                                                BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips) {
        return this.blockStatesNearby(RequirementType.BLOCKS_NEARBY, response, minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition tileEntitiesNearby(BiFunction<Integer, Stream<BlockEntity>, IAgriGrowthResponse> response,
                                                 BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips) {
        return this.tileEntitiesNearby(RequirementType.TILES_NEARBY, response, minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(BiFunction<Integer, Stream<Class<? extends Block>>, IAgriGrowthResponse> response,
                                          BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips) {
        return this.blocksNearby((str, stream) -> response.apply(str, stream.map(Block::getClass)), minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition blockNearby(Block block, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(BlockState::getBlock).filter(block::equals).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, ImmutableList.of(
                Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount),
                block.getName()));
    }

    @Override
    public IAgriGrowCondition blockStateNearby(BlockState state, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.filter(state::equals).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, ImmutableList.of(
                Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount),
               state.getBlock().getName()));
    }

    @Override
    public IAgriGrowCondition tileEntityNearby(Predicate<CompoundTag> filter, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockEntity>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(BlockEntity::saveWithFullMetadata).filter(filter).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.tileEntitiesNearby(response, minOffset, maxOffset, ImmutableList.of(
                Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)));
    }

    @Override
    public IAgriGrowCondition blocksNearby(Collection<Block> blocks, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(BlockState::getBlock).filter(blocks::contains).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, Stream.concat(
                Stream.of(Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)),
                blocks.stream().map(Block::getName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blockStatesNearby(Collection<BlockState> states, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.filter(states::contains).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, Stream.concat(
                Stream.of(Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)),
                states.stream().map(BlockState::getBlock).map(Block::getName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tileEntitiesNearby(Collection<Predicate<CompoundTag>> filters, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockEntity>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(BlockEntity::saveWithFullMetadata).filter(nbt ->
                        filters.stream().allMatch(filter -> filter.test(nbt))
                ).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.tileEntitiesNearby(response, minOffset, maxOffset, ImmutableList.of(Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)));
    }

    protected abstract GrowConditionBase<Stream<BlockState>> blockStatesNearby(RequirementType type, BiFunction<Integer, Stream<BlockState>,
            IAgriGrowthResponse> response, BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips);

    protected abstract GrowConditionBase<Stream<BlockEntity>> tileEntitiesNearby(RequirementType type, BiFunction<Integer, Stream<BlockEntity>,
            IAgriGrowthResponse> response, BlockPos minOffset, BlockPos maxOffset, List<Component> tootlips);

    @Override
    public IAgriGrowCondition entityNearby(IntUnaryOperator strengthToAmount, EntityType<?> entityType, double range) {
        BiFunction<Integer, Stream<Entity>, IAgriGrowthResponse> response = (str, stream) -> {
            int amount = strengthToAmount.applyAsInt(str);
            if (amount <= 0 || amount <= stream.map(Entity::getType).filter(entityType::equals).count()) {
                return Responses.FERTILE;
            } else {
                return Responses.INFERTILE;
            }
        };
        return this.entitiesNearby(response, range, ImmutableList.of(new TextComponent("")
                .append(Descriptions.ENTITY_NEARBY)
                .append(new TextComponent(": "))
                .append(entityType.getDescription())
        ));
    }

    @Override
    public IAgriGrowCondition entityNearby(IntUnaryOperator strengthToAmount, Class<? extends Entity> entityClass, double range, Component entityName) {
        BiFunction<Integer, Stream<Entity>, IAgriGrowthResponse> response = (str, stream) -> {
            int amount = strengthToAmount.applyAsInt(str);
            if (amount <= 0 || amount <= stream.filter(entityClass::isInstance).count()) {
                return Responses.FERTILE;
            } else {
                return Responses.INFERTILE;
            }
        };
        return this.entitiesNearby(response, range, ImmutableList.of(new TextComponent("")
                .append(Descriptions.ENTITY_NEARBY)
                .append(new TextComponent(": "))
                .append(entityName)
        ));
    }

    @Override
    public IAgriGrowCondition noRain(IntPredicate strength) {
       BiFunction<Integer, Boolean, IAgriGrowthResponse> response = (str, rain) ->
               strength.test(str) || !rain ? Responses.FERTILE : Responses.INFERTILE;
       return this.rain(response, ImmutableList.of(Descriptions.NO_RAIN));
    }

    @Override
    public IAgriGrowCondition inRain(IntPredicate strength) {
        BiFunction<Integer, Boolean, IAgriGrowthResponse> response = (str, rain) ->
                strength.test(str) || rain ? Responses.FERTILE : Responses.INFERTILE;
        return this.rain(response, ImmutableList.of(Descriptions.RAIN));
    }

    @Override
    public IAgriGrowCondition noSnow(IntPredicate strength) {
        BiFunction<Integer, Boolean, IAgriGrowthResponse> response = (str, snow) ->
                strength.test(str) || !snow ? Responses.FERTILE : Responses.INFERTILE;
        return this.snow(response, ImmutableList.of(Descriptions.NO_SNOW));
    }

    @Override
    public IAgriGrowCondition inSnow(IntPredicate strength) {
        BiFunction<Integer, Boolean, IAgriGrowthResponse> response = (str, snow) ->
                strength.test(str) || snow ? Responses.FERTILE : Responses.INFERTILE;
        return this.snow(response, ImmutableList.of(Descriptions.SNOW));
    }

    @Override
    public IAgriGrowCondition season(IntPredicate strength, AgriSeason season) {
        BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response = (str, aSeason) ->
                strength.test(str) || season.matches(aSeason) ? Responses.FERTILE : Responses.INFERTILE;
        return this.season(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.SEASON)
                .append(new TextComponent(": "))
                .append(season.getDisplayName())
        ));
    }

    @Override
    public IAgriGrowCondition seasons(IntPredicate strength, Collection<AgriSeason> seasons) {
        BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response = (str, aSeason) ->
                strength.test(str) || seasons.stream().anyMatch(aSeason::matches) ? Responses.FERTILE : Responses.INFERTILE;
        return this.season(response,ImmutableList.of(new TextComponent("")
                .append(Descriptions.SEASON)
                .append(new TextComponent(": "))
                .append(AgriToolTips.collect(seasons.stream().map(AgriSeason::getDisplayName), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition inVillage(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.VILLAGE, StructureNames.VILLAGE);
    }

    @Override
    public IAgriGrowCondition inPillagerOutpost(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.PILLAGER_OUTPOST, StructureNames.PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition inMineshaft(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.MINESHAFT, StructureNames.MINESHAFT);
    }

    @Override
    public IAgriGrowCondition inMansion(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.WOODLAND_MANSION, StructureNames.WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition inPyramid(IntPredicate strength) {
        BiFunction<Integer, Stream<StructureFeature<?>>, IAgriGrowthResponse> response = (str, stream) -> {
            if (strength.test(str)) {
                return Responses.FERTILE;
            }
            if (stream.anyMatch(structure -> structure.equals(StructureFeature.DESERT_PYRAMID) || structure.equals(StructureFeature.JUNGLE_TEMPLE))) {
                return Responses.FERTILE;
            }
            return Responses.INFERTILE;
        };
        return this.structure(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.OUT_STRUCTURE)
                .append(new TextComponent(": "))
                .append(StructureNames.PYRAMID)
        ));
    }

    @Override
    public IAgriGrowCondition inJunglePyramid(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.JUNGLE_TEMPLE, StructureNames.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inDesertPyramid(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.DESERT_PYRAMID, StructureNames.DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inIgloo(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.IGLOO, StructureNames.IGLOO);
    }

    @Override
    public IAgriGrowCondition inRuinedPortal(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.RUINED_PORTAL, StructureNames.RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition inShipwreck(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.SHIPWRECK, StructureNames.SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition inSwampHut(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.SWAMP_HUT, StructureNames.SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition inStronghold(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.STRONGHOLD, StructureNames.STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition inMonument(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.OCEAN_MONUMENT, StructureNames.MONUMENT);
    }

    @Override
    public IAgriGrowCondition inOceanRuin(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.OCEAN_RUIN, StructureNames.OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition inFortress(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.FORTRESS, StructureNames.FORTRESS);
    }

    @Override
    public IAgriGrowCondition inEndCity(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.END_CITY, StructureNames.END_CITY);
    }

    @Override
    public IAgriGrowCondition inBuriedTreasure(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.BURIED_TREASURE, StructureNames.BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition inNetherFossil(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.NETHER_FOSSIL, StructureNames.NETHER_FOSSIL);
    }

    @Override
    public IAgriGrowCondition inBastionRemnant(IntPredicate strength) {
        return this.inStructure(strength, StructureFeature.BASTION_REMNANT, StructureNames.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition notInVillage(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.VILLAGE, StructureNames.VILLAGE);
    }

    @Override
    public IAgriGrowCondition notInPillagerOutpost(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.PILLAGER_OUTPOST, StructureNames.PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition notInMineshaft(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.MINESHAFT, StructureNames.MINESHAFT);
    }

    @Override
    public IAgriGrowCondition notInMansion(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.WOODLAND_MANSION, StructureNames.WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition notInPyramid(IntPredicate strength) {
        BiFunction<Integer, Stream<StructureFeature<?>>, IAgriGrowthResponse> response = (str, stream) -> {
            if (strength.test(str)) {
                return Responses.FERTILE;
            }
            if (stream.anyMatch(structure -> structure.equals(StructureFeature.DESERT_PYRAMID) || structure.equals(StructureFeature.JUNGLE_TEMPLE))) {
                return Responses.INFERTILE;
            }
            return Responses.FERTILE;
        };
        return this.structure(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.OUT_STRUCTURE)
                .append(new TextComponent(": "))
                .append(StructureNames.PYRAMID)
        ));
    }

    @Override
    public IAgriGrowCondition notInJunglePyramid(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.JUNGLE_TEMPLE, StructureNames.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInDesertPyramid(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.DESERT_PYRAMID, StructureNames.DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInIgloo(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.IGLOO, StructureNames.IGLOO);
    }

    @Override
    public IAgriGrowCondition notInRuinedPortal(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.RUINED_PORTAL, StructureNames.RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition notInShipwreck(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.SHIPWRECK, StructureNames.SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition notInSwampHut(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.SWAMP_HUT, StructureNames.SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition notInStronghold(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.STRONGHOLD, StructureNames.STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition notInMonument(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.OCEAN_MONUMENT, StructureNames.MONUMENT);
    }

    @Override
    public IAgriGrowCondition notInOceanRuin(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.OCEAN_RUIN, StructureNames.OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition notInFortress(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.FORTRESS, StructureNames.FORTRESS);
    }

    @Override
    public IAgriGrowCondition notInEndCity(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.END_CITY, StructureNames.END_CITY);
    }

    @Override
    public IAgriGrowCondition notInBuriedTreasure(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.BURIED_TREASURE, StructureNames.BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition notInNetherFossil(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.NETHER_FOSSIL, StructureNames.NETHER_FOSSIL);
    }

    public IAgriGrowCondition notInBastionRemnant(IntPredicate strength) {
        return this.notInStructure(strength, StructureFeature.BASTION_REMNANT, StructureNames.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition inStructure(IntPredicate predicate, StructureFeature<?> structure, Component structureName) {
        BiFunction<Integer, Stream<StructureFeature<?>>, IAgriGrowthResponse> response =
                (str, stream) -> predicate.test(str) || stream.anyMatch(structure::equals) ? Responses.FERTILE : Responses.INFERTILE;
        return this.structure(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.IN_STRUCTURE)
                .append(new TextComponent(": "))
                .append(structureName)
        ));
    }

    @Override
    public IAgriGrowCondition notInStructure(IntPredicate predicate, StructureFeature<?> structure, Component structureName) {
        BiFunction<Integer, Stream<StructureFeature<?>>, IAgriGrowthResponse> response =
                (str, stream) -> predicate.test(str) || stream.noneMatch(structure::equals) ? Responses.FERTILE : Responses.INFERTILE;
        return this.structure(response, ImmutableList.of(new TextComponent("")
                .append(Descriptions.IN_STRUCTURE)
                .append(new TextComponent(": "))
                .append(structureName)
        ));
    }

    private static final class Offsets {
        private static final BlockPos NONE = new BlockPos(0, 0, 0);
        private static final BlockPos SOIL = new BlockPos(0, -1, 0);
        private static final BlockPos BELOW = new BlockPos(0, -2, 0);

        private Offsets() {}
    }

    public static final class Responses {
        public static final IAgriGrowthResponse FERTILE = IAgriGrowthResponse.FERTILE;
        public static final IAgriGrowthResponse INFERTILE = IAgriGrowthResponse.INFERTILE;
    }

    public static final class Descriptions {
        public static Component inRange(Component base, int min, int max) {
            return new TextComponent("").append(base).append(new TextComponent("[" + min + "; " + max + "]"));
        }

        public static Component equalTo(Component base, int value) {
            return new TextComponent("").append(base).append(new TextComponent("" + value));
        }

        public static Component weed(IAgriWeed weed, IAgriGrowthStage stage) {
            return new TextComponent("")
                    .append(weed.getWeedName())
                    .append(new TextComponent(" ("))
                    .append(AgriToolTips.getGrowthTooltip(stage))
                    .append(new TextComponent(")"));
        }

        public static final Component SOIL = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil");
        public static final Component SOIL_HUMIDITY = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.humidity");
        public static final Component SOIL_ACIDITY = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.acidity");
        public static final Component SOIL_NUTRIENTS = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.nutrients");
        public static final Component LIGHT = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.light");
        public static final Component REDSTONE = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.redstone");
        public static final Component FLUID = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.fluid");
        public static final Component BIOME = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.biome");
        public static final Component BIOME_CATEGORY = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.biome_category");
        public static final Component DIMENSION = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dimension");
        public static final Component WITH_WEED = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.with_weed");
        public static final Component WITHOUT_WEED = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.without_weed");
        public static final List<Component> DAY = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.day"));
        public static final List<Component> DUSK = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dusk"));
        public static final List<Component> NIGHT = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.night"));
        public static final List<Component> DAWN = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dawn"));
        public static final Component BLOCK_BELOW = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.block_below");
        public static final Component BLOCK_NEARBY = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.block_nearby");
        public static final Component ENTITY_NEARBY = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.entity_nearby");
        private static final Component NO_RAIN = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.no_rain");
        private static final Component RAIN = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.rain");
        private static final Component NO_SNOW = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.no_snow");
        private static final Component SNOW = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.snow");
        public static final Component SEASON = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.season");
        public static final Component IN_STRUCTURE = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_in");
        public static final Component OUT_STRUCTURE = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_out");

        private Descriptions() {}
    }

    private static final class StructureNames {
        private static final Component VILLAGE = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.village");
        private static final Component PILLAGER_OUTPOST = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.pillager_outpost");
        private static final Component MINESHAFT = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.mineshaft");
        private static final Component WOODLAND_MANSION = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.woodland_mansion");
        private static final Component PYRAMID = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.pyramid");
        private static final Component JUNGLE_PYRAMID = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.jungle_pyramid");
        private static final Component DESERT_PYRAMID = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.desert_pyramid");
        private static final Component IGLOO = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.igloo");
        private static final Component RUINED_PORTAL = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.ruined_portal");
        private static final Component SHIPWRECK = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.shipwreck");
        private static final Component SWAMP_HUT = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.swamp_hut");
        private static final Component STRONGHOLD = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.stronghold");
        private static final Component MONUMENT = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.monument");
        private static final Component OCEAN_RUIN = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.ocean_ruin");
        private static final Component FORTRESS = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.fortress");
        private static final Component END_CITY = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.end_city");
        private static final Component BURIED_TREASURE = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.buried_treasure");
        private static final Component NETHER_FOSSIL = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.nether_fossil");
        private static final Component BASTION_REMNANT = new TranslatableComponent(
                AgriCraft.instance.getModId() + ".structure.bastion_remnant");

        private StructureNames() {}
    }
}
