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
import net.minecraft.tags.Tag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.extensions.IForgeStructure;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Intermediate implementation forwarding all default methods to the predicate ones
 */
public abstract class FactoryAbstract implements IDefaultGrowConditionFactory {
    protected FactoryAbstract() {}

    protected IAgriGrowCondition stateAt(RequirementType type, BiPredicate<Integer, BlockState> predicate,
                                         BlockPos offset, List<ITextComponent> tooltips) {
        return this.statesInRange(type, predicate, 1, 1, offset, offset, tooltips);
    }

    protected abstract IAgriGrowCondition statesInRange(RequirementType type, BiPredicate<Integer, BlockState> predicate,
                                                        int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                                        List<ITextComponent> tooltips);

    @Override
    public IAgriGrowCondition soil(IntPredicate strength, IAgriSoil soil) {
        return this.soil((str, aSoil) -> strength.test(str) || aSoil.equals(soil), ImmutableList.of(new StringTextComponent("")
                .append(Descriptions.SOIL)
                .append(new StringTextComponent(": "))
                .append(soil.getName())
        ));
    }

    @Override
    public IAgriGrowCondition soil(IntPredicate strength, IAgriSoil... soils) {
        return this.soil(strength, Arrays.asList(soils));
    }

    @Override
    public IAgriGrowCondition soil(IntPredicate strength, Collection<IAgriSoil> soils) {
        return this.soil((str, soil) -> strength.test(str) || soils.contains(soil), ImmutableList.of(new StringTextComponent("")
                .append(Descriptions.SOIL)
                .append(new StringTextComponent(": "))
                .append(AgriToolTips.collect(soils.stream().map(IAgriSoil::getName), ", "))
        ));
    }

    protected <P extends IAgriSoil.SoilProperty> IAgriGrowCondition soilProperty(
            IntPredicate strength, Function<IAgriSoil, P> mapper, P property, ITextComponent tooltip) {
        return this.soil((str, soil) -> strength.test(str) || mapper.apply(soil).equals(property),
                ImmutableList.of(new StringTextComponent("")
                        .append(tooltip)
                        .append(new StringTextComponent(": "))
                        .append(property.getDescription())
                ));
    }

