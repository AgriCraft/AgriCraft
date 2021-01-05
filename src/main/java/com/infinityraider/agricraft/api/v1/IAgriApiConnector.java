package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface IAgriApiConnector {

    IAgriApiConnector FAKE = new AgriApiConnectorFake();

    @Nonnull
    AgriApiState getState();

    @Nonnull
    IAgriRegistry<IAgriPlant> connectPlantRegistry();

    @Nonnull
    IAgriRegistry<IAgriGrowthStage> connectGrowthStageRegistry();

    @Nonnull
    IAgriRegistry<IAgriWeed> connectWeedRegistry();

    @Nonnull
    IAgriMutationRegistry connectMutationRegistry();

    @Nonnull
    IAgriGeneRegistry connectGeneRegistry();

    @Nonnull
    IAgriSoilRegistry connectSoilRegistry();

    @Nonnull
    IAgriStatRegistry connectStatRegistry();

    @Nonnull
    IAgriAdapterizer<AgriSeed> connectSeedRegistry();

    @Nonnull
    IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry();

    @Nonnull
    Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos);

    @Nonnull
    IDefaultGrowConditionFactory getDefaultGrowConditionFactory();

    @Nonnull
    IAgriMutationHandler getAgriMutationHandler();

    @Nonnull
    IAgriGenome.Builder getAgriGenomeBuilder(IAgriPlant plant);
}
