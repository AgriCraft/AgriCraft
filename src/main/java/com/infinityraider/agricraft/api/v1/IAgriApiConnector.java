package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.client.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.*;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    IAgriAdapterizer<BlockState> connectSoilAdapterizer();

    @Nonnull
    IAgriAdapterizer<AgriSeed> connectSeedAdapterizer();

    @Nonnull
    IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry();

    @Nonnull
    IAgriSeasonLogic connectSeasonLogic();

    @Nonnull
    ItemStack seedToStack(AgriSeed seed, int amount);

    @Nonnull
    ItemStack plantToSeedStack(IAgriPlant plant, int amount);

    @Nonnull
    Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos);

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

    @Nonnull
    IAgriIrrigationNetwork getIrrigationNetwork(IAgriIrrigationComponent component, Direction side);

    boolean isObservingWithMagnifyingGlass(PlayerEntity player);
}