    protected <P extends IAgriSoil.SoilProperty> IAgriGrowCondition soilProperties(
            IntPredicate strength, Function<IAgriSoil, P> mapper, Collection<P> properties, ITextComponent tooltip) {
        return this.soil((str, soil) -> strength.test(str) || properties.contains(mapper.apply(soil)),
                ImmutableList.of(new StringTextComponent("")
                        .append(tooltip)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(properties.stream().map(IAgriSoil.SoilProperty::getDescription), ", "))
                ));
    }

    protected abstract <P extends IAgriSoil.SoilProperty> GrowConditionBase<P> soilProperty(
            BiPredicate<Integer, P> predicate, Function<IAgriSoil, P> mapper, P invalid, List<ITextComponent> tooltips);

    @Override
    public IAgriGrowCondition soilHumidity(IntPredicate strength, IAgriSoil.Humidity humidity) {
        return this.soilProperty(strength, IAgriSoil::getHumidity, humidity, Descriptions.SOIL_HUMIDITY);
    }

    @Override
    public IAgriGrowCondition soilHumidities(IntPredicate strength, IAgriSoil.Humidity... humidities) {
        return this.soilHumidities(strength, Arrays.asList(humidities));
    }

    @Override
    public IAgriGrowCondition soilHumidities(IntPredicate strength, Collection<IAgriSoil.Humidity> humidities) {
        return this.soilProperties(strength, IAgriSoil::getHumidity, humidities, Descriptions.SOIL_HUMIDITY);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Humidity> soilHumidity(BiPredicate<Integer, IAgriSoil.Humidity> predicate, List<ITextComponent> tooltips) {
        return this.soilProperty(predicate, IAgriSoil::getHumidity, IAgriSoil.Humidity.INVALID, tooltips);
    }

    @Override
    public IAgriGrowCondition soilAcidity(IntPredicate strength, IAgriSoil.Acidity acidity) {
        return this.soilProperty(strength, IAgriSoil::getAcidity, acidity, Descriptions.SOIL_ACIDITY);
    }

    @Override
    public IAgriGrowCondition soilAcidities(IntPredicate strength, IAgriSoil.Acidity... acidities) {
        return this.soilAcidities(strength, Arrays.asList(acidities));
    }

    @Override
    public IAgriGrowCondition soilAcidities(IntPredicate strength, Collection<IAgriSoil.Acidity> acidities) {
        return this.soilProperties(strength, IAgriSoil::getAcidity, acidities, Descriptions.SOIL_ACIDITY);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Acidity> soilAcidity(BiPredicate<Integer, IAgriSoil.Acidity> predicate, List<ITextComponent> tooltips) {
        return this.soilProperty(predicate, IAgriSoil::getAcidity, IAgriSoil.Acidity.INVALID, tooltips);
    }

    @Override
    public IAgriGrowCondition soilNutrients(IntPredicate strength, IAgriSoil.Nutrients nutrients) {
        return this.soilProperty(strength, IAgriSoil::getAcidity, nutrients, Descriptions.SOIL_NUTRIENTS);
    }

    @Override
    public IAgriGrowCondition soilNutrients(IntPredicate strength, IAgriSoil.Nutrients... nutrients) {
        return this.soilNutrients(strength, Arrays.asList(nutrients));
    }

    @Override
    public IAgriGrowCondition soilNutrients(IntPredicate strength, Collection<IAgriSoil.Nutrients> nutrients) {
        return this.soilProperties(strength, IAgriSoil::getNutrients, nutrients, Descriptions.SOIL_NUTRIENTS);
    }

    @Override
    public GrowConditionBase<IAgriSoil.Nutrients> soilNutrients(BiPredicate<Integer, IAgriSoil.Nutrients> predicate, List<ITextComponent> tooltips) {
        return this.soilProperty(predicate, IAgriSoil::getNutrients, IAgriSoil.Nutrients.INVALID, tooltips);
    }

    @Override
    public IAgriGrowCondition light(IntPredicate strength, int min, int max) {
        return this.light((str, light) -> strength.test(str) || light >= min && light <= max,
                ImmutableList.of(Descriptions.inRange(Descriptions.LIGHT, min, max)));
    }

    @Override
    public IAgriGrowCondition light(IntPredicate strength, int value) {
        return this.light((str, light) -> strength.test(str) || light == value,
                ImmutableList.of(Descriptions.equalTo(Descriptions.LIGHT, value)));
    }

    @Override
    public IAgriGrowCondition redstone(IntPredicate strength, int min, int max) {
        return this.redstone((str, redstone) -> strength.test(str) || redstone >= min && redstone <= max,
                ImmutableList.of(Descriptions.inRange(Descriptions.REDSTONE, min, max)));
    }

    @Override
    public IAgriGrowCondition redstone(IntPredicate strength, int value) {
        return this.redstone((str, redstone) -> strength.test(str) || redstone == value,
                ImmutableList.of(Descriptions.equalTo(Descriptions.REDSTONE, value)));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(IntPredicate strength, Fluid fluid) {
        return this.liquidFromFluid(
                (str, fld) -> strength.test(str) || fld.equals(fluid),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new StringTextComponent(": "))
                        .append(new TranslationTextComponent(fluid.getDefaultState().getBlockState().getBlock().getTranslationKey()))
                ));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(IntPredicate strength, Fluid... fluids) {
        return this.liquidFromFluid(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(IntPredicate strength, Collection<Fluid> fluids) {
        return this.liquidFromFluid((str, fluid) -> strength.test(str) || fluids.contains(fluid),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(fluids.stream()
                                .map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslationKey())
                                .map(TranslationTextComponent::new), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(BiPredicate<Integer, Fluid> predicate, List<ITextComponent> tooltips) {
        return this.liquidFromState((str, state) -> predicate.test(str, state.getFluid()), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromState(IntPredicate strength, FluidState state) {
        return this.liquidFromState(
                (str, fluid) -> strength.test(str) || state.equals(fluid),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new StringTextComponent(": "))
                        .append(new TranslationTextComponent(state.getBlockState().getBlock().getTranslationKey()))
                ));
    }

    @Override
    public IAgriGrowCondition liquidFromState(IntPredicate strength, FluidState... states) {
        return this.liquidFromState(strength, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition liquidFromState(IntPredicate strength, Collection<FluidState> states) {
        return this.liquidFromState((str, state) -> strength.test(str) || states.contains(state),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(states.stream()
                                .map(fluid -> fluid.getBlockState().getBlock().getTranslationKey())
                                .map(TranslationTextComponent::new), ", "))
        ));
    }

    @Override
    public IAgriGrowCondition liquidFromState(BiPredicate<Integer, FluidState> predicate, List<ITextComponent> tooltips) {
        return this.stateAt(RequirementType.LIQUID, (str, state) -> predicate.test(str, state.getFluidState()), Offsets.NONE, tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromTag(IntPredicate strength, Tag<Fluid> tag) {
        return this.liquidFromFluid((str, fluid) -> strength.test(str) || tag.contains(fluid),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(tag.getAllElements().stream()
                                .map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslationKey())
                                .map(TranslationTextComponent::new), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition liquidFromTag(IntPredicate strength, Tag<Fluid>... fluids) {
        return this.liquidFromTag(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromTag(IntPredicate strength, Collection<Tag<Fluid>> tags) {
        return this.liquidFromFluid((str, fluid) -> strength.test(str) || tags.stream().anyMatch(tag -> tag.contains(fluid)),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.FLUID)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(tags.stream().flatMap(tag -> tag.getAllElements().stream()
                                .map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslationKey())
                                .map(TranslationTextComponent::new)), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition liquidFromClass(IntPredicate strength, Class<Fluid> clazz, List<ITextComponent> tooltips) {
        return this.liquidFromFluid((str, fluid) -> strength.test(str) || clazz.isInstance(fluid), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(IntPredicate strength, List<ITextComponent> tooltips, Class<Fluid>... fluids) {
        return this.liquidFromClass(strength, Arrays.asList(fluids), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(IntPredicate strength, Collection<Class<Fluid>> classes, List<ITextComponent> tooltips) {
        return this.liquidFromFluid((str, fluid) -> strength.test(str) || classes.stream().anyMatch(clazz -> clazz.isInstance(fluid)), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(BiPredicate<Integer, Class<? extends Fluid>> predicate, List<ITextComponent> tooltips) {
        return this.liquidFromFluid((str, fluid) -> predicate.test(str, fluid.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition biome(IntPredicate strength, Biome biome, ITextComponent biomeName) {
        return this.biome((str, aBiome) -> strength.test(str) || biome.equals(aBiome),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.BIOME)
                        .append(new StringTextComponent(": "))
                        .append(biomeName)
                ));
    }

    @Override
    public IAgriGrowCondition biome(IntPredicate strength, Function<Biome, ITextComponent> nameFunction, Biome... biomes) {
        return this.biome(strength, Arrays.asList(biomes), nameFunction);
    }

    @Override
    public IAgriGrowCondition biome(IntPredicate strength, Collection<Biome> biomes, Function<Biome, ITextComponent> nameFunction) {
        return this.biome((str, biome) -> strength.test(str) || biomes.contains(biome),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.BIOME)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(biomes.stream().map(nameFunction), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition biomeFromCategory(IntPredicate strength, Biome.Category category, ITextComponent categoryName) {
        return this.biomeFromCategory((str, aCategory) -> strength.test(str) || category.equals(aCategory),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.BIOME_CATEGORY)
                        .append(new StringTextComponent(": "))
                        .append(categoryName)
                ));
    }

    @Override
    public IAgriGrowCondition biomeFromCategories(IntPredicate strength, Function<Biome.Category, ITextComponent> nameFunction, Biome.Category... categories) {
        return this.biomeFromCategories(strength, Arrays.asList(categories), nameFunction);
    }

    @Override
    public IAgriGrowCondition biomeFromCategories(IntPredicate strength, Collection<Biome.Category> categories, Function<Biome.Category, ITextComponent> nameFunction) {
        return this.biomeFromCategory((str, category) -> strength.test(str) || categories.contains(category),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.BIOME)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(categories.stream().map(nameFunction), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition biomeFromCategory(BiPredicate<Integer, Biome.Category> predicate, List<ITextComponent> tooltips) {
        return this.biome((str, biome) -> predicate.test(str, biome.getCategory()), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(IntPredicate strength, Biome.Climate climate, List<ITextComponent> tooltips) {
        return this.climate((str, aClimate) -> strength.test(str) || climate.equals(aClimate), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(IntPredicate strength, List<ITextComponent> tooltips, Biome.Climate... climates) {
        return this.climate(strength, Arrays.asList(climates), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(IntPredicate strength, Collection<Biome.Climate> climates, List<ITextComponent> tooltips) {
        return this.climate((str, climate) -> strength.test(str) || climates.contains(climate), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(BiPredicate<Integer, Biome.Climate> predicate, List<ITextComponent> tooltips) {
        return this.biome((str, biome) -> predicate.test(str, this.getClimate(biome)), tooltips);
    }

    private Biome.Climate getClimate(Biome biome) {
        return ObfuscationReflectionHelper.getPrivateValue(Biome.class, biome, "field_242423_j");
    }

    @Override
    public IAgriGrowCondition dimension(IntPredicate strength, ResourceLocation dimension, ITextComponent dimensionName) {
        return this.dimensionFromKey(strength, RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dimension), dimensionName);
    }

    @Override
    public IAgriGrowCondition dimensions(IntPredicate strength, Function<ResourceLocation, ITextComponent> nameFunction, ResourceLocation... dimensions) {
        return this.dimensions(strength, Arrays.asList(dimensions), nameFunction);
    }

    @Override
    public IAgriGrowCondition dimensions(IntPredicate strength, Collection<ResourceLocation> dimensions,
                                         Function<ResourceLocation, ITextComponent> nameFunction) {
        return this.dimensionsFromKeys(strength, dimensions.stream().map(dimension ->
                RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dimension)).collect(Collectors.toList()),
                (key) -> nameFunction.apply(key.getLocation()));
    }

    @Override
    public IAgriGrowCondition dimensionFromKey(IntPredicate strength, RegistryKey<World> dimension, ITextComponent dimensionName) {
        return this.dimensionFromKey((str, aDimension) -> strength.test(str) || dimension.equals(aDimension),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.DIMENSION)
                        .append(new StringTextComponent(": "))
                        .append(dimensionName)
                ));
    }

    @Override
    public IAgriGrowCondition dimensionsFromKeys(IntPredicate strength, Function<RegistryKey<World>, ITextComponent> nameFunction,
                                                 RegistryKey<World>... dimensions) {
        return this.dimensionsFromKeys(strength, Arrays.asList(dimensions), nameFunction);
    }

    @Override
    public IAgriGrowCondition dimensionsFromKeys(
            IntPredicate strength, Collection<RegistryKey<World>> dimensions, Function<RegistryKey<World>, ITextComponent> nameFunction) {
        return this.dimensionFromKey((str, dimension) -> strength.test(str) || dimensions.contains(dimension),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.DIMENSION)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(dimensions.stream().map(nameFunction), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition dimensionFromType(IntPredicate strength, DimensionType dimension, ITextComponent dimensionName) {
        return this.dimensionFromType((str, aDimension) -> strength.test(str) || dimension.equals(aDimension),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.DIMENSION)
                        .append(new StringTextComponent(": "))
                        .append(dimensionName)
                ));
    }

    @Override
    public IAgriGrowCondition dimensionFromTypes(IntPredicate strength, Function<DimensionType, ITextComponent> nameFunction, DimensionType... dimensions) {
        return this.dimensionFromTypes(strength, Arrays.asList(dimensions), nameFunction);
    }

    @Override
    public IAgriGrowCondition dimensionFromTypes(IntPredicate strength, Collection<DimensionType> dimensions, Function<DimensionType, ITextComponent> nameFunction) {
        return this.dimensionFromType((str, dim) -> strength.test(str) || dimensions.contains(dim),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.DIMENSION)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(dimensions.stream().map(nameFunction), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition withWeed(IntPredicate strength) {
        return this.weed((str, weed) -> strength.test(str) || weed.isWeed(), ImmutableList.of(Descriptions.WITH_WEED));
    }

    @Override
    public IAgriGrowCondition withoutWeed(IntPredicate strength) {
        return this.weed((str, weed) -> strength.test(str) || !weed.isWeed(), ImmutableList.of(Descriptions.WITHOUT_WEED));
    }

    @Override
    public IAgriGrowCondition withWeed(IntPredicate strength, IAgriWeed weed) {
        return this.weed((str, aWeed) -> strength.test(str) || weed.equals(aWeed),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.WITH_WEED)
                        .append(new StringTextComponent(": "))
                        .append(weed.getWeedName())
                ));
    }

    @Override
    public IAgriGrowCondition withoutWeed(IntPredicate strength, IAgriWeed weed) {
        return this.weed((str, aWeed) -> strength.test(str) || !weed.equals(aWeed),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.WITHOUT_WEED)
                        .append(new StringTextComponent(": "))
                        .append(weed.getWeedName())
                ));
    }

    @Override
    public IAgriGrowCondition weed(BiPredicate<Integer, IAgriWeed> predicate, List<ITextComponent> tooltips) {
        return this.weed((str, weed, growth) -> predicate.test(str, weed), tooltips);
    }

    @Override
    public IAgriGrowCondition withWeed(IntPredicate strength, IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weed((str, aWeed, growth) -> strength.test(str) || weed.equals(aWeed) && stage.equals(growth),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.WITH_WEED)
                        .append(new StringTextComponent(": "))
                        .append(Descriptions.weed(weed, stage))
                ));
    }

    @Override
    public IAgriGrowCondition withoutWeed(IntPredicate strength, IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weed((str, aWeed, growth) -> strength.test(str) || !(weed.equals(aWeed) && stage.equals(growth)),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.WITHOUT_WEED)
                        .append(new StringTextComponent(": "))
                        .append(Descriptions.weed(weed, stage))
                ));
    }

    @Override
    public IAgriGrowCondition day(IntPredicate strength) {
        return this.timeAllowed(strength, 0, 12000, Descriptions.DAY);
    }

    @Override
    public IAgriGrowCondition dusk(IntPredicate strength) {
        return this.timeAllowed(strength, 12000, 13000, Descriptions.DUSK);
    }

    @Override
    public IAgriGrowCondition night(IntPredicate strength) {
        return this.timeAllowed(strength, 13000, 23000, Descriptions.NIGHT);
    }

    @Override
    public IAgriGrowCondition dawn(IntPredicate strength) {
        return this.timeAllowed(strength, 23000, 24000, Descriptions.DAWN);
    }

    @Override
    public IAgriGrowCondition timeAllowed(IntPredicate strength, long min, long max, List<ITextComponent> tooltips) {
        return this.time((str, time) -> strength.test(str) || time >= min && time <= max, tooltips);
    }

    @Override
    public IAgriGrowCondition timeForbidden(IntPredicate strength, long min, long max, List<ITextComponent> tooltips) {
        return this.time((str, time) -> strength.test(str) || time < min || time > max, tooltips);
    }

    @Override
    public IAgriGrowCondition blockBelow(IntPredicate strength, Block block) {
        return this.blockBelow((str, aBlock) -> strength.test(str) || block.equals(aBlock),
                ImmutableList.of(Descriptions.BLOCK_BELOW, new TranslationTextComponent(block.getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition blockBelow(IntPredicate strength, Block... blocks) {
        return this.blockBelow(strength, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blockBelow(IntPredicate strength, Collection<Block> blocks) {
        return this.blockBelow((str, block) -> strength.test(str) || blocks.contains(block), Stream.concat(Stream.of(
                Descriptions.BLOCK_BELOW),
                blocks.stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blockBelow(BiPredicate<Integer, Block> predicate, List<ITextComponent> tooltips) {
        return this.stateBelow((str, state) -> predicate.test(str, state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition stateBelow(IntPredicate strength, BlockState state) {
        return this.stateBelow((str, aState) -> strength.test(str) || state.equals(aState),
                ImmutableList.of(Descriptions.BLOCK_BELOW, new TranslationTextComponent(state.getBlock().getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition stateBelow(IntPredicate strength, BlockState... states) {
        return this.stateBelow(strength, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition stateBelow(IntPredicate strength, Collection<BlockState> states) {
        return this.stateBelow((str, state) -> strength.test(str) || states.contains(state), Stream.concat(Stream.of(
                Descriptions.BLOCK_BELOW),
                states.stream()
                        .map(state -> state.getBlock().getTranslationKey())
                        .map(TranslationTextComponent::new)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition stateBelow(BiPredicate<Integer, BlockState> predicate, List<ITextComponent> tooltips) {
        return this.stateAt(RequirementType.BLOCK_BELOW, predicate, Offsets.BELOW, tooltips);
    }

    @Override
    public IAgriGrowCondition tagBelow(IntPredicate strength, Tag<Block> tag) {
        return this.blockBelow((str, block) -> strength.test(str) || tag.contains(block), Stream.concat(Stream.of(
                Descriptions.BLOCK_BELOW),
                tag.getAllElements().stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tagBelow(IntPredicate strength, Tag<Block>... tags) {
        return this.tagBelow(strength, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagBelow(IntPredicate strength, Collection<Tag<Block>> tags) {
        return this.blockBelow((str, block) -> strength.test(str) || tags.stream().anyMatch(tag -> tag.contains(block)),
                Stream.concat(Stream.of(
                        Descriptions.BLOCK_BELOW),
                        tags.stream().flatMap(tag -> tag.getAllElements().stream()).map(Block::getTranslatedName)
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition classBelow(IntPredicate strength, Class<Block> clazz, List<ITextComponent> tooltips) {
        return this.blockBelow((str, block) -> strength.test(str) || clazz.isInstance(block), tooltips);
    }

    @Override
    public IAgriGrowCondition classBelow(IntPredicate strength, List<ITextComponent> tooltips, Class<Block>... classes) {
        return this.classBelow(strength, Arrays.asList(classes), tooltips);
    }

    @Override
    public IAgriGrowCondition classBelow(IntPredicate strength, Collection<Class<Block>> classes, List<ITextComponent> tooltips) {
        return this.blockBelow((str, block) -> strength.test(str) || classes.stream().anyMatch(clazz -> clazz.isInstance(block)), tooltips);
    }

    @Override
    public IAgriGrowCondition classBelow(BiPredicate<Integer, Class<? extends Block>> predicate, List<ITextComponent> tooltips) {
        return this.blockBelow((str, block) -> predicate.test(str, block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block block) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, block::equals,
                ImmutableList.of(Descriptions.equalTo(Descriptions.BLOCK_NEARBY, amount), new TranslationTextComponent(block.getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block... blocks) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, blocks::contains, Stream.concat(Stream.of(
                Descriptions.equalTo(Descriptions.BLOCK_NEARBY, amount)),
                blocks.stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<Block> predicate, List<ITextComponent> tooltips) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, (state) -> predicate.test(state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState state) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, state::equals,
                ImmutableList.of(Descriptions.equalTo(Descriptions.BLOCK_NEARBY, amount), new TranslationTextComponent(state.getBlock().getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState... states) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, states::contains, Stream.concat(Stream.of(
                Descriptions.equalTo(Descriptions.BLOCK_NEARBY, amount)),
                states.stream()
                        .map(state -> state.getBlock().getTranslationKey())
                        .map(TranslationTextComponent::new)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<BlockState> predicate, List<ITextComponent> tooltips) {
        return this.statesNearby(strength, amount, amount, minOffset, maxOffset, predicate, tooltips);
    }

    @Override
    public IAgriGrowCondition tagsNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, tag::contains, Stream.concat(Stream.of(
                Descriptions.equalTo(Descriptions.BLOCK_NEARBY, amount)),
                tag.getAllElements().stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tagsNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags) {
        return this.tagsNearby(strength, amount, minOffset, maxOffset, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagsNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)),
                Stream.concat(Stream.of(
                        Descriptions.equalTo(Descriptions.BLOCK_NEARBY, amount)),
                        tags.stream().flatMap(tag -> tag.getAllElements().stream()).map(Block::getTranslatedName)
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, clazz::isInstance, tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                          List<ITextComponent> tooltips, Class<Block>... classes) {
        return this.classNearby(strength, amount, minOffset, maxOffset, Arrays.asList(classes), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                          Collection<Class<Block>> classes, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset,
                (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                          Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> predicate.test(block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block block) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, block::equals,
                ImmutableList.of(Descriptions.inRange(Descriptions.BLOCK_NEARBY, min, max), new TranslationTextComponent(block.getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block... blocks) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, blocks::contains, Stream.concat(Stream.of(
                Descriptions.inRange(Descriptions.BLOCK_NEARBY, min, max)),
                blocks.stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<Block> predicate, List<ITextComponent> tooltips) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, (state) -> predicate.test(state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState state) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, state::equals,
                ImmutableList.of(Descriptions.inRange(Descriptions.BLOCK_NEARBY, min, max), new TranslationTextComponent(state.getBlock().getTranslationKey())));
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState... states) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, states::contains, Stream.concat(Stream.of(
                Descriptions.inRange(Descriptions.BLOCK_NEARBY, min, max)),
                states.stream()
                        .map(state -> state.getBlock().getTranslationKey())
                        .map(TranslationTextComponent::new)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<BlockState> predicate, List<ITextComponent> tooltips) {
        return this.statesInRange(RequirementType.BLOCKS_NEARBY, (str, state) -> strength.test(str) || predicate.test(state),
                min, max, minOffset, maxOffset, tooltips);
    }

    @Override
    public IAgriGrowCondition tagsNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, tag::contains, Stream.concat(Stream.of(
                Descriptions.inRange(Descriptions.BLOCK_NEARBY, min, max)),
                tag.getAllElements().stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tagsNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags) {
        return this.tagsNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagsNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)),
                Stream.concat(Stream.of(
                        Descriptions.inRange(Descriptions.BLOCK_NEARBY, min, max)),
                        tags.stream().flatMap(tag -> tag.getAllElements().stream()).map(Block::getTranslatedName)
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          Class<Block> clazz, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, clazz::isInstance, tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          List<ITextComponent> tooltips, Class<Block>... classes) {
        return this.classNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(classes), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          Collection<Class<Block>> classes, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset,
                (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> predicate.test(block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition entityNearby(IntPredicate strength, EntityType<?> type, double range) {
        return this.entityNearby((str, entity) -> strength.test(str) || entity.getType().equals(type), range,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.ENTITY_NEARBY)
                        .append(new StringTextComponent(": "))
                        .append(type.getName())
                ));
    }

    @Override
    public IAgriGrowCondition entityNearby(IntPredicate strength, Class<Entity> clazz, double range, ITextComponent entityName) {
        return this.entityNearby((str, entity) -> strength.test(str) || clazz.isInstance(entity), range,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.ENTITY_NEARBY)
                        .append(new StringTextComponent(": "))
                        .append(entityName)
                ));
    }

    @Override
    public IAgriGrowCondition entityNearby(BiPredicate<Integer, Entity> predicate, double range, List<ITextComponent> tooltips) {
        return this.entitiesNearby(predicate, range, 1, tooltips);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(IntPredicate strength, EntityType<?> type, double range, int amount) {
        return this.entitiesNearby((str, entity) -> strength.test(str) || entity.getType().equals(type), range, amount,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.equalTo(Descriptions.ENTITY_NEARBY, amount))
                        .append(new StringTextComponent(": "))
                        .append(type.getName())
                ));
    }

    @Override
    public IAgriGrowCondition entitiesNearby(IntPredicate strength, Class<Entity> clazz, double range, int amount, ITextComponent entityName) {
        return this.entitiesNearby((str, entity) -> strength.test(str) || clazz.isInstance(entity), range, amount,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.equalTo(Descriptions.ENTITY_NEARBY, amount))
                        .append(new StringTextComponent(": "))
                        .append(entityName)
                ));
    }

    @Override
    public IAgriGrowCondition entitiesNearby(BiPredicate<Integer, Entity> predicate, double range, int amount, List<ITextComponent> tooltips) {
        return this.entitiesNearby(predicate, range, amount, amount, tooltips);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(IntPredicate strength, EntityType<?> type, double range, int min, int max) {
        return this.entitiesNearby((str, entity) -> strength.test(str) || entity.getType().equals(type), range, min, max,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.inRange(Descriptions.ENTITY_NEARBY, min, max))
                        .append(new StringTextComponent(": "))
                        .append(type.getName())
                ));
    }

    @Override
    public IAgriGrowCondition entitiesNearby(IntPredicate strength, Class<Entity> clazz, double range, int min, int max, ITextComponent entityName) {
        return this.entitiesNearby((str, entity) -> strength.test(str) || clazz.isInstance(entity), range, min, max,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.inRange(Descriptions.ENTITY_NEARBY, min, max))
                        .append(new StringTextComponent(": "))
                        .append(entityName)
                ));
    }

    @Override
    public IAgriGrowCondition inSeason(IntPredicate strength, AgriSeason season) {
        return this.season((str, aSeason) -> strength.test(str) || season.matches(aSeason),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.SEASON)
                        .append(new StringTextComponent(": "))
                        .append(season.getDisplayName())
                ));
    }

    @Override
    public IAgriGrowCondition inSeasons(IntPredicate strength, AgriSeason... seasons) {
        return this.inSeasons(strength, Arrays.asList(seasons));
    }

    @Override
    public IAgriGrowCondition inSeasons(IntPredicate strength, Collection<AgriSeason> seasons) {
        return this.season((str, season) -> strength.test(str) || seasons.stream().anyMatch(season::matches),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.SEASON)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(seasons.stream().map(AgriSeason::getDisplayName), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition notInSeason(IntPredicate strength, AgriSeason season) {
        return this.season((str, aSeason) -> strength.test(str) || !season.matches(aSeason),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.SEASON)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(Arrays.stream(AgriSeason.values())
                                .filter(s -> !s.matches(season))
                                .map(AgriSeason::getDisplayName), ", "))
                ));
    }

    @Override
    public IAgriGrowCondition notInSeasons(IntPredicate strength, AgriSeason... seasons) {
        return this.notInSeasons(strength, Arrays.asList(seasons));
    }

    @Override
    public IAgriGrowCondition notInSeasons(IntPredicate strength, Collection<AgriSeason> seasons) {
        return this.season((str, season) -> strength.test(str) || seasons.stream().noneMatch(season::matches),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.SEASON)
                        .append(new StringTextComponent(": "))
                        .append(AgriToolTips.collect(Arrays.stream(AgriSeason.values())
                                .filter(s -> seasons.stream().noneMatch(s::matches))
                                .map(AgriSeason::getDisplayName),", "))
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
        return this.structure((str, structure) -> strength.test(str) || structure == Structure.DESERT_PYRAMID || structure == Structure.JUNGLE_PYRAMID,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.IN_STRUCTURE)
                        .append(new StringTextComponent(": "))
                        .append(StructureNames.PYRAMID)
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
        return this.structure((str, structure) -> strength.test(str) || structure != Structure.DESERT_PYRAMID && structure != Structure.JUNGLE_PYRAMID,
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.OUT_STRUCTURE)
                        .append(new StringTextComponent(": "))
                        .append(StructureNames.PYRAMID)
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

    @Override
    public IAgriGrowCondition notInBastionRemnant(IntPredicate strength) {
        return this.notInStructure(strength, Structure.BASTION_REMNANT, StructureNames.BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition inStructure(IntPredicate strength, IForgeStructure structure, ITextComponent structureName) {
        return this.structure((str, aStructure) -> strength.test(str) || structure.equals(aStructure),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.IN_STRUCTURE)
                        .append(new StringTextComponent(": "))
                        .append(structureName)
                ));
    }

    @Override
    public IAgriGrowCondition notInStructure(IntPredicate strength, IForgeStructure structure, ITextComponent structureName) {
        return this.structure((str, aStructure) -> strength.test(str) || !structure.equals(aStructure),
                ImmutableList.of(new StringTextComponent("")
                        .append(Descriptions.OUT_STRUCTURE)
                        .append(new StringTextComponent(": "))
                        .append(structureName)
                ));
    }

    private static final class Offsets {
        private static final BlockPos NONE = new BlockPos(0, 0, 0);
        private static final BlockPos SOIL = new BlockPos(0, -1, 0);
        private static final BlockPos BELOW = new BlockPos(0, -2, 0);

        private Offsets() {}
    }

    private static final class Descriptions {
        private static ITextComponent inRange(ITextComponent base, int min, int max) {
            return new StringTextComponent("").append(base).append(new StringTextComponent("[" + min + "; " + max + "]"));
        }

        private static ITextComponent equalTo(ITextComponent base, int value) {
            return new StringTextComponent("").append(base).append(new StringTextComponent("" + value));
        }

        private static ITextComponent weed(IAgriWeed weed, IAgriGrowthStage stage) {
            return new StringTextComponent("")
                    .append(weed.getWeedName())
                    .append(new StringTextComponent(" ("))
                    .append(AgriToolTips.getGrowthTooltip(stage))
                    .append(new StringTextComponent(")"));
        }

        private static final ITextComponent SOIL = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil");
        private static final ITextComponent SOIL_HUMIDITY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.humidity");
        private static final ITextComponent SOIL_ACIDITY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.acidity");
        private static final ITextComponent SOIL_NUTRIENTS = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.nutrients");
        private static final ITextComponent LIGHT = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.light");
        private static final ITextComponent REDSTONE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.redstone");
        private static final ITextComponent FLUID = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.fluid");
        private static final ITextComponent BIOME = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.biome");
        private static final ITextComponent BIOME_CATEGORY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.biome_category");
        private static final ITextComponent DIMENSION = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dimension");
        private static final ITextComponent WITH_WEED = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.with_weed");
        private static final ITextComponent WITHOUT_WEED = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.without_weed");
        private static final List<ITextComponent> DAY = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.day"));
        private static final List<ITextComponent> DUSK = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dusk"));
        private static final List<ITextComponent> NIGHT = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.night"));
        private static final List<ITextComponent> DAWN = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.dawn"));
        private static final ITextComponent BLOCK_BELOW = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.block_below");
        private static final ITextComponent BLOCK_NEARBY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.block_nearby");
        private static final ITextComponent ENTITY_NEARBY = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.entity_nearby");
        private static final ITextComponent SEASON = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.season");
        private static final ITextComponent IN_STRUCTURE = new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_in");
        private static final ITextComponent OUT_STRUCTURE = new TranslationTextComponent(
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
