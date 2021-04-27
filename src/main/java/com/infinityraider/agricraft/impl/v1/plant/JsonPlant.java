package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoilCondition;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.requirement.AgriGrowthRequirement;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
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
        this.callbacks = JsonPlantCallback.get(plant.getCallbacks());
        this.seedTexture = new ResourceLocation(plant.getSeedTexture());
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
    public double getPlantHeight(IAgriGrowthStage stage) {
        int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        if(index < 0 || index >= this.plant.getGrowthStages()) {
            return 0;
        }
        return this.plant.getGrowthStageHeight(index)*this.plant.getTexture().getRenderType().getHeightModifier();
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
    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> bakeQuads(Direction face, IAgriGrowthStage stage) {
        if(!stage.isGrowthStage()) {
            return ImmutableList.of();
        }
        final int index = IncrementalGrowthLogic.getGrowthIndex(stage);
        final int height = this.plant.getGrowthStageHeight(index);
        ImmutableList.Builder<BakedQuad> listBuilder = new ImmutableList.Builder<>();
        int layer = 0;
        int layers = (int) Math.ceil(height/16.0);
        while((16*layer) < height) {
            ResourceLocation rl = new ResourceLocation(this.plant.getTexture().getPlantTextures(index)[layer]);
            switch (this.plant.getTexture().getRenderType()) {
                case HASH:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForHashPattern(face, rl, layers - layer - 1));
                    break;
                case CROSS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForCrossPattern(face, rl, layers - layer - 1));
                    break;
                case PLUS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForPlusPattern(face, rl, layers - layer - 1));
                    break;
                case RHOMBUS:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForRhombusPattern(face, rl, layers - layer - 1));
                    break;
                default:
                    listBuilder.addAll(AgriApi.getPlantQuadGenerator().bakeQuadsForDefaultPattern(face, rl, layers - layer - 1));
                    break;
            }
            layer++;
        }
        return listBuilder.build();
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

    public static IAgriGrowthRequirement initGrowthRequirement(AgriPlant plant) {
        // Run checks
        if (plant == null) {
            return AgriGrowthRequirement.getNone();
        }

        // Initialize utility objects
        IAgriGrowthRequirement.Builder builder = AgriApi.getGrowthRequirementBuilder();
        IDefaultGrowConditionFactory growConditionFactory = AgriApi.getDefaultGrowConditionFactory();

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
            return light >= lower && light <= upper;
        });

        // Define requirement for nearby blocks
        plant.getRequirement().getConditions().forEach(obj -> {
            BlockPos min = new BlockPos(obj.getMinX(), obj.getMinY(), obj.getMinZ());
            BlockPos max = new BlockPos(obj.getMaxX(), obj.getMaxY(), obj.getMaxZ());
            builder.addCondition(growConditionFactory.statesNearby(str -> false, obj.getAmount(), min, max, obj.convertAll(BlockState.class)));
        });

        // Define requirement for seasons
        List<AgriSeason> seasons = plant.getRequirement().getSeasons().stream()
                .map(AgriSeason::fromString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
        if(seasons.size() > 0) {
            builder.defineSeasonality((str, season) -> str >= AgriApi.getStatRegistry().strengthStat().getMax() || seasons.stream().anyMatch(season::matches));
        }

        // Define requirement for fluids
        List<Fluid> fluids = plant.getRequirement().getFluid().convertAll(FluidState.class).stream()
                .map(FluidState::getFluid)
                .distinct()
                .collect(Collectors.toList());
        if(fluids.size() > 0) {
            builder.addCondition(builder.liquidFromFluid(str -> false, fluids));
        }

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
            T criterion, Consumer<BiPredicate<Integer, T>> consumer, AgriSoilCondition.Type type, double f, Runnable invalidCallback) {
        if(!criterion.isValid()) {
            invalidCallback.run();
        }
        consumer.accept((strength, humidity) -> {
            if(humidity.isValid() && criterion.isValid()) {
                int lower = type.lowerLimit(criterion.ordinal() - (int) (f * strength));
                int upper = type.upperLimit(criterion.ordinal() + (int) (f * strength));
                return humidity.ordinal() <= upper && humidity.ordinal() >= lower;
            }
            return false;
        });
    }

    private static boolean noSoilsMatch(IAgriGrowthRequirement requirement) {
        return AgriApi.getSoilRegistry().stream().noneMatch(soil -> {
            int min = AgriApi.getStatRegistry().strengthStat().getMin();
            int max = AgriApi.getStatRegistry().strengthStat().getMax();
            for(int strength = min; strength <= max; strength++) {
                boolean humidity = requirement.isSoilHumidityAccepted(soil.getHumidity(), strength);
                boolean acidity = requirement.isSoilAcidityAccepted(soil.getAcidity(), strength);
                boolean nutrients = requirement.isSoilNutrientsAccepted(soil.getNutrients(), strength);
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
