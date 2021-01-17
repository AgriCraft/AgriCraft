package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriRenderType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.requirement.JsonSoil;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationRegistry;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.render.QuadCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlant implements IAgriPlant {

    private final AgriPlant plant;
    private final List<IAgriGrowthStage> growthStages;
    private final Set<IGrowCondition> growthConditions;

    private final ItemStack seed;
    private final List<ItemStack> seedSubstitutes;

    @OnlyIn(Dist.CLIENT)
    private Map<Integer, QuadCache> quads;

    public JsonPlant(AgriPlant plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.plant.getGrowthStages());
        this.growthConditions = initGrowConditions(plant);
        this.seed = this.initSeed(plant);
        this.seedSubstitutes = initSeedSubstitutes(plant);
    }

    private ItemStack initSeed(AgriPlant plant) {
        ItemStack seed = plant.getSeed().convertSingle(ItemStack.class)
                .orElseThrow(() -> new IllegalArgumentException("No valid seed items defined for plant " + plant.getPlantName()));
        return this.initDynamicTag(seed);
    }

    private List<ItemStack> initSeedSubstitutes(AgriPlant plant) {
        return plant.getSeed().convertSubstitutes(ItemStack.class)
                .stream()
                .map(this::initDynamicTag)
                .collect(Collectors.toList());
    }

    private ItemStack initDynamicTag(ItemStack seed) {
        if(seed.getItem() instanceof ItemDynamicAgriSeed) {
            if(!seed.hasTag()) {
                seed.setTag(new CompoundNBT());
            }
            seed.getTag().putString(AgriNBT.PLANT, plant.getId());
        }
        return seed;
    }

    @Override
    public String getId() {
        return this.plant.getId();
    }

    @Override
    public String getPlantName() {
        return this.plant.getPlantName();
    }

    @Override
    public String getSeedName() {
        return this.plant.getSeedName();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getSeedSubstitutes() {
        return this.seedSubstitutes;
    }

    @Override
    public boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer) {
        return false;
    }

    @Override
    public double getSpreadChance(IAgriGrowthStage growthStage) {
        return this.plant.getSpreadChance();
    }

    @Override
    public double getGrowthChanceBase(IAgriGrowthStage growthStage) {
        return this.plant.getGrowthChance();
    }

    @Override
    public double getGrowthChanceBonus(IAgriGrowthStage growthStage) {
        return this.plant.getGrowthBonus();
    }

    @Override
    public double getSeedDropChanceBase(IAgriGrowthStage growthStage) {
        return this.plant.getSeedDropChance();
    }

    @Override
    public double getSeedDropChanceBonus(IAgriGrowthStage growthStage) {
        return this.plant.getSeedDropBonus();
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getInitialGrowthStage() {
        return this.growthStages.get(0);
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getGrowthStageAfterHarvest() {
        return this.growthStages.get(this.plant.getStageAfterHarvest());
    }

    @Override
    public Collection<IAgriGrowthStage> getGrowthStages() {
        return this.growthStages;
    }

    @Override
    public final ItemStack getSeed() {
        return this.seed.copy();
    }

    @Nonnull
    @Override
    public Set<IGrowCondition> getGrowConditions(IAgriGrowthStage stage) {
        return this.growthConditions;
    }

    @Override
    public Optional<BlockState> asBlockState(IAgriGrowthStage stage) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public String getInformation(IAgriGrowthStage stage) {
        return this.plant.getDescription().toString();
    }

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        consumer.accept(new StringTextComponent(this.plant.getPlantName()));
        consumer.accept(new StringTextComponent(this.plant.getDescription().toString()));
    }

    @Override
    public void getAllPossibleProducts(@Nonnull Consumer<ItemStack> products) {
        this.plant.getProducts().getAll().stream()
                .flatMap(p -> p.convertAll(ItemStack.class).stream())
                .forEach(products);
    }

    @Override
    public void getHarvestProducts(@Nonnull Consumer<ItemStack> products, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand) {
        if(growthStage.isMature()) {
            this.plant.getProducts().getRandom(rand).stream()
                    .map(p -> p.convertSingle(ItemStack.class, rand))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(products);
        }
    }

    @Override
    public boolean allowsCloning(IAgriGrowthStage stage) {
        return this.plant.allowsCloning();
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(Direction face, IAgriGrowthStage stage) {
        if (this.quads == null) {
            this.quads = Maps.newConcurrentMap();
        }
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if (index < 0) {
            return ImmutableList.of();
        }
        return this.quads.computeIfAbsent(index, i -> new QuadCache(dir -> {
            ResourceLocation rl = new ResourceLocation(this.plant.getTexture().getPlantTexture(index));
            if (this.plant.getTexture().getRenderType() == AgriRenderType.CROSS) {
                return AgriApi.getPlantQuadGenerator().bakeQuadsForCrossPattern(dir, rl);
            }
            if (this.plant.getTexture().getRenderType() == AgriRenderType.HASH) {
                return AgriApi.getPlantQuadGenerator().bakeQuadsForHashPattern(dir, rl);
            }
            return ImmutableList.of();
        })).getQuads(face);
    }

    @Nullable
    protected ResourceLocation getTextureFor(IAgriGrowthStage stage) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        return index < 0 ? null : new ResourceLocation(this.plant.getTexture().getPlantTexture(index));
    }

    @Nonnull
    @Override
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        ResourceLocation rl = this.getTextureFor(stage);
        return rl == null ? ImmutableList.of() : ImmutableList.of(rl);
    }

    public static final List<ItemStack> initSeedItemsListJSON(AgriPlant plant) {
        return plant.getSeedItems().stream()
                .flatMap(i -> i.convertAll(ItemStack.class).stream())
                .collect(Collectors.toList());
    }

    public static Set<IGrowCondition> initGrowConditions(AgriPlant plant) {
        // Run checks
        if (plant == null) {
            return ImmutableSet.of();
        }
        if (plant.getRequirement().getSoils().isEmpty()) {
            AgriCore.getLogger("agricraft").warn("Plant: \"{0}\" has no valid soils to plant on!", plant.getId());
        }

        // Initialize utility objects
        ImmutableSet.Builder<IGrowCondition> builder = new ImmutableSet.Builder<>();
        IDefaultGrowConditionFactory growConditionFactory = AgriApi.getDefaultGrowConditionFactory();

        // Define requirement for soil
        final int maxStrength = AgriApi.getStatRegistry().strengthStat().getMax();
        plant.getRequirement().getSoils().stream()
                .map(JsonSoil::new)
                .map(soil -> growConditionFactory.soil(maxStrength, soil))
                .forEach(builder::add);

        // Define requirement for nearby blocks
        plant.getRequirement().getConditions().forEach(obj -> {
            BlockPos min = new BlockPos(obj.getMinX(), obj.getMinY(), obj.getMinZ());
            BlockPos max = new BlockPos(obj.getMaxX(), obj.getMaxY(), obj.getMaxZ());
            builder.add(growConditionFactory.statesNearby(obj.getStrength(), obj.getAmount(), min, max, obj.convertAll(BlockState.class)));
        });

        // Define requirement for light
        builder.add(growConditionFactory.light(10, plant.getRequirement().getMinLight(), plant.getRequirement().getMaxLight()));

        // Return the growth requirements
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IAgriPlant) && (this.getId().equals(((IAgriPlant) obj).getId()));
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public IAgriGene<IAgriPlant> gene() {
        return GeneSpecies.getInstance();
    }

    @Override
    public IAgriPlant trait() {
        return this;
    }

    @Override
    public boolean isDominant(IAllel<IAgriPlant> other) {
        return AgriMutationRegistry.getInstance().complexity(this)
                <= AgriMutationRegistry.getInstance().complexity(other.trait());
    }

    @Override
    public CompoundNBT writeToNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString(AgriNBT.PLANT, this.getId());
        return tag;
    }
}
