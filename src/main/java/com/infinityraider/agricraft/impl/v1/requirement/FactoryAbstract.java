package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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
            BiFunction<Integer, P, IAgriGrowthResponse> response, Function<IAgriSoil, P> mapper, P invalid, List<ITextComponent> tooltips);

    @Override
    public GrowConditionBase<IAgriSoil.Humidity> soilHumidity(BiFunction<Integer, IAgriSoil.Humidity, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.soilProperty(response, IAgriSoil::getHumidity, IAgriSoil.Humidity.INVALID, tooltips);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Acidity> soilAcidity(BiFunction<Integer, IAgriSoil.Acidity, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.soilProperty(response, IAgriSoil::getAcidity, IAgriSoil.Acidity.INVALID, tooltips);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Nutrients> soilNutrients(BiFunction<Integer, IAgriSoil.Nutrients, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
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
    public IAgriGrowCondition fluidState(BiFunction<Integer, FluidState, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.blockStatesNearby(RequirementType.LIQUID, (str, stream) -> response.apply(str, stream.findAny().map(BlockState::getFluidState).orElse(Fluids.EMPTY.getDefaultState())), Offsets.NONE, Offsets.NONE, tooltips);
    }

    @Override
    public IAgriGrowCondition fluidClass(BiFunction<Integer, Class<? extends Fluid>, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.fluid((str, fluid) -> response.apply(str, fluid.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition fluid(IntPredicate strength, Fluid fluid) {
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response =
                (str, aFluid) -> strength.test(str) || fluid.equals(aFluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluid(response,
                ImmutableList.of(new StringTextComponent("")
                        .appendSibling(Descriptions.FLUID)
                        .appendSibling(new StringTextComponent(": "))
                        .appendSibling(new TranslationTextComponent(fluid.getDefaultState().getBlockState().getBlock().getTranslationKey()))
                ));
    }

    @Override
    public IAgriGrowCondition fluidState(IntPredicate strength, FluidState fluid) {
        BiFunction<Integer, FluidState, IAgriGrowthResponse> response =
                (str, aFluid) -> strength.test(str) || fluid.equals(aFluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluidState(response,
                ImmutableList.of(new StringTextComponent("")
                        .appendSibling(Descriptions.FLUID)
                        .appendSibling(new StringTextComponent(": "))
                        .appendSibling(new TranslationTextComponent(fluid.getBlockState().getBlock().getTranslationKey()))
                ));
    }

    @Override
    public IAgriGrowCondition fluidClass(IntPredicate strength, Class<? extends Fluid> fluid, List<ITextComponent> tooltips) {
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response =
                (str, aFluid) -> strength.test(str) || fluid.isInstance(aFluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluid(response, tooltips);
    }

    @Override
    public IAgriGrowCondition fluids(IntPredicate strength, Collection<Fluid> fluids) {
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response =
                (str, fluid) -> strength.test(str) || fluids.contains(fluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluid(response,
                ImmutableList.of(new StringTextComponent("")
                        .appendSibling(Descriptions.FLUID)
                        .appendSibling(new StringTextComponent(": "))
                        .appendSibling(AgriToolTips.collect(fluids.stream()
                                .map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslationKey())
                                .map(TranslationTextComponent::new), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition fluidStates(IntPredicate strength, Collection<FluidState> fluids) {
        BiFunction<Integer, FluidState, IAgriGrowthResponse> response =
                (str, fluid) -> strength.test(str) || fluids.contains(fluid) ? Responses.FERTILE : Responses.INFERTILE;
        return this.fluidState(response,
                ImmutableList.of(new StringTextComponent("")
                        .appendSibling(Descriptions.FLUID)
                        .appendSibling(new StringTextComponent(": "))
                        .appendSibling(AgriToolTips.collect(fluids.stream()
                                .map(fluid -> fluid.getBlockState().getBlock().getTranslationKey())
                                .map(TranslationTextComponent::new), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition biomeCategory(BiFunction<Integer, Biome.Category, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.biome((str, biome) -> response.apply(str, biome.getCategory()), tooltips);
    }

    @Override
    public IAgriGrowCondition biome(IntPredicate strength, Biome biome, ITextComponent biomeName) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, aBiome) -> strength.test(str) || biome.equals(aBiome) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.BIOME)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(biomeName)
        ));
    }

    @Override
    public IAgriGrowCondition biomeCategory(IntPredicate strength, Biome.Category category, ITextComponent categoryName) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, biome) -> strength.test(str) || biome.getCategory().equals(category) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.BIOME_CATEGORY)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(categoryName)
        ));
    }

    @Override
    public IAgriGrowCondition biomes(IntPredicate strength, Collection<Biome> biomes, Function<Biome, ITextComponent> nameFunction) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, aBiome) -> strength.test(str) || biomes.contains(aBiome) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.BIOME)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(AgriToolTips.collect(biomes.stream().map(nameFunction), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition biomeCategories(IntPredicate strength, Collection<Biome.Category> categories, Function<Biome.Category, ITextComponent> nameFunction) {
        BiFunction<Integer, Biome, IAgriGrowthResponse> response =
                (str, biome) -> strength.test(str) || categories.contains(biome.getCategory()) ? Responses.FERTILE : Responses.INFERTILE;
        return this.biome(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.BIOME)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(AgriToolTips.collect(categories.stream().map(nameFunction), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition climate(BiFunction<Integer, Biome.Climate, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.biome((str, biome) -> response.apply(str, this.getClimate(biome)), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(IntPredicate strength, Biome.Climate climate, List<ITextComponent> tooltips) {
        BiFunction<Integer, Biome.Climate, IAgriGrowthResponse> response =
                (str, aClimate) -> strength.test(str) || climate.equals(aClimate) ? Responses.FERTILE : Responses.INFERTILE;
        return this.climate(response, tooltips);
    }

    @Override
    public IAgriGrowCondition climates(IntPredicate strength, Collection<Biome.Climate> climates, List<ITextComponent> tooltips) {
        BiFunction<Integer, Biome.Climate, IAgriGrowthResponse> response =
                (str, aClimate) -> strength.test(str) || climates.contains(aClimate) ? Responses.FERTILE : Responses.INFERTILE;
        return this.climate(response, tooltips);
    }

    private Biome.Climate getClimate(Biome biome) {
        return ObfuscationReflectionHelper.getPrivateValue(Biome.class, biome, "field_242423_j");
    }

    @Override
    public IAgriGrowCondition dimension(IntPredicate strength, RegistryKey<World> dimension, ITextComponent dimensionName) {
        BiFunction<Integer, RegistryKey<World>, IAgriGrowthResponse> response =
                (str, aDimension) -> strength.test(str) || aDimension.equals(dimension) ? Responses.FERTILE : Responses.INFERTILE;
        return this.dimensionFromKey(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.DIMENSION)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(dimensionName)
        ));
    }

    @Override
    public IAgriGrowCondition dimension(IntPredicate strength, DimensionType dimension, ITextComponent dimensionName) {
        BiFunction<Integer, DimensionType, IAgriGrowthResponse> response =
                (str, aDimension) -> strength.test(str) || aDimension.equals(dimension) ? Responses.FERTILE : Responses.INFERTILE;
        return this.dimensionFromType(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.DIMENSION)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(dimensionName)
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
        return this.weed(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.WITH_WEED)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(weed.getWeedName())
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
        return this.weed(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.WITHOUT_WEED)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(weed.getWeedName())
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
    public IAgriGrowCondition blockBelow(BiFunction<Integer, Block, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return this.stateBelow((str, state) -> response.apply(str, state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition stateBelow(BiFunction<Integer, BlockState, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
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
                                         List<ITextComponent> tooltips) {
        return this.blockBelow((str, block) -> response.apply(str, block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition blocksNearby(BiFunction<Integer, Stream<Block>, IAgriGrowthResponse> response,
                                           BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tooltips) {
        return this.blockStatesNearby((str, stream) -> response.apply(str, stream.map(BlockState::getBlock)), minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition blockStatesNearby(BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response,
                                                BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tooltips) {
        return this.blockStatesNearby(RequirementType.BLOCKS_NEARBY, response, minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition tileEntitiesNearby(BiFunction<Integer, Stream<TileEntity>, IAgriGrowthResponse> response,
                                                 BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tooltips) {
        return this.tileEntitiesNearby(RequirementType.TILES_NEARBY, response, minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(BiFunction<Integer, Stream<Class<? extends Block>>, IAgriGrowthResponse> response,
                                          BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tooltips) {
        return this.blocksNearby((str, stream) -> response.apply(str, stream.map(Block::getClass)), minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition blockNearby(Block block, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(BlockState::getBlock).filter(block::equals).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, ImmutableList.of(
                Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount),
                new TranslationTextComponent(block.getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition blockStateNearby(BlockState state, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.filter(state::equals).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, ImmutableList.of(
                Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount),
                new TranslationTextComponent(state.getBlock().getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition tileEntityNearby(Predicate<CompoundNBT> filter, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<TileEntity>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(tile -> tile.write(new CompoundNBT())).filter(filter).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.tileEntitiesNearby(response, minOffset, maxOffset, ImmutableList.of(
                Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)));
    }

    @Override
    public IAgriGrowCondition blocksNearby(Collection<Block> blocks, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(BlockState::getBlock).filter(blocks::contains).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, Stream.concat(
                Stream.of(Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)),
                blocks.stream().map(Block::getTranslationKey).map(TranslationTextComponent::new)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blockStatesNearby(Collection<BlockState> states, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response = (str, stream) ->
                stream.filter(states::contains).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.blockStatesNearby(response, minOffset, maxOffset, Stream.concat(
                Stream.of(Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)),
                states.stream().map(BlockState::getBlock).map(Block::getTranslationKey).map(TranslationTextComponent::new)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tileEntitiesNearby(Collection<Predicate<CompoundNBT>> filters, int amount, BlockPos minOffset, BlockPos maxOffset) {
        BiFunction<Integer, Stream<TileEntity>, IAgriGrowthResponse> response = (str, stream) ->
                stream.map(tile -> tile.write(new CompoundNBT())).filter(nbt ->
                        filters.stream().allMatch(filter -> filter.test(nbt))
                ).count() >= amount ? Responses.FERTILE : Responses.INFERTILE;
        return this.tileEntitiesNearby(response, minOffset, maxOffset, ImmutableList.of(Descriptions.equalTo(FactoryAbstract.Descriptions.BLOCK_NEARBY, amount)));
    }

    protected abstract GrowConditionBase<Stream<BlockState>> blockStatesNearby(RequirementType type, BiFunction<Integer, Stream<BlockState>,
            IAgriGrowthResponse> response, BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tooltips);

    protected abstract GrowConditionBase<Stream<TileEntity>> tileEntitiesNearby(RequirementType type, BiFunction<Integer, Stream<TileEntity>,
            IAgriGrowthResponse> response, BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tootlips);

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
        return this.entitiesNearby(response, range, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.ENTITY_NEARBY)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(entityType.getName())
        ));
    }

    @Override
    public IAgriGrowCondition entityNearby(IntUnaryOperator strengthToAmount, Class<? extends Entity> entityClass, double range, ITextComponent entityName) {
        BiFunction<Integer, Stream<Entity>, IAgriGrowthResponse> response = (str, stream) -> {
            int amount = strengthToAmount.applyAsInt(str);
            if (amount <= 0 || amount <= stream.filter(entityClass::isInstance).count()) {
                return Responses.FERTILE;
            } else {
                return Responses.INFERTILE;
            }
        };
        return this.entitiesNearby(response, range, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.ENTITY_NEARBY)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(entityName)
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
        return this.season(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.SEASON)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(season.getDisplayName())
        ));
    }

    @Override
    public IAgriGrowCondition seasons(IntPredicate strength, Collection<AgriSeason> seasons) {
        BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response = (str, aSeason) ->
                strength.test(str) || seasons.stream().anyMatch(aSeason::matches) ? Responses.FERTILE : Responses.INFERTILE;
        return this.season(response,ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.SEASON)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(AgriToolTips.collect(seasons.stream().map(AgriSeason::getDisplayName), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition inVillage(IntPredicate strength) {
        return this.inStructure(strength, Structure.VILLAGE, StructureNames.VILLAGE);
    }

    @Override
    public IAgriGrowCondition inPillagerOutpost(IntPredicate strength) {
        return this.inStructure(strength, Structure.PILLAGER_OUTPOST, StructureNames.PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition inMineshaft(IntPredicate strength) {
        return this.inStructure(strength, Structure.MINESHAFT, StructureNames.MINESHAFT);
    }

    @Override
    public IAgriGrowCondition inMansion(IntPredicate strength) {
        return this.inStructure(strength, Structure.WOODLAND_MANSION, StructureNames.WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition inPyramid(IntPredicate strength) {
        BiFunction<Integer, Stream<Structure<?>>, IAgriGrowthResponse> response = (str, stream) -> {
            if (strength.test(str)) {
                return Responses.FERTILE;
            }
            if (stream.anyMatch(structure -> structure.equals(Structure.DESERT_PYRAMID) || structure.equals(Structure.JUNGLE_PYRAMID))) {
                return Responses.FERTILE;
            }
            return Responses.INFERTILE;
        };
        return this.structure(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.OUT_STRUCTURE)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(StructureNames.PYRAMID)
        ));
    }

    @Override
    public IAgriGrowCondition inJunglePyramid(IntPredicate strength) {
        return this.inStructure(strength, Structure.JUNGLE_PYRAMID, StructureNames.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inDesertPyramid(IntPredicate strength) {
        return this.inStructure(strength, Structure.DESERT_PYRAMID, StructureNames.DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inIgloo(IntPredicate strength) {
        return this.inStructure(strength, Structure.IGLOO, StructureNames.IGLOO);
    }

    @Override
    public IAgriGrowCondition inRuinedPortal(IntPredicate strength) {
        return this.inStructure(strength, Structure.RUINED_PORTAL, StructureNames.RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition inShipwreck(IntPredicate strength) {
        return this.inStructure(strength, Structure.SHIPWRECK, StructureNames.SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition inSwampHut(IntPredicate strength) {
        return this.inStructure(strength, Structure.SWAMP_HUT, StructureNames.SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition inStronghold(IntPredicate strength) {
        return this.inStructure(strength, Structure.STRONGHOLD, StructureNames.STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition inMonument(IntPredicate strength) {
        return this.inStructure(strength, Structure.MONUMENT, StructureNames.MONUMENT);
    }

    @Override
    public IAgriGrowCondition inOceanRuin(IntPredicate strength) {
        return this.inStructure(strength, Structure.OCEAN_RUIN, StructureNames.OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition inFortress(IntPredicate strength) {
        return this.inStructure(strength, Structure.FORTRESS, StructureNames.FORTRESS);
    }

    @Override
    public IAgriGrowCondition inEndCity(IntPredicate strength) {
        return this.inStructure(strength, Structure.END_CITY, StructureNames.END_CITY);
    }

    @Override
    public IAgriGrowCondition inBuriedTreasure(IntPredicate strength) {
        return this.inStructure(strength, Structure.BURIED_TREASURE, StructureNames.BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition inNetherFossil(IntPredicate strength) {
        return this.inStructure(strength, Structure.NETHER_FOSSIL, StructureNames.NETHER_FOSSIL);
    }

    @Override
    public IAgriGrowCondition inBastionRemnant(IntPredicate strength) {
        return this.inStructure(strength, Structure.BASTION_REMNANT, StructureNames.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition notInVillage(IntPredicate strength) {
        return this.notInStructure(strength, Structure.VILLAGE, StructureNames.VILLAGE);
    }

    @Override
    public IAgriGrowCondition notInPillagerOutpost(IntPredicate strength) {
        return this.notInStructure(strength, Structure.PILLAGER_OUTPOST, StructureNames.PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition notInMineshaft(IntPredicate strength) {
        return this.notInStructure(strength, Structure.MINESHAFT, StructureNames.MINESHAFT);
    }

    @Override
    public IAgriGrowCondition notInMansion(IntPredicate strength) {
        return this.notInStructure(strength, Structure.WOODLAND_MANSION, StructureNames.WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition notInPyramid(IntPredicate strength) {
        BiFunction<Integer, Stream<Structure<?>>, IAgriGrowthResponse> response = (str, stream) -> {
            if (strength.test(str)) {
                return Responses.FERTILE;
            }
            if (stream.anyMatch(structure -> structure.equals(Structure.DESERT_PYRAMID) || structure.equals(Structure.JUNGLE_PYRAMID))) {
                return Responses.INFERTILE;
            }
            return Responses.FERTILE;
        };
        return this.structure(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.OUT_STRUCTURE)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(StructureNames.PYRAMID)
        ));
    }

    @Override
    public IAgriGrowCondition notInJunglePyramid(IntPredicate strength) {
        return this.notInStructure(strength, Structure.JUNGLE_PYRAMID, StructureNames.JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInDesertPyramid(IntPredicate strength) {
        return this.notInStructure(strength, Structure.DESERT_PYRAMID, StructureNames.DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInIgloo(IntPredicate strength) {
        return this.notInStructure(strength, Structure.IGLOO, StructureNames.IGLOO);
    }

    @Override
    public IAgriGrowCondition notInRuinedPortal(IntPredicate strength) {
        return this.notInStructure(strength, Structure.RUINED_PORTAL, StructureNames.RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition notInShipwreck(IntPredicate strength) {
        return this.notInStructure(strength, Structure.SHIPWRECK, StructureNames.SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition notInSwampHut(IntPredicate strength) {
        return this.notInStructure(strength, Structure.SWAMP_HUT, StructureNames.SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition notInStronghold(IntPredicate strength) {
        return this.notInStructure(strength, Structure.STRONGHOLD, StructureNames.STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition notInMonument(IntPredicate strength) {
        return this.notInStructure(strength, Structure.MONUMENT, StructureNames.MONUMENT);
    }

    @Override
    public IAgriGrowCondition notInOceanRuin(IntPredicate strength) {
        return this.notInStructure(strength, Structure.OCEAN_RUIN, StructureNames.OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition notInFortress(IntPredicate strength) {
        return this.notInStructure(strength, Structure.FORTRESS, StructureNames.FORTRESS);
    }

    @Override
    public IAgriGrowCondition notInEndCity(IntPredicate strength) {
        return this.notInStructure(strength, Structure.END_CITY, StructureNames.END_CITY);
    }

    @Override
    public IAgriGrowCondition notInBuriedTreasure(IntPredicate strength) {
        return this.notInStructure(strength, Structure.BURIED_TREASURE, StructureNames.BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition notInNetherFossil(IntPredicate strength) {
        return this.notInStructure(strength, Structure.NETHER_FOSSIL, StructureNames.NETHER_FOSSIL);
    }

    public IAgriGrowCondition notInBastionRemnant(IntPredicate strength) {
        return this.notInStructure(strength, Structure.BASTION_REMNANT, StructureNames.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition inStructure(IntPredicate predicate, Structure<?> structure, ITextComponent structureName) {
        BiFunction<Integer, Stream<Structure<?>>, IAgriGrowthResponse> response =
                (str, stream) -> predicate.test(str) || stream.anyMatch(structure::equals) ? Responses.FERTILE : Responses.INFERTILE;
        return this.structure(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.IN_STRUCTURE)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(structureName)
        ));
    }

    @Override
    public IAgriGrowCondition notInStructure(IntPredicate predicate, Structure<?> structure, ITextComponent structureName) {
        BiFunction<Integer, Stream<Structure<?>>, IAgriGrowthResponse> response =
                (str, stream) -> predicate.test(str) || stream.noneMatch(structure::equals) ? Responses.FERTILE : Responses.INFERTILE;
        return this.structure(response, ImmutableList.of(new StringTextComponent("")
                .appendSibling(Descriptions.IN_STRUCTURE)
                .appendSibling(new StringTextComponent(": "))
                .appendSibling(structureName)
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
        public static ITextComponent inRange(ITextComponent base, int min, int max) {
            return new StringTextComponent("").appendSibling(base).appendSibling(new StringTextComponent("[" + min + "; " + max + "]"));
        }

        public static ITextComponent equalTo(ITextComponent base, int value) {
            return new StringTextComponent("").appendSibling(base).appendSibling(new StringTextComponent("" + value));
        }

        public static ITextComponent weed(IAgriWeed weed, IAgriGrowthStage stage) {
            return new StringTextComponent("")
                    .appendSibling(weed.getWeedName())
                    .appendSibling(new StringTextComponent(" ("))
                    .appendSibling(AgriToolTips.getGrowthTooltip(stage))
                    .appendSibling(new StringTextComponent(")"));
        }

        public static final ITextComponent SOIL = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil");
        public static final ITextComponent SOIL_HUMIDITY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.humidity");
        public static final ITextComponent SOIL_ACIDITY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.acidity");
        public static final ITextComponent SOIL_NUTRIENTS = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.nutrients");
        public static final ITextComponent LIGHT = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.light");
        public static final ITextComponent REDSTONE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.redstone");
        public static final ITextComponent FLUID = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.fluid");
        public static final ITextComponent BIOME = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.biome");
        public static final ITextComponent BIOME_CATEGORY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.biome_category");
        public static final ITextComponent DIMENSION = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dimension");
        public static final ITextComponent WITH_WEED = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.with_weed");
        public static final ITextComponent WITHOUT_WEED = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.without_weed");
        public static final List<ITextComponent> DAY = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.day"));
        public static final List<ITextComponent> DUSK = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dusk"));
        public static final List<ITextComponent> NIGHT = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.night"));
        public static final List<ITextComponent> DAWN = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dawn"));
        public static final ITextComponent BLOCK_BELOW = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.block_below");
        public static final ITextComponent BLOCK_NEARBY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.block_nearby");
        public static final ITextComponent ENTITY_NEARBY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.entity_nearby");
        private static final ITextComponent NO_RAIN = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.no_rain");
        private static final ITextComponent RAIN = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.rain");
        private static final ITextComponent NO_SNOW = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.no_snow");
        private static final ITextComponent SNOW = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.snow");
        public static final ITextComponent SEASON = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.season");
        public static final ITextComponent IN_STRUCTURE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_in");
        public static final ITextComponent OUT_STRUCTURE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_out");

        private Descriptions() {}
    }

    private static final class StructureNames {
        private static final ITextComponent VILLAGE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.village");
        private static final ITextComponent PILLAGER_OUTPOST = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.pillager_outpost");
        private static final ITextComponent MINESHAFT = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.mineshaft");
        private static final ITextComponent WOODLAND_MANSION = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.woodland_mansion");
        private static final ITextComponent PYRAMID = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.pyramid");
        private static final ITextComponent JUNGLE_PYRAMID = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.jungle_pyramid");
        private static final ITextComponent DESERT_PYRAMID = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.desert_pyramid");
        private static final ITextComponent IGLOO = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.igloo");
        private static final ITextComponent RUINED_PORTAL = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.ruined_portal");
        private static final ITextComponent SHIPWRECK = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.shipwreck");
        private static final ITextComponent SWAMP_HUT = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.swamp_hut");
        private static final ITextComponent STRONGHOLD = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.stronghold");
        private static final ITextComponent MONUMENT = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.monument");
        private static final ITextComponent OCEAN_RUIN = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.ocean_ruin");
        private static final ITextComponent FORTRESS = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.fortress");
        private static final ITextComponent END_CITY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.end_city");
        private static final ITextComponent BURIED_TREASURE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.buried_treasure");
        private static final ITextComponent NETHER_FOSSIL = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.nether_fossil");
        private static final ITextComponent BASTION_REMNANT = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".structure.bastion_remnant");

        private StructureNames() {}
    }
}
