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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Intermediate implementation forwarding all default methods to the predicate ones
 */
public abstract class FactoryAbstract implements IDefaultGrowConditionFactory {
    public static final BlockPos OFFSET_NONE = new BlockPos(0, 0, 0);
    public static final BlockPos OFFSET_SOIL = new BlockPos(0, -1, 0);
    public static final BlockPos OFFSET_BELOW = new BlockPos(0, -2, 0);

    private static final ITextComponent TOOLTIP_SOIL = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.soil");
    private static final ITextComponent TOOLTIP_LIGHT = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.light");
    private static final ITextComponent TOOLTIP_REDSTONE = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.redstone");
    private static final ITextComponent TOOLTIP_FLUID = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.fluid");
    private static final ITextComponent TOOLTIP_BIOME = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.biome");
    private static final ITextComponent TOOLTIP_BIOME_CATEGORY = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.biome_category");
    private static final ITextComponent TOOLTIP_DIMENSION = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.dimension");
    private static final ITextComponent TOOLTIP_WITH_WEED = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.with_weed");
    private static final ITextComponent TOOLTIP_WITHOUT_WEED = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.without_weed");
    private static final List<ITextComponent> TOOLTIP_DAY = ImmutableList.of(new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.day"));
    private static final List<ITextComponent> TOOLTIP_DUSK = ImmutableList.of(new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.dusk"));
    private static final List<ITextComponent> TOOLTIP_NIGHT = ImmutableList.of(new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.night"));
    private static final List<ITextComponent> TOOLTIP_DAWN = ImmutableList.of(new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.dawn"));
    private static final ITextComponent TOOLTIP_BLOCK_BELOW = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.block_below");
    private static final ITextComponent TOOLTIP_BLOCK_NEARBY = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.block_nearby");
    private static final ITextComponent TOOLTIP_ENTITY_NEARBY = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.entity_nearby");
    private static final ITextComponent TOOLTIP_SEASON = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.season");
    private static final ITextComponent TOOLTIP_IN_STRUCTURE = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_in");
    private static final ITextComponent TOOLTIP_OUT_STRUCTURE = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".tooltip.growth_req.structure_out");

    private static ITextComponent tooltipInRange(ITextComponent base, int min, int max) {
        return new StringTextComponent("").append(base).append(new StringTextComponent("[" + min + "; " + max + "]"));
    }

    private static ITextComponent tooltipEqualTo(ITextComponent base, int value) {
        return new StringTextComponent("").append(base).append(new StringTextComponent("" + value));
    }

    private static ITextComponent tooltipWeedStage(IAgriWeed weed, IAgriGrowthStage stage) {
        return new StringTextComponent("")
                .append(weed.getWeedName())
                .append(new StringTextComponent(" ("))
                .append(AgriToolTips.getGrowthTooltip(stage))
                .append(new StringTextComponent(")"));
    }

    protected FactoryAbstract() {}

    protected abstract IAgriGrowCondition statesInRange(int strength, RequirementType type, int min, int max,
                                                        BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate,
                                                        List<ITextComponent> tooltips);

    @Override
    public IAgriGrowCondition soil(int strength, IAgriSoil soil) {
        return this.statesInRange(strength, RequirementType.SOIL, 1, 1, OFFSET_SOIL, OFFSET_SOIL, soil::isVariant,
                ImmutableList.of(TOOLTIP_SOIL, soil.getName()));
    }

    @Override
    public IAgriGrowCondition soil(int strength, IAgriSoil... soils) {
        return this.soil(strength, Arrays.asList(soils));
    }

    @Override
    public IAgriGrowCondition soil(int strength, Collection<IAgriSoil> soils) {
        return this.statesInRange(strength, RequirementType.SOIL, 1, 1, OFFSET_SOIL, OFFSET_SOIL,
                (state) -> soils.stream().anyMatch(soil -> soil.isVariant(state)),
                Stream.concat(Stream.of(TOOLTIP_SOIL), soils.stream().map(IAgriSoil::getName)).collect(Collectors.toList())
        );
    }

    @Override
    public IAgriGrowCondition light(int strength, int min, int max) {
        return this.light(strength, (light) -> light >= min && light <= max, ImmutableList.of(tooltipInRange(TOOLTIP_LIGHT, min, max)));
    }

    @Override
    public IAgriGrowCondition light(int strength, int value) {
        return this.light(strength, (light) -> light == value, ImmutableList.of(tooltipEqualTo(TOOLTIP_LIGHT, value)));
    }

    @Override
    public IAgriGrowCondition redstone(int strength, int min, int max) {
        return this.redstone(strength, (redstone) -> redstone >= min && redstone <= max, ImmutableList.of(tooltipInRange(TOOLTIP_REDSTONE, min, max)));
    }

    @Override
    public IAgriGrowCondition redstone(int strength, int value) {
        return this.redstone(strength, (redstone) -> redstone == value, ImmutableList.of(tooltipEqualTo(TOOLTIP_REDSTONE, value)));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Fluid fluid) {
        return this.liquidFromFluid(
                strength, fluid::equals, ImmutableList.of(TOOLTIP_FLUID, fluid.getDefaultState().getBlockState().getBlock().getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Fluid... fluids) {
        return this.liquidFromFluid(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Collection<Fluid> fluids) {
        return this.liquidFromFluid(strength, fluids::contains,
                Stream.concat(Stream.of(
                        TOOLTIP_FLUID),
                        fluids.stream().map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslatedName())
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition liquidFromFluid(int strength, Predicate<Fluid> predicate, List<ITextComponent> tooltips) {
        return this.liquidFromState(strength, (state) -> predicate.test(state.getFluid()), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, FluidState state) {
        return this.liquidFromState(
                strength, state::equals, ImmutableList.of(TOOLTIP_FLUID, state.getBlockState().getBlock().getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, FluidState... states) {
        return this.liquidFromState(strength, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, Collection<FluidState> states) {
        return this.liquidFromState(strength, states::contains,
                Stream.concat(Stream.of(
                        TOOLTIP_FLUID),
                        states.stream().map(fluid -> fluid.getBlockState().getBlock().getTranslatedName())
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition liquidFromState(int strength, Predicate<FluidState> predicate, List<ITextComponent> tooltips) {
        return this.statesInRange(
                strength, RequirementType.LIQUID, 1, 1, OFFSET_NONE, OFFSET_NONE, (state) -> predicate.test(state.getFluidState()), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromTag(int strength, Tag<Fluid> tag) {
        return this.liquidFromFluid(strength, tag::contains,
                Stream.concat(Stream.of(
                        TOOLTIP_FLUID),
                        tag.getAllElements().stream().map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslatedName())
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition liquidFromTag(int strength, Tag<Fluid>... fluids) {
        return this.liquidFromTag(strength, Arrays.asList(fluids));
    }

    @Override
    public IAgriGrowCondition liquidFromTag(int strength, Collection<Tag<Fluid>> tags) {
        return this.liquidFromFluid(strength, (liquid) -> tags.stream().anyMatch(fluid -> fluid.contains(liquid)),
                Stream.concat(Stream.of(
                        TOOLTIP_FLUID),
                        tags.stream().flatMap(tag -> tag.getAllElements().stream().map(fluid -> fluid.getDefaultState().getBlockState().getBlock().getTranslatedName()))
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Class<Fluid> fluid, List<ITextComponent> tooltips) {
        return this.liquidFromFluid(strength, fluid::isInstance, tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, List<ITextComponent> tooltips, Class<Fluid>... fluids) {
        return this.liquidFromClass(strength, Arrays.asList(fluids), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Collection<Class<Fluid>> fluids, List<ITextComponent> tooltips) {
        return this.liquidFromFluid(strength,liquid ->fluids.stream().anyMatch(fluid -> fluid.isInstance(liquid)), tooltips);
    }

    @Override
    public IAgriGrowCondition liquidFromClass(int strength, Predicate<Class<? extends Fluid>> predicate, List<ITextComponent> tooltips) {
        return this.liquidFromFluid(strength, liquid -> predicate.test(liquid.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition biome(int strength, Biome biome, ITextComponent biomeName) {
        return this.biome(strength, biome::equals, ImmutableList.of(TOOLTIP_BIOME, biomeName));
    }

    @Override
    public IAgriGrowCondition biome(int strength, Function<Biome, ITextComponent> nameFunction, Biome... biomes) {
        return this.biome(strength, Arrays.asList(biomes), nameFunction);
    }

    @Override
    public IAgriGrowCondition biome(int strength, Collection<Biome> biomes, Function<Biome, ITextComponent> nameFunction) {
        return this.biome(strength, biomes::contains, Stream.concat(Stream.of(TOOLTIP_BIOME),
                biomes.stream().map(nameFunction)).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition biomeFromCategory(int strength, Biome.Category category, ITextComponent categoryName) {
        return this.biomeFromCategory(strength, category::equals, ImmutableList.of(TOOLTIP_BIOME_CATEGORY, categoryName));
    }

    @Override
    public IAgriGrowCondition biomeFromCategories(int strength, Function<Biome.Category, ITextComponent> nameFunction, Biome.Category... categories) {
        return this.biomeFromCategories(strength, Arrays.asList(categories), nameFunction);
    }

    @Override
    public IAgriGrowCondition biomeFromCategories(int strength, Collection<Biome.Category> categories, Function<Biome.Category, ITextComponent> nameFunction) {
        return this.biomeFromCategory(strength, categories::contains, Stream.concat(Stream.of(TOOLTIP_BIOME),
                categories.stream().map(nameFunction)).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition biomeFromCategory(int strength, Predicate<Biome.Category> predicate, List<ITextComponent> tooltips) {
        return this.biome(strength, (biome) -> predicate.test(biome.getCategory()), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(int strength, Biome.Climate climate, List<ITextComponent> tooltips) {
        return this.climate(strength, climate::equals, tooltips);
    }

    @Override
    public IAgriGrowCondition climate(int strength, List<ITextComponent> tooltips, Biome.Climate... climates) {
        return this.climate(strength, Arrays.asList(climates), tooltips);
    }

    @Override
    public IAgriGrowCondition climate(int strength, Collection<Biome.Climate> climates, List<ITextComponent> tooltips) {
        return this.climate(strength, climates::contains, tooltips);
    }

    @Override
    public IAgriGrowCondition climate(int strength, Predicate<Biome.Climate> predicate, List<ITextComponent> tooltips) {
        return this.biome(strength, (biome) -> predicate.test(this.getClimate(biome)), tooltips);
    }

    private Biome.Climate getClimate(Biome biome) {
        return ObfuscationReflectionHelper.getPrivateValue(Biome.class, biome, "field_242423_j");
    }

    @Override
    public IAgriGrowCondition dimension(int strength, ResourceLocation dimension, ITextComponent dimensionName) {
        return this.dimensionFromKey(strength, RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dimension), dimensionName);
    }

    @Override
    public IAgriGrowCondition dimensions(int strength, Function<ResourceLocation, ITextComponent> nameFunction, ResourceLocation... dimensions) {
        return this.dimensions(strength, Arrays.asList(dimensions), nameFunction);
    }

    @Override
    public IAgriGrowCondition dimensions(int strength, Collection<ResourceLocation> dimensions,
                                         Function<ResourceLocation, ITextComponent> nameFunction) {
        return this.dimensionsFromKeys(strength, dimensions.stream().map(dimension ->
                RegistryKey.getOrCreateKey(Registry.WORLD_KEY, dimension)).collect(Collectors.toList()),
                (key) -> nameFunction.apply(key.getLocation()));
    }

    @Override
    public IAgriGrowCondition dimensionFromKey(int strength, RegistryKey<World> dimension, ITextComponent dimensionName) {
        return this.dimensionFromKey(strength, dimension::equals, ImmutableList.of(TOOLTIP_DIMENSION, dimensionName));
    }

    @Override
    public IAgriGrowCondition dimensionsFromKeys(int strength, Function<RegistryKey<World>, ITextComponent> nameFunction,
                                                 RegistryKey<World>... dimensions) {
        return this.dimensionsFromKeys(strength, Arrays.asList(dimensions), nameFunction);
    }

    @Override
    public IAgriGrowCondition dimensionsFromKeys(
            int strength, Collection<RegistryKey<World>> dimensions, Function<RegistryKey<World>, ITextComponent> nameFunction) {
        return this.dimensionFromKey(strength, dimensions::contains, Stream.concat(Stream.of(TOOLTIP_DIMENSION),
                dimensions.stream().map(nameFunction)).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition dimensionFromType(int strength, DimensionType dimension, ITextComponent dimensionName) {
        return this.dimensionFromType(strength, dimension::equals, ImmutableList.of(TOOLTIP_DIMENSION, dimensionName));
    }

    @Override
    public IAgriGrowCondition dimensionFromTypes(int strength, Function<DimensionType, ITextComponent> nameFunction, DimensionType... dimensions) {
        return this.dimensionFromTypes(strength, Arrays.asList(dimensions), nameFunction);
    }

    @Override
    public IAgriGrowCondition dimensionFromTypes(int strength, Collection<DimensionType> dimensions, Function<DimensionType, ITextComponent> nameFunction) {
        return this.dimensionFromType(strength, dimensions::contains, Stream.concat(Stream.of(TOOLTIP_DIMENSION),
                dimensions.stream().map(nameFunction)).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition withWeed(int strength) {
        return this.weed(strength, IAgriWeed::isWeed, ImmutableList.of(TOOLTIP_WITH_WEED));
    }

    @Override
    public IAgriGrowCondition withoutWeed(int strength) {
        return this.weed(strength, weed -> !weed.isWeed(), ImmutableList.of(TOOLTIP_WITHOUT_WEED));
    }

    @Override
    public IAgriGrowCondition withWeed(int strength, IAgriWeed weed) {
        return this.weed(strength, weed::equals, ImmutableList.of(TOOLTIP_WITH_WEED, weed.getWeedName()));
    }

    @Override
    public IAgriGrowCondition withoutWeed(int strength, IAgriWeed weed) {
        return this.weed(strength, aWeed -> !weed.equals(aWeed), ImmutableList.of(TOOLTIP_WITHOUT_WEED, weed.getWeedName()));
    }

    @Override
    public IAgriGrowCondition weed(int strength, Predicate<IAgriWeed> predicate, List<ITextComponent> tooltips) {
        return this.weed(strength, (weed, growth) -> predicate.test(weed), tooltips);
    }

    @Override
    public IAgriGrowCondition withWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weed(strength, (aWeed, growth) -> weed.equals(aWeed) && stage.equals(growth),
                ImmutableList.of(TOOLTIP_WITH_WEED, tooltipWeedStage(weed, stage)));
    }

    @Override
    public IAgriGrowCondition withoutWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage) {
        return this.weed(strength, (aWeed, growth) -> !(weed.equals(aWeed) && stage.equals(growth)),
                ImmutableList.of(TOOLTIP_WITHOUT_WEED, tooltipWeedStage(weed, stage)));
    }

    @Override
    public IAgriGrowCondition day(int strength) {
        return this.timeAllowed(strength, 0, 12000, TOOLTIP_DAY);
    }

    @Override
    public IAgriGrowCondition dusk(int strength) {
        return this.timeAllowed(strength, 12000, 13000, TOOLTIP_DUSK);
    }

    @Override
    public IAgriGrowCondition night(int strength) {
        return this.timeAllowed(strength, 13000, 23000, TOOLTIP_NIGHT);
    }

    @Override
    public IAgriGrowCondition dawn(int strength) {
        return this.timeAllowed(strength, 23000, 24000, TOOLTIP_DAWN);
    }

    @Override
    public IAgriGrowCondition timeAllowed(int strength, long min, long max, List<ITextComponent> tooltips) {
        return this.time(strength, (time) -> time >= min && time <= max, tooltips);
    }

    @Override
    public IAgriGrowCondition timeForbidden(int strength, long min, long max, List<ITextComponent> tooltips) {
        return this.time(strength, (time) -> time < min || time > max, tooltips);
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Block block) {
        return this.blockBelow(strength, block::equals, ImmutableList.of(TOOLTIP_BLOCK_BELOW, block.getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Block... blocks) {
        return this.blockBelow(strength, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Collection<Block> blocks) {
        return this.blockBelow(strength, blocks::contains, Stream.concat(Stream.of(
                TOOLTIP_BLOCK_BELOW),
                blocks.stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blockBelow(int strength, Predicate<Block> predicate, List<ITextComponent> tooltips) {
        return this.stateBelow(strength, state -> predicate.test(state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, BlockState state) {
        return this.stateBelow(strength, state::equals, ImmutableList.of(TOOLTIP_BLOCK_BELOW, state.getBlock().getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, BlockState... states) {
        return this.stateBelow(strength, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, Collection<BlockState> states) {
        return this.stateBelow(strength, states::contains, Stream.concat(Stream.of(
                TOOLTIP_BLOCK_BELOW),
                states.stream().map(state -> state.getBlock().getTranslatedName())
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition stateBelow(int strength, Predicate<BlockState> predicate, List<ITextComponent> tooltips) {
        return this.statesInRange(strength, RequirementType.BLOCK_BELOW, 1, 1, OFFSET_BELOW, OFFSET_BELOW, predicate, tooltips);
    }

    @Override
    public IAgriGrowCondition tagBelow(int strength, Tag<Block> tag) {
        return this.blockBelow(strength, tag::contains, Stream.concat(Stream.of(
                TOOLTIP_BLOCK_BELOW),
                tag.getAllElements().stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tagBelow(int strength, Tag<Block>... tags) {
        return this.tagBelow(strength, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagBelow(int strength, Collection<Tag<Block>> tags) {
        return this.blockBelow(strength, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)), Stream.concat(Stream.of(
                TOOLTIP_BLOCK_BELOW),
                tags.stream().flatMap(tag -> tag.getAllElements().stream()).map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Class<Block> clazz, List<ITextComponent> tooltips) {
        return this.blockBelow(strength, clazz::isInstance, tooltips);
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, List<ITextComponent> tooltips, Class<Block>... classes) {
        return this.classBelow(strength, Arrays.asList(classes), tooltips);
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Collection<Class<Block>> classes, List<ITextComponent> tooltips) {
        return this.blockBelow(strength, (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)), tooltips);
    }

    @Override
    public IAgriGrowCondition classBelow(int strength, Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips) {
        return this.blockBelow(strength, (block) -> predicate.test(block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block block) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, block::equals,
                ImmutableList.of(tooltipEqualTo(TOOLTIP_BLOCK_NEARBY, amount), block.getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block... blocks) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, blocks::contains, Stream.concat(Stream.of(
                tooltipEqualTo(TOOLTIP_BLOCK_NEARBY, amount)),
                blocks.stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<Block> predicate, List<ITextComponent> tooltips) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, (state) -> predicate.test(state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState state) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, state::equals,
                ImmutableList.of(tooltipEqualTo(TOOLTIP_BLOCK_NEARBY, amount), state.getBlock().getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState... states) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states) {
        return this.statesNearby(strength, amount, minOffset, maxOffset, states::contains, Stream.concat(Stream.of(
                tooltipEqualTo(TOOLTIP_BLOCK_NEARBY, amount)),
                states.stream().map(state -> state.getBlock().getTranslatedName())
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<BlockState> predicate, List<ITextComponent> tooltips) {
        return this.statesNearby(strength, amount, amount, minOffset, maxOffset, predicate, tooltips);
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, tag::contains, Stream.concat(Stream.of(
                tooltipEqualTo(TOOLTIP_BLOCK_NEARBY, amount)),
                tag.getAllElements().stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags) {
        return this.tagsNearby(strength, amount, minOffset, maxOffset, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)),
                Stream.concat(Stream.of(
                        tooltipEqualTo(TOOLTIP_BLOCK_NEARBY, amount)),
                        tags.stream().flatMap(tag -> tag.getAllElements().stream()).map(Block::getTranslatedName)
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, clazz::isInstance, tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                          List<ITextComponent> tooltips, Class<Block>... classes) {
        return this.classNearby(strength, amount, minOffset, maxOffset, Arrays.asList(classes), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                          Collection<Class<Block>> classes, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset,
                (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                          Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, amount, minOffset, maxOffset, (block) -> predicate.test(block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block block) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, block::equals,
                ImmutableList.of(tooltipInRange(TOOLTIP_BLOCK_NEARBY, min, max), block.getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block... blocks) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(blocks));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, blocks::contains, Stream.concat(Stream.of(
                tooltipInRange(TOOLTIP_BLOCK_NEARBY, min, max)),
                blocks.stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<Block> predicate, List<ITextComponent> tooltips) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, (state) -> predicate.test(state.getBlock()), tooltips);
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState state) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, state::equals,
                ImmutableList.of(tooltipInRange(TOOLTIP_BLOCK_NEARBY, min, max), state.getBlock().getTranslatedName()));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState... states) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(states));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states) {
        return this.statesNearby(strength, min, max, minOffset, maxOffset, states::contains, Stream.concat(Stream.of(
                tooltipInRange(TOOLTIP_BLOCK_NEARBY, min, max)),
                states.stream().map(state -> state.getBlock().getTranslatedName())
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                           Predicate<BlockState> predicate, List<ITextComponent> tooltips) {
        return this.statesInRange(strength, RequirementType.BLOCKS_NEARBY, min, max, minOffset, maxOffset, predicate, tooltips);
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, tag::contains, Stream.concat(Stream.of(
                tooltipInRange(TOOLTIP_BLOCK_NEARBY, min, max)),
                tag.getAllElements().stream().map(Block::getTranslatedName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags) {
        return this.tagsNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(tags));
    }

    @Override
    public IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> tags.stream().anyMatch(tag -> tag.contains(block)),
                Stream.concat(Stream.of(
                        tooltipInRange(TOOLTIP_BLOCK_NEARBY, min, max)),
                        tags.stream().flatMap(tag -> tag.getAllElements().stream()).map(Block::getTranslatedName)
                ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          Class<Block> clazz, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, clazz::isInstance, tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          List<ITextComponent> tooltips, Class<Block>... classes) {
        return this.classNearby(strength, min, max, minOffset, maxOffset, Arrays.asList(classes), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          Collection<Class<Block>> classes, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset,
                (block) -> classes.stream().anyMatch(clazz -> clazz.isInstance(block)), tooltips);
    }

    @Override
    public IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                          Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips) {
        return this.blocksNearby(strength, min, max, minOffset, maxOffset, (block) -> predicate.test(block.getClass()), tooltips);
    }

    @Override
    public IAgriGrowCondition entityNearby(int strength, EntityType<?> type, double range) {
        return this.entityNearby(strength, (entity) -> entity.getType().equals(type), range,
                ImmutableList.of(TOOLTIP_ENTITY_NEARBY, type.getName()));
    }

    @Override
    public IAgriGrowCondition entityNearby(int strength, Class<Entity> clazz, double range, ITextComponent entityName) {
        return this.entityNearby(strength, clazz::isInstance, range,
                ImmutableList.of(TOOLTIP_ENTITY_NEARBY, entityName));
    }

    @Override
    public IAgriGrowCondition entityNearby(int strength, Predicate<Entity> predicate, double range, List<ITextComponent> tooltips) {
        return this.entitiesNearby(strength, predicate, range, 1, tooltips);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int amount) {
        return this.entitiesNearby(strength, (entity) -> entity.getType().equals(type), range, amount,
                ImmutableList.of(tooltipEqualTo(TOOLTIP_ENTITY_NEARBY, amount), type.getName()));
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int amount, ITextComponent entityName) {
        return this.entitiesNearby(strength, clazz::isInstance, range, amount,
                ImmutableList.of(tooltipEqualTo(TOOLTIP_ENTITY_NEARBY, amount), entityName));
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int amount, List<ITextComponent> tooltips) {
        return this.entitiesNearby(strength, predicate, range, amount, amount, tooltips);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int min, int max) {
        return this.entitiesNearby(strength, (entity) -> entity.getType().equals(type), range, min, max,
                ImmutableList.of(tooltipInRange(TOOLTIP_ENTITY_NEARBY, min, max), type.getName()));
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int min, int max, ITextComponent entityName) {
        return this.entitiesNearby(strength, clazz::isInstance, range, min, max,
                ImmutableList.of(tooltipInRange(TOOLTIP_ENTITY_NEARBY, min, max), entityName));
    }

    @Override
    public IAgriGrowCondition inSeason(int strength, AgriSeason season) {
        return this.season(strength, season::matches, ImmutableList.of(TOOLTIP_SEASON, season.getDisplayName()));
    }

    @Override
    public IAgriGrowCondition inSeasons(int strength, AgriSeason... seasons) {
        return this.inSeasons(strength, Arrays.asList(seasons));
    }

    @Override
    public IAgriGrowCondition inSeasons(int strength, Collection<AgriSeason> seasons) {
        return this.season(strength, season -> seasons.stream().anyMatch(season::matches),
                Stream.concat(Stream.of(TOOLTIP_SEASON), seasons.stream().map(AgriSeason::getDisplayName)).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition notInSeason(int strength, AgriSeason season) {
        return this.season(strength, s -> !s.matches(season), Stream.concat(
                Stream.of(TOOLTIP_SEASON),
                Arrays.stream(AgriSeason.values()).filter(s -> !s.matches(season)).map(AgriSeason::getDisplayName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition notInSeasons(int strength, AgriSeason... seasons) {
        return this.notInSeasons(strength, Arrays.asList(seasons));
    }

    @Override
    public IAgriGrowCondition notInSeasons(int strength, Collection<AgriSeason> seasons) {
        return this.season(strength, season -> seasons.stream().noneMatch(season::matches), Stream.concat(
                Stream.of(TOOLTIP_SEASON),
                Arrays.stream(AgriSeason.values()).filter(s -> seasons.stream().noneMatch(s::matches)).map(AgriSeason::getDisplayName)
        ).collect(Collectors.toList()));
    }

    @Override
    public IAgriGrowCondition inVillage(int strength) {
        return this.inStructure(strength, Structure.VILLAGE, STRUCTURE_VILLAGE);
    }

    @Override
    public IAgriGrowCondition inPillagerOutpost(int strength) {
        return this.inStructure(strength, Structure.PILLAGER_OUTPOST, STRUCTURE_PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition inMineshaft(int strength) {
        return this.inStructure(strength, Structure.MINESHAFT, STRUCTURE_MINESHAFT);
    }

    @Override
    public IAgriGrowCondition inMansion(int strength) {
        return this.inStructure(strength, Structure.WOODLAND_MANSION, STRUCTURE_WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition inPyramid(int strength) {
        return this.structure(strength, structure -> structure == Structure.DESERT_PYRAMID || structure == Structure.JUNGLE_PYRAMID,
                ImmutableList.of(TOOLTIP_IN_STRUCTURE, STRUCTURE_PYRAMID));
    }

    @Override
    public IAgriGrowCondition inJunglePyramid(int strength) {
        return this.inStructure(strength, Structure.JUNGLE_PYRAMID, STRUCTURE_JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inDesertPyramid(int strength) {
        return this.inStructure(strength, Structure.DESERT_PYRAMID, STRUCTURE_DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition inIgloo(int strength) {
        return this.inStructure(strength, Structure.IGLOO, STRUCTURE_IGLOO);
    }

    @Override
    public IAgriGrowCondition inRuinedPortal(int strength) {
        return this.inStructure(strength, Structure.RUINED_PORTAL, STRUCTURE_RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition inShipwreck(int strength) {
        return this.inStructure(strength, Structure.SHIPWRECK, STRUCTURE_SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition inSwampHut(int strength) {
        return this.inStructure(strength, Structure.SWAMP_HUT, STRUCTURE_SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition inStronghold(int strength) {
        return this.inStructure(strength, Structure.STRONGHOLD, STRUCTURE_STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition inMonument(int strength) {
        return this.inStructure(strength, Structure.MONUMENT, STRUCTURE_MONUMENT);
    }

    @Override
    public IAgriGrowCondition inOceanRuin(int strength) {
        return this.inStructure(strength, Structure.OCEAN_RUIN, STRUCTURE_OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition inFortress(int strength) {
        return this.inStructure(strength, Structure.FORTRESS, STRUCTURE_FORTRESS);
    }

    @Override
    public IAgriGrowCondition inEndCity(int strength) {
        return this.inStructure(strength, Structure.END_CITY, STRUCTURE_END_CITY);
    }

    @Override
    public IAgriGrowCondition inBuriedTreasure(int strength) {
        return this.inStructure(strength, Structure.BURIED_TREASURE, STRUCTURE_BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition inNetherFossil(int strength) {
        return this.inStructure(strength, Structure.NETHER_FOSSIL, STRUCTURE_NETHER_FOSSIL);
    }

    @Override
    public IAgriGrowCondition inBastionRemnant(int strength) {
        return this.inStructure(strength, Structure.BASTION_REMNANT, STRUCTURE_BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition notInVillage(int strength) {
        return this.notInStructure(strength, Structure.VILLAGE, STRUCTURE_VILLAGE);
    }

    @Override
    public IAgriGrowCondition notInPillagerOutpost(int strength) {
        return this.notInStructure(strength, Structure.PILLAGER_OUTPOST, STRUCTURE_PILLAGER_OUTPOST);
    }

    @Override
    public IAgriGrowCondition notInMineshaft(int strength) {
        return this.notInStructure(strength, Structure.MINESHAFT, STRUCTURE_MINESHAFT);
    }

    @Override
    public IAgriGrowCondition notInMansion(int strength) {
        return this.notInStructure(strength, Structure.WOODLAND_MANSION, STRUCTURE_WOODLAND_MANSION);
    }

    @Override
    public IAgriGrowCondition notInPyramid(int strength) {
        return this.structure(strength, structure -> structure != Structure.DESERT_PYRAMID && structure != Structure.JUNGLE_PYRAMID,
                ImmutableList.of(TOOLTIP_OUT_STRUCTURE, STRUCTURE_PYRAMID));
    }

    @Override
    public IAgriGrowCondition notInJunglePyramid(int strength) {
        return this.notInStructure(strength, Structure.JUNGLE_PYRAMID, STRUCTURE_JUNGLE_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInDesertPyramid(int strength) {
        return this.notInStructure(strength, Structure.DESERT_PYRAMID, STRUCTURE_DESERT_PYRAMID);
    }

    @Override
    public IAgriGrowCondition notInIgloo(int strength) {
        return this.notInStructure(strength, Structure.IGLOO, STRUCTURE_IGLOO);
    }

    @Override
    public IAgriGrowCondition notInRuinedPortal(int strength) {
        return this.notInStructure(strength, Structure.RUINED_PORTAL, STRUCTURE_RUINED_PORTAL);
    }

    @Override
    public IAgriGrowCondition notInShipwreck(int strength) {
        return this.notInStructure(strength, Structure.SHIPWRECK, STRUCTURE_SHIPWRECK);
    }

    @Override
    public IAgriGrowCondition notInSwampHut(int strength) {
        return this.notInStructure(strength, Structure.SWAMP_HUT, STRUCTURE_SWAMP_HUT);
    }

    @Override
    public IAgriGrowCondition notInStronghold(int strength) {
        return this.notInStructure(strength, Structure.STRONGHOLD, STRUCTURE_STRONGHOLD);
    }

    @Override
    public IAgriGrowCondition notInMonument(int strength) {
        return this.notInStructure(strength, Structure.MONUMENT, STRUCTURE_MONUMENT);
    }

    @Override
    public IAgriGrowCondition notInOceanRuin(int strength) {
        return this.notInStructure(strength, Structure.OCEAN_RUIN, STRUCTURE_OCEAN_RUIN);
    }

    @Override
    public IAgriGrowCondition notInFortress(int strength) {
        return this.notInStructure(strength, Structure.FORTRESS, STRUCTURE_FORTRESS);
    }

    @Override
    public IAgriGrowCondition notInEndCity(int strength) {
        return this.notInStructure(strength, Structure.END_CITY, STRUCTURE_END_CITY);
    }

    @Override
    public IAgriGrowCondition notInBuriedTreasure(int strength) {
        return this.notInStructure(strength, Structure.BURIED_TREASURE, STRUCTURE_BURIED_TREASURE);
    }

    @Override
    public IAgriGrowCondition notInNetherFossil(int strength) {
        return this.notInStructure(strength, Structure.NETHER_FOSSIL, STRUCTURE_NETHER_FOSSIL);
    }

    @Override
    public IAgriGrowCondition notInBastionRemnant(int strength) {
        return this.notInStructure(strength, Structure.BASTION_REMNANT, STRUCTURE_BASTION_REMNANT);
    }

    @Override
    public IAgriGrowCondition inStructure(int strength, IForgeStructure structure, ITextComponent structureName) {
        return this.structure(strength, structure::equals, ImmutableList.of(TOOLTIP_IN_STRUCTURE, structureName));
    }

    @Override
    public IAgriGrowCondition notInStructure(int strength, IForgeStructure structure, ITextComponent structureName) {
        return this.structure(strength, aStructure -> !structure.equals(aStructure), ImmutableList.of(TOOLTIP_OUT_STRUCTURE, structureName));
    }


    private static final ITextComponent STRUCTURE_VILLAGE = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.village");
    private static final ITextComponent STRUCTURE_PILLAGER_OUTPOST = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.pillager_outpost");
    private static final ITextComponent STRUCTURE_MINESHAFT = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.mineshaft");
    private static final ITextComponent STRUCTURE_WOODLAND_MANSION = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.woodland_mansion");
    private static final ITextComponent STRUCTURE_PYRAMID = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.pyramid");
    private static final ITextComponent STRUCTURE_JUNGLE_PYRAMID = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.jungle_pyramid");
    private static final ITextComponent STRUCTURE_DESERT_PYRAMID = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.desert_pyramid");
    private static final ITextComponent STRUCTURE_IGLOO = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.igloo");
    private static final ITextComponent STRUCTURE_RUINED_PORTAL = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.ruined_portal");
    private static final ITextComponent STRUCTURE_SHIPWRECK = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.shipwreck");
    private static final ITextComponent STRUCTURE_SWAMP_HUT = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.swamp_hut");
    private static final ITextComponent STRUCTURE_STRONGHOLD = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.stronghold");
    private static final ITextComponent STRUCTURE_MONUMENT = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.monument");
    private static final ITextComponent STRUCTURE_OCEAN_RUIN = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.ocean_ruin");
    private static final ITextComponent STRUCTURE_FORTRESS = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.fortress");
    private static final ITextComponent STRUCTURE_END_CITY = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.end_city");
    private static final ITextComponent STRUCTURE_BURIED_TREASURE = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.buried_treasure");
    private static final ITextComponent STRUCTURE_NETHER_FOSSIL = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.nether_fossil");
    private static final ITextComponent STRUCTURE_BASTION_REMNANT = new TranslationTextComponent(
            AgriCraft.instance.getModId() + ".structure.bastion_remnant");
}
