package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoilCondition;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.handler.VanillaSeedConversionHandler;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.requirement.AgriGrowthRequirement;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JsonPlant implements IAgriPlant {

    private final AgriPlant plant;

    private final TranslationTextComponent plantName;
    private final TranslationTextComponent seedName;
    private final TranslationTextComponent description;

    private final List<IAgriGrowthStage> growthStages;
    private final IAgriGrowthRequirement growthRequirement;

    private final List<ItemStack> seedItems;
    private final List<IJsonPlantCallback> callbacks;
    private final ResourceLocation seedTexture;
    private final ResourceLocation seedModel;

    public JsonPlant(AgriPlant plant) {
        this.plant = Objects.requireNonNull(plant, "A JSONPlant may not consist of a null AgriPlant! Why would you even try that!?");
        this.plantName = new TranslationTextComponent(plant.getPlantLangKey());
        this.seedName = new TranslationTextComponent(plant.getSeedLangKey());
        this.description = new TranslationTextComponent(plant.getDescLangKey());
        this.growthStages = IncrementalGrowthLogic.getOrGenerateStages(this.plant.getGrowthStages());
        this.growthRequirement = initGrowthRequirement(plant);
        this.seedItems = initSeedItems(plant);
        this.callbacks = this.initCallBacks(plant);
        this.seedTexture = new ResourceLocation(plant.getSeedTexture());
        this.seedModel = this.initSeedModel(plant.getSeedModel());
    }

    private List<ItemStack> initSeedItems(AgriPlant plant) {
        return plant.getSeeds().stream()
                .flatMap(seed -> {
                    boolean override = seed.isOverridePlanting();
                    if(override) {
                        return seed.convertAll(ItemStack.class).stream();
                    } else {
                        return seed.convertAll(ItemStack.class).stream() .peek(stack ->
                                VanillaSeedConversionHandler.getInstance().registerException(stack.getItem())
                        );
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
    public TranslationTextComponent getPlantName() {
        return this.plantName;
    }

    @Nonnull
    @Override
    public TranslationTextComponent getSeedName() {
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
    public Collection<ItemStack> getSeedItems() {
        return this.seedItems;
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
    public TranslationTextComponent getInformation() {
        return this.description;
    }

    @Override
    public void addTooltip(Consumer<ITextComponent> consumer) {
        consumer.accept(this.getPlantName());
        if (!this.getInformation().getKey().isEmpty()) {
            consumer.accept(this.getInformation());
        }
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
    public List<BakedQuad> bakeQuads(@Nullable Direction face, IAgriGrowthStage stage) {
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(this.plant.getTexture().useModels()) {
            ResourceLocation rl = new ResourceLocation(this.plant.getTexture().getPlantModel(index));
            return AgriPlantQuadGenerator.getInstance().fetchQuads(rl);
        } else {
            List<ResourceLocation> textures = Arrays.stream(plant.getTexture().getPlantTextures(index))
                    .map(ResourceLocation::new)
                    .collect(Collectors.toList());
            return AgriPlantQuadGenerator.getInstance().bakeQuads(this, stage, this.plant.getTexture().getRenderType(),
                    face, textures);
        }
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
        final World world = crop.world();
        if (index == -1 || world == null) {
            return;
        }
        this.plant.getParticleEffects().stream()
                .filter(effect -> effect.allowParticles(index))
                .forEach(effect -> {
                    ParticleType<?> particle = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(effect.getParticle()));
                    if (!(particle instanceof IParticleData)) {
                        return;
                    }
                    for (int amount = 0; amount < 3; ++amount) {
                        if (rand.nextDouble() < effect.getProbability()) {
                            BlockPos pos = crop.getPosition();
                            double x = pos.getX() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.getDeltaX() * rand.nextDouble();
                            double y = pos.getY() + 0.5D + effect.getDeltaY() * rand.nextDouble();
                            double z = pos.getZ() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.getDeltaZ() * rand.nextDouble();
                            world.addParticle((IParticleData) particle, x, y, z, 0.0D, 0.0D, 0.0D);
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
    public Optional<ActionResultType> onRightClickPre(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        return this.callbacks.stream()
                .map(callback -> callback.onRightClickPre(crop, stack, entity))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public Optional<ActionResultType> onRightClickPost(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        return this.callbacks.stream()
                .map(callback -> callback.onRightClickPost(crop, stack, entity))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    public static IAgriGrowthRequirement initGrowthRequirement(AgriPlant plant) {
        // Run checks
        if (plant == null) {
            return AgriGrowthRequirement.getNone();
        }

        // Initialize utility objects
        IAgriGrowthRequirement.Builder builder = AgriApi.getGrowthRequirementBuilder();

        // Define requirement for humidity
        String humidityString = plant.getRequirement().getHumiditySoilCondition().getCondition();
        IAgriSoil.Humidity humidity = IAgriSoil.Humidity.fromString(humidityString).orElse(IAgriSoil.Humidity.INVALID);
        handleSoilCriterion(
                humidity,
                builder::defineHumidity,
                plant.getRequirement().getHumiditySoilCondition().getType(),
                plant.getRequirement().getHumiditySoilCondition().getToleranceFactor(),
                () -> AgriCore.getLogger("agricraft")
                        .warn("Plant: \"{0}\" has an invalid humidity criterion (\"{1}\")!", plant.getId(), humidityString));

        // Define requirement for acidity
        String acidityString = plant.getRequirement().getAciditySoilCondition().getCondition();
        IAgriSoil.Acidity acidity = IAgriSoil.Acidity.fromString(acidityString).orElse(IAgriSoil.Acidity.INVALID);
        handleSoilCriterion(
                acidity,
                builder::defineAcidity,
                plant.getRequirement().getAciditySoilCondition().getType(),
                plant.getRequirement().getAciditySoilCondition().getToleranceFactor(),
                () -> AgriCore.getLogger("agricraft")
                        .warn("Plant: \"{0}\" has an invalid acidity criterion (\"{1}\")!", plant.getId(), acidityString));

        // Define requirement for nutrients
        String nutrientString = plant.getRequirement().getNutrientSoilCondition().getCondition();
        IAgriSoil.Nutrients nutrients = IAgriSoil.Nutrients.fromString(nutrientString).orElse(IAgriSoil.Nutrients.INVALID);
        handleSoilCriterion(
                nutrients,
                builder::defineNutrients,
                plant.getRequirement().getNutrientSoilCondition().getType(),
                plant.getRequirement().getNutrientSoilCondition().getToleranceFactor(),
                () -> AgriCore.getLogger("agricraft")
                        .warn("Plant: \"{0}\" has an invalid nutrients criterion (\"{1}\")!", plant.getId(), nutrientString));

        // Define requirement for light
        final double f = plant.getRequirement().getLightToleranceFactor();
        final int minLight = plant.getRequirement().getMinLight();
        final int maxLight = plant.getRequirement().getMaxLight();
        builder.defineLightLevel((strength, light) -> {
            int lower = minLight - (int) (f * strength);
            int upper = maxLight + (int) (f * strength);
            return light >= lower && light <= upper ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.INFERTILE;
        });

        // Define requirement for nearby blocks
        plant.getRequirement().getConditions().forEach(obj -> {
            BlockPos min = new BlockPos(obj.getMinX(), obj.getMinY(), obj.getMinZ());
            BlockPos max = new BlockPos(obj.getMaxX(), obj.getMaxY(), obj.getMaxZ());
            builder.addCondition(builder.blockStatesNearby(obj.convertAll(BlockState.class), obj.getAmount(), min, max));
        });

        // Define requirement for seasons
        List<AgriSeason> seasons = plant.getRequirement().getSeasons().stream()
                .map(AgriSeason::fromString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
        if(seasons.size() > 0) {
            builder.defineSeasonality((str, season) -> {
                if(str >= AgriApi.getStatRegistry().strengthStat().getMax() || seasons.stream().anyMatch(season::matches)) {
                    return IAgriGrowthResponse.FERTILE;
                } else {
                    return IAgriGrowthResponse.INFERTILE;
                }
            });
        }

        // Define requirement for fluids
        List<Fluid> fluids = plant.getRequirement().getFluid().convertAll(FluidState.class).stream()
                .map(FluidState::getFluid)
                .distinct()
                .collect(Collectors.toList());
        BiFunction<Integer, Fluid, IAgriGrowthResponse> response = (strength, fluid) -> {
            if(fluids.size() > 0) {
                if(fluids.contains(fluid)) {
                    return IAgriGrowthResponse.FERTILE;
                }
                return fluid.equals(Fluids.LAVA) ? IAgriGrowthResponse.KILL_IT_WITH_FIRE : IAgriGrowthResponse.LETHAL;
            } else {
                if(fluid.equals(Fluids.LAVA)) {
                    return IAgriGrowthResponse.KILL_IT_WITH_FIRE;
                }
               return fluid.equals(Fluids.EMPTY) ? IAgriGrowthResponse.FERTILE : IAgriGrowthResponse.LETHAL;
            }
        };
        builder.defineFluid(response);

        // Build the growth requirement
        IAgriGrowthRequirement req = builder.build();

        // Log warning if no soils exist for this requirement combination
        if (noSoilsMatch(req)) {
            AgriCore.getLogger("agricraft").warn("Plant: \"{0}\" has no valid soils to plant on for any strength level!", plant.getId());
        }

        // Return the growth requirements
        return req;
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

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IAgriPlant) && (this.getId().equals(((IAgriPlant) obj).getId()));
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
