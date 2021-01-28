package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.misc.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.item.ItemStack;
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
    IAgriAdapterizer<AgriSeed> connectSeedAdapterizer();

    @Nonnull
    IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry();

    @Nonnull
    ItemStack seedToStack(AgriSeed seed, int amount);

    @Nonnull
    ItemStack plantToSeedStack(IAgriPlant plant, int amount);

    @Nonnull
    Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos);

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
}
