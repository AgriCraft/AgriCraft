package com.infinityraider.agricraft.api.v1;

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
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public interface IAgriApiConnector {

    IAgriApiConnector FAKE = new AgriApiConnectorFake();

    @Nonnull
    AgriApiState getState();

    @Nonnull
    IAgriContent connectAgriContent();

    @Nullable
    IAgriCropStickItem.Variant createCropStickVariant(String name, Material material, SoundType sound, Predicate<Fluid> fluidPredicate);

    @Nonnull
    IAgriConfig connectAgriConfig();

    @Nonnull
    IAgriPlantRegistry connectPlantRegistry();

    @Nonnull
    IAgriGrowthRegistry connectGrowthStageRegistry();

    @Nonnull
    IAgriWeedRegistry connectWeedRegistry();

    @Nonnull
    IAgriMutationRegistry connectMutationRegistry();

    @Nonnull
    IAgriGeneRegistry connectGeneRegistry();

    @Nonnull
    IAgriStatRegistry connectStatRegistry();

    @Nonnull
    IAgriSoilRegistry connectSoilRegistry();

    @Nonnull
    IAgriAdapterizer<IAgriGenome> connectGenomeAdapterizer();

    @Nonnull
    IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry();

    @Nonnull
    IAgriSeasonLogic connectSeasonLogic();

    @Nonnull
    IWorldGenPlantManager connectWorldGenPlantManager();

    @Nonnull
    ItemStack plantToSeedStack(IAgriPlant plant, int amount);

    @Nonnull
    IIngredientSerializer<AgriPlantIngredient> connectPlantIngredientSerializer();

    @Nonnull
    IIngredientSerializer<AnySoilIngredient> connectAnySoilIngredientSerializer();

    @Nonnull
    Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos);

    @Nonnull
    Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos);

    @Nonnull
    Optional<IAgriFertilizer> getFertilizer(ItemStack itemStack);

    @Nonnull
    ItemStack attemptConversionToAgriSeed(ItemStack original);

    void registerVanillaPlantingOverrideException(Item seed);

    <T extends BlockEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance);

    @Nonnull
    IAgriGrowthRequirement.Builder getGrowthRequirementBuilder();

    @Nonnull
    IDefaultGrowConditionFactory getDefaultGrowConditionFactory();

    @Nonnull
    List<IAgriGrowthStage> getDefaultGrowthStages(int stages);

    @Nonnull
    IAgriMutationHandler getAgriMutationHandler();

    @Nonnull
    IAgriGenome.Builder getAgriGenomeBuilder(@Nonnull IAgriPlant plant);

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    IAgriPlantQuadGenerator getPlantQuadGenerator();

    @Nonnull
    Optional<IJsonPlantCallback.Factory> getJsonPlantCallback(String id);

    boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback.Factory callback);

    @Nonnull
    Optional<IJsonMutationCondition.Factory> getJsonMutationCondition(String id);

    boolean registerJsonMutationCondition(@Nonnull IJsonMutationCondition.Factory trigger);

    IAgriMutation.Condition convertJsonMutationCondition(IJsonMutationCondition condition, boolean isRequired, double guaranteedProbability);

    boolean isObservingWithMagnifyingGlass(Player player);

    @OnlyIn(Dist.CLIENT)
    void registerMagnifyingGlassInspector(IMagnifyingGlassInspector inspector);

    @OnlyIn(Dist.CLIENT)
    <P extends IAgriJournalItem.IPage> void registerJournalDataDrawer(IJournalDataDrawer<P> drawer);
}
