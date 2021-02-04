package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.requirement.JsonSoil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlant implements IAgriPlant {

    private final AgriPlant plant;

    private final ITextComponent plantName;
    private final ITextComponent seedName;
    private final ITextComponent description;

    private final List<IAgriGrowthStage> growthStages;
    private final Set<IGrowCondition> growthConditions;

    private final List<ItemStack> seedItems;
    private final List<IJsonPlantCallback> callbacks;
    private final ResourceLocation seedModel;

    public JsonPlant(AgriPlant plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.plantName = new TranslationTextComponent(plant.getPlantLangKey());
        this.seedName = new TranslationTextComponent(plant.getSeedLangKey());
        this.description = new TranslationTextComponent(plant.getDescLangKey());
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.plant.getGrowthStages());
        this.growthConditions = initGrowConditions(plant);
        this.seedItems = initSeedItems(plant);
        this.callbacks = JsonPlantCallback.get(plant.getCallbacks());
        this.seedModel = this.initSeedModel(plant.getSeedModel());
    }

    private List<ItemStack> initSeedItems(AgriPlant plant) {
        return plant.getSeeds().stream()
                .flatMap(seed -> seed.convertAll(ItemStack.class).stream())
                .collect(Collectors.toList());
    }

    private ResourceLocation initSeedModel(String model) {
        if(model.contains("#")) {
            return new ModelResourceLocation(model);
        } else {
            return new ResourceLocation(plant.getSeedModel());
        }
    }

    @Nonnull
    @Override
    public String getId() {
        return this.plant.getId();
    }

    @Nonnull
    @Override
    public ITextComponent getPlantName() {
        return this.plantName;
    }

    @Nonnull
    @Override
    public ITextComponent getSeedName() {
        return this.seedName;
    }

    @Override
    public int getTier() {
        return this.plant.getTier();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getSeedItems() {
        return this.seedItems;
    }

    @Override
    public boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer) {
        return this.plant.canBonemeal();
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

    @Nonnull
    @Override
    public Collection<IAgriGrowthStage> getGrowthStages() {
        return this.growthStages;
    }

    @Override
    public int getPlantHeight(IAgriGrowthStage stage) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(index < 0 || index >= this.plant.getGrowthStages()) {
            return 0;
        }
        return this.plant.getGrowthStageHeight(index);
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
    public ITextComponent getInformation(IAgriGrowthStage stage) {
        return this.description;
    }

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        consumer.accept(this.getPlantName());
        consumer.accept(this.description);
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
    public void getAllPossibleClipProducts(@Nonnull Consumer<ItemStack> products) {
        this.plant.getClipProducts().getAll().stream()
                .flatMap(p -> p.convertAll(ItemStack.class).stream())
                .forEach(products);
    }

    @Override
    public void getClipProducts(@Nonnull Consumer<ItemStack> products, @Nonnull ItemStack clipper, @Nonnull IAgriGrowthStage growthStage, @Nonnull IAgriStatsMap stats, @Nonnull Random rand) {
        if(growthStage.isMature()) {
            this.plant.getClipProducts().getRandom(rand).stream()
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
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        final int height = this.plant.getGrowthStageHeight(index);
        ImmutableList.Builder<BakedQuad> listBuilder = new ImmutableList.Builder<>();
        int layer = 0;
        while((16*layer) < height) {
            ResourceLocation rl = layer == 0
                    ? new ResourceLocation(this.plant.getTexture().getPlantTexture(index))
                    : new ResourceLocation(this.plant.getTexture().getPlantTexture(this.plant.getGrowthStages() + layer - 1));
            switch (this.plant.getTexture().getRenderType()) {
                case HASH:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForHashPattern(face, rl, layer));
                    break;
                case CROSS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForCrossPattern(face, rl, layer));
                    break;
                case PLUS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForPlusPattern(face, rl, layer));
                    break;
                case RHOMBUS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForRhombusPattern(face, rl, layer));
                    break;
                default:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForDefaultPattern(face, rl, layer));
                    break;
            }
            layer++;
        }
        return listBuilder.build();
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

    @Nonnull
    @Override
    public ResourceLocation getSeedModel() {
        return this.seedModel;
    }

    @Override
    public boolean allowsHarvest(@Nonnull IAgriGrowthStage stage, @Nullable LivingEntity entity) {
        return stage.isMature();
    }

    @Override
    public boolean allowsClipping(@Nonnull IAgriGrowthStage stage, @Nonnull ItemStack clipper, @Nullable LivingEntity entity) {
        return stage.isMature() && this.plant.getClipProducts().getAll().size() > 0;
    }

    @Override
    public void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        this.callbacks.forEach(callback -> callback.onPlanted(crop, entity));
    }

    @Override
    public void onSpawned(@Nonnull IAgriCrop crop) {
        this.callbacks.forEach(callback -> callback.onSpawned(crop));
    }

    @Override
    public void onGrowth(@Nonnull IAgriCrop crop) {
        this.callbacks.forEach(callback -> callback.onGrowth(crop));
    }

    @Override
    public void onRemoved(@Nonnull IAgriCrop crop) {
        this.callbacks.forEach(callback -> callback.onRemoved(crop));
    }

    @Override
    public void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        this.callbacks.forEach(callback -> callback.onHarvest(crop, entity));
    }

    @Override
    public void onClipped(@Nonnull IAgriCrop crop, @Nonnull ItemStack clipper, @Nullable LivingEntity entity) {
        this.callbacks.forEach(callback -> callback.onClipped(crop, clipper, entity));
    }

    @Override
    public void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {
        this.callbacks.forEach(callback -> callback.onBroken(crop, entity));
    }

    @Override
    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        this.callbacks.forEach(callback -> callback.onEntityCollision(crop, entity));
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
}
