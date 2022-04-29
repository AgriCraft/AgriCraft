package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.templates.AgriPlant;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.handler.VanillaSeedConversionHandler;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.infinityraider.agricraft.impl.v1.requirement.GrowthReqInitializer;
import com.infinityraider.infinitylib.utility.FuzzyStack;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlant implements IAgriPlant {
    protected final AgriPlant plant;

    private final TranslatableComponent plantName;
    private final TranslatableComponent seedName;
    private final TranslatableComponent description;

    private final List<IAgriGrowthStage> growthStages;
    private final IAgriGrowthRequirement growthRequirement;

    private final List<FuzzyStack> seedItems;
    private final List<IJsonPlantCallback> callbacks;
    private final ResourceLocation seedTexture;
    private final ResourceLocation seedModel;

    public JsonPlant(AgriPlant plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.plantName = new TranslatableComponent(plant.getPlantLangKey());
        this.seedName = new TranslatableComponent(plant.getSeedLangKey());
        this.description = new TranslatableComponent(plant.getDescLangKey());
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.plant.getGrowthStages());
        this.seedItems = initSeedItems(plant);
        this.callbacks = this.initCallBacks(plant);
        this.growthRequirement = GrowthReqInitializer.initGrowthRequirement(plant, this.callbacks);
        this.seedTexture = new ResourceLocation(plant.getSeedTexture());
        this.seedModel = this.initSeedModel(plant.getSeedModel());
    }

    private List<FuzzyStack> initSeedItems(AgriPlant plant) {
        return plant.getSeeds().stream()
                .flatMap(seed -> {
                    boolean override = seed.isOverridePlanting();
                    if(override) {
                        return seed.convertAll(FuzzyStack.class).stream();
                    } else {
                        return seed.convertAll(FuzzyStack.class).stream().peek(stack -> stack.foreach(VanillaSeedConversionHandler.getInstance()::registerException));
                    }
                })
                .collect(Collectors.toList());
    }

    private List<IJsonPlantCallback> initCallBacks(AgriPlant plant) {
        return plant.getCallbacks().stream()
                .map(json ->
                        JsonPlantCallbackManager.get(json).flatMap(factory -> {
                            try {
                                return Optional.of(factory.makeCallBack(json));
                            } catch(Exception e) {
                                AgriCraft.instance.getLogger().error("Failed to parse json plant callback");
                                AgriCraft.instance.getLogger().error(json.toString());
                                AgriCraft.instance.getLogger().printStackTrace(e);
                            }
                            return Optional.empty();
                        }))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private ResourceLocation initSeedModel(String model) {
        if(model.contains("#")) {
            return new ModelResourceLocation(model);
        } else {
            return new ResourceLocation(model);
        }
    }

    @Nonnull
    @Override
    public String getId() {
        return this.plant.getId();
    }

    @Nonnull
    @Override
    public TranslatableComponent getPlantName() {
        return this.plantName;
    }

    @Nonnull
    @Override
    public TranslatableComponent getSeedName() {
        return this.seedName;
    }

    @Override
    public int getTier() {
        return this.plant.getTier();
    }

    @Override
    public boolean isFertilizable(IAgriGrowthStage growthStage, IAgriFertilizer fertilizer) {
        return true;
    }

    @Nonnull
    @Override
    public boolean isSeedItem(ItemStack stack) {
        return this.seedItems.stream().anyMatch(i -> i.matches(stack));
    }

    @Nonnull
    @Override
    public IAgriGrowthRequirement getGrowthRequirement(IAgriGrowthStage stage) {
        return this.growthRequirement;
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
    public double getPlantHeight(IAgriGrowthStage stage) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(index < 0 || index >= this.plant.getGrowthStages()) {
            return 0;
        }
        return this.plant.getGrowthStageHeight(index);
    }

    @Override
    public Optional<BlockState> asBlockState(IAgriGrowthStage stage) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public TranslatableComponent getInformation() {
        return this.description;
    }

    @Override
    public void addTooltip(Consumer<Component> consumer) {
        consumer.accept(this.getPlantName());
        consumer.accept(this.getInformation());
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
    public List<ResourceLocation> getTexturesFor(IAgriGrowthStage stage) {
        return Arrays.stream(this.plant.getTexture().getPlantTextures(IncrementalGrowthLogic.getGrowthIndex(stage)))
                .map(ResourceLocation::new)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public ResourceLocation getSeedTexture() {
        return this.seedTexture;
    }

    @Nonnull
    @Override
    public ResourceLocation getSeedModel() {
        return this.seedModel;
    }

    @Override
    public void spawnParticles(@Nonnull IAgriCrop crop, Random rand) {
        final int index = IncrementalGrowthLogic.getGrowthIndex(crop.getGrowthStage());
        final Level world = crop.world();
        if (index == -1 || world == null) {
            return;
        }
        this.plant.getParticleEffects().stream()
                .filter(effect -> effect.allowParticles(index))
                .forEach(effect -> {
                    ParticleType<?> particle = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(effect.getParticle()));
                    if (!(particle instanceof ParticleOptions)) {
                        return;
                    }
                    for (int amount = 0; amount < 3; ++amount) {
                        if (rand.nextDouble() < effect.getProbability()) {
                            BlockPos pos = crop.getPosition();
                            double x = pos.getX() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.getDeltaX() * rand.nextDouble();
                            double y = pos.getY() + 0.5D + effect.getDeltaY() * rand.nextDouble();
                            double z = pos.getZ() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.getDeltaZ() * rand.nextDouble();
                            world.addParticle((ParticleOptions) particle, x, y, z, 0.0D, 0.0D, 0.0D);
                        }
                    }
                });
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
    public int getBrightness(@Nonnull IAgriCrop crop) {
        return this.callbacks.stream().mapToInt(callback -> callback.getBrightness(crop)).max().orElse(0);
    }

    @Override
    public int getRedstonePower(@Nonnull IAgriCrop crop) {
        return this.callbacks.stream().mapToInt(callback -> callback.getRedstonePower(crop)).max().orElse(0);
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

    @Override
    public Optional<InteractionResult> onRightClickPre(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        return this.callbacks.stream()
                .map(callback -> callback.onRightClickPre(crop, stack, entity))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public Optional<InteractionResult> onRightClickPost(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        return this.callbacks.stream()
                .map(callback -> callback.onRightClickPost(crop, stack, entity))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
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
