package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApiState;
import com.infinityraider.agricraft.api.v1.IAgriApiConnector;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.api.v1.content.items.IAgriCropStickItem;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.content.world.IWorldGenPlantManager;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.client.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.*;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import com.infinityraider.agricraft.capability.CapabilityCrop;
import com.infinityraider.agricraft.content.AgriRecipeSerializerRegistry;
import com.infinityraider.agricraft.content.core.CropStickVariant;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.content.world.WorldGenPlantManager;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.agricraft.handler.MagnifyingGlassViewHandler;
import com.infinityraider.agricraft.handler.VanillaSeedConversionHandler;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGenome;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationHandler;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationRegistry;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;
import com.infinityraider.agricraft.impl.v1.journal.JsonMutationConditionManager;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.agricraft.impl.v1.plant.AgriWeedRegistry;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallbackManager;
import com.infinityraider.agricraft.impl.v1.requirement.AgriGrowthRequirement;
import com.infinityraider.agricraft.impl.v1.requirement.AgriSoilRegistry;
import com.infinityraider.agricraft.impl.v1.requirement.Factory;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonLogic;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AgriApiConnector implements IAgriApiConnector {
    private final IAgriPlantRegistry plantRegistry;
    private final IAgriGrowthRegistry growthStageRegistry;
    private final IAgriWeedRegistry weedRegistry;
    private final IAgriMutationRegistry mutationRegistry;
    private final IAgriStatRegistry statRegistry;
    private final IAgriGeneRegistry geneRegistry;
    private final IAgriSoilRegistry soilRegistry;
    private final IAgriAdapterizer<IAgriGenome> genomeAdapterizer;
    private final IAgriAdapterizer<IAgriFertilizer> fertilizerAdapterizer;
    private final IAgriSeasonLogic seasonLogic;
    private final AgriMutationHandler mutator;

    public AgriApiConnector() {
        this.plantRegistry = AgriPlantRegistry.getInstance();
        this.growthStageRegistry = AgriGrowthRegistry.getInstance();
        this.weedRegistry = AgriWeedRegistry.getInstance();
        this.mutationRegistry = AgriMutationRegistry.getInstance();
        this.statRegistry = AgriStatRegistry.getInstance();
        this.geneRegistry = AgriGeneRegistry.getInstance();
        this.soilRegistry =  AgriSoilRegistry.getInstance();
        this.genomeAdapterizer = new AgriAdapterizer<>();
        this.fertilizerAdapterizer = new AgriAdapterizer<>();
        this.seasonLogic = SeasonLogic.getInstance();
        this.mutator = AgriMutationHandler.getInstance();
    }

    @Override
    @Nonnull
    public AgriApiState getState() {
        return AgriApiState.VALID;
    }

    @Nonnull
    @Override
    public IAgriContent connectAgriContent() {
        return AgriContent.getInstance();
    }

    @Nullable
    @Override
    public IAgriCropStickItem.Variant createCropStickVariant(String name, Material material, SoundType sound, Predicate<Fluid> fluidPredicate) {
        return CropStickVariant.create(name, material, sound, fluidPredicate);
    }

    @Nonnull
    @Override
    public IAgriConfig connectAgriConfig() {
        return AgriCraft.instance.getConfig();
    }

    @Override
    @Nonnull
    public IAgriPlantRegistry connectPlantRegistry() {
        return this.plantRegistry;
    }

    @Nonnull
    @Override
    public IAgriGrowthRegistry connectGrowthStageRegistry() {
        return this.growthStageRegistry;
    }

    @Nonnull
    @Override
    public IAgriWeedRegistry connectWeedRegistry() {
        return this.weedRegistry;
    }

    @Override
    @Nonnull
    public IAgriMutationRegistry connectMutationRegistry() {
        return this.mutationRegistry;
    }

    @Override
    @Nonnull
    public IAgriGeneRegistry connectGeneRegistry() {
        return this.geneRegistry;
    }

    @Override
    @Nonnull
    public IAgriStatRegistry connectStatRegistry() {
        return this.statRegistry;
    }

    @Override
    @Nonnull
    public IAgriSoilRegistry connectSoilRegistry() {
        return this.soilRegistry;
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<IAgriGenome> connectGenomeAdapterizer() {
        return this.genomeAdapterizer;
    }

    public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
        return this.fertilizerAdapterizer;
    }

    @Override
    @Nonnull
    public IAgriSeasonLogic connectSeasonLogic() {
        return this.seasonLogic;
    }

    @Nonnull
    @Override
    public IWorldGenPlantManager connectWorldGenPlantManager() {
        return WorldGenPlantManager.getInstance();
    }

    @Nonnull
    @Override
    public ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
        return ItemDynamicAgriSeed.toStack(plant, amount);
    }

    @Nonnull
    @Override
    public IIngredientSerializer<AgriPlantIngredient> connectPlantIngredientSerializer() {
        return AgriRecipeSerializerRegistry.getInstance().plant_ingredient;
    }

    @Nonnull
    @Override
    public IIngredientSerializer<AnySoilIngredient> connectAnySoilIngredientSerializer() {
        return AgriRecipeSerializerRegistry.getInstance().any_soil_ingredient;
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof IAgriCrop) {
            return Optional.of((IAgriCrop) tile);
        }
        return CapabilityCrop.getInstance().getCapability(tile).map(crop -> crop);
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        IAgriSoilRegistry registry = this.connectSoilRegistry();
        Optional<IAgriSoil> soil = registry.valueOf(state);
        return soil.isPresent() ? soil : registry.getProvider(state.getBlock()).getSoil(world, pos, state);
    }

    @Nonnull
    @Override
    public Optional<IAgriFertilizer> getFertilizer(ItemStack itemStack) {
        if (fertilizerAdapterizer.hasAdapter(itemStack)) {
            return fertilizerAdapterizer.valueOf(itemStack);
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public ItemStack attemptConversionToAgriSeed(ItemStack original) {
        return VanillaSeedConversionHandler.getInstance().attemptConvert(original);
    }

    @Override
    public void registerVanillaPlantingOverrideException(Item seed) {
        VanillaSeedConversionHandler.getInstance().registerException(seed);
    }

    @Override
    public <T extends BlockEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance) {
        CapabilityCrop.getInstance().registerInstance(instance);
    }

    @Nonnull
    @Override
    public IAgriGrowthRequirement.Builder getGrowthRequirementBuilder() {
        return AgriGrowthRequirement.getBuilder();
    }

    @Override
    @Nonnull
    public IDefaultGrowConditionFactory getDefaultGrowConditionFactory() {
        return Factory.getInstance();
    }

    @Nonnull
    @Override
    public List<IAgriGrowthStage> getDefaultGrowthStages(int stages) {
        return IncrementalGrowthLogic.getOrGenerateStages(stages);
    }

    @Nonnull
    @Override
    public IAgriMutationHandler getAgriMutationHandler() {
        return this.mutator;
    }

    @Nonnull
    @Override
    public IAgriGenome.Builder getAgriGenomeBuilder(@Nonnull IAgriPlant plant) {
        return AgriGenome.builder(plant);
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public IAgriPlantQuadGenerator getPlantQuadGenerator() {
        return AgriPlantQuadGenerator.getInstance();
    }

    @Nonnull
    @Override
    public Optional<IJsonPlantCallback.Factory> getJsonPlantCallback(String id) {
        return JsonPlantCallbackManager.get(id);
    }

    @Override
    public boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback.Factory callback) {
        return JsonPlantCallbackManager.register(callback);
    }

    @Nonnull
    @Override
    public Optional<IJsonMutationCondition.Factory> getJsonMutationCondition(String id) {
        return JsonMutationConditionManager.get(id);
    }

    @Override
    public boolean registerJsonMutationCondition(@Nonnull IJsonMutationCondition.Factory condition) {
        return JsonMutationConditionManager.register(condition);
    }

    @Override
    public IAgriMutation.Condition convertJsonMutationCondition(IJsonMutationCondition condition, boolean isRequired, double guaranteedProbability) {
        return JsonMutationConditionManager.convert(condition, isRequired, guaranteedProbability);
    }

    @Override
    public boolean isObservingWithMagnifyingGlass(Player player) {
        return AgriCraft.instance.proxy().isMagnifyingGlassObserving(player);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerMagnifyingGlassInspector(IMagnifyingGlassInspector inspector) {
        MagnifyingGlassViewHandler.getInstance().registerMagnifyingGlassInspector(inspector);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <P extends IAgriJournalItem.IPage> void registerJournalDataDrawer(IJournalDataDrawer<P> drawer) {
        JournalViewPointHandler.registerJournalDataDrawer(drawer);
    }
}
