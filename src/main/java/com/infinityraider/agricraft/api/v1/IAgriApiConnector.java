package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.client.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.plant.*;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface IAgriApiConnector {

    IAgriApiConnector FAKE = new AgriApiConnectorFake();

    @Nonnull
    AgriApiState getState();

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
    ItemStack plantToSeedStack(IAgriPlant plant, int amount);

    @Nonnull
    IIngredientSerializer<AgriPlantIngredient> connectPlantIngredientSerializer();

    @Nonnull
    Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos);

    @Nonnull
    Optional<IAgriSoil> getSoil(IBlockReader world, BlockPos pos);

    @Nonnull
    IFluidHandler getIrrigationComponentFluidHandler(IAgriIrrigationComponent component);

    void registerVanillaPlantingOverrideException(Item seed);

    <T extends TileEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance);

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
    Optional<IJsonPlantCallback> getJsonPlantCallback(String id);

    boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback callback);

    boolean isObservingWithMagnifyingGlass(PlayerEntity player);
}
