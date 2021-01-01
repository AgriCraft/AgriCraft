package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.AgriApiState;
import com.infinityraider.agricraft.api.v1.IAgriApiConnector;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import com.infinityraider.agricraft.core.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.core.genetics.AgriGenome;
import com.infinityraider.agricraft.core.mutation.AgriMutationHandler;
import com.infinityraider.agricraft.core.mutation.AgriMutationRegistry;
import com.infinityraider.agricraft.core.plant.AgriGrowthRegistry;
import com.infinityraider.agricraft.core.requirement.AgriSoilRegistry;
import com.infinityraider.agricraft.core.requirement.Factory;
import com.infinityraider.agricraft.core.stats.AgriStatRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public class AgriApiConnector implements IAgriApiConnector {
    @Nonnull
    private final IAgriRegistry<IAgriPlant> plantRegistry;
    @Nonnull
    private final IAgriRegistry<IAgriGrowthStage> growthStageRegistry;
    @Nonnull
    private final IAgriMutationRegistry mutationRegistry;
    @Nonnull
    private final IAgriStatRegistry statRegistry;
    @Nonnull
    private final IAgriGeneRegistry geneRegistry;
    @Nonnull
    private final IAgriSoilRegistry soilRegistry;
    @Nonnull
    private final IAgriRegistry<IAgriWeed> weedRegistry;
    @Nonnull
    private final IAgriAdapterizer<AgriSeed> seedAdapterizer;
    @Nonnull
    private final IAgriAdapterizer<IAgriFertilizer> fertilizerAdapterizer;
    @Nonnull
    private final AgriMutationHandler mutator;

    public AgriApiConnector() {
        this.plantRegistry = new AgriRegistry<>("plant", IAgriPlant.class);
        this.growthStageRegistry = AgriGrowthRegistry.getInstance();
        this.mutationRegistry = AgriMutationRegistry.getInstance();
        this.statRegistry = AgriStatRegistry.getInstance();
        this.geneRegistry = AgriGeneRegistry.getInstance();
        this.soilRegistry = AgriSoilRegistry.getInstance();
        this.weedRegistry = new AgriRegistry<>("weed", IAgriWeed.class);
        this.seedAdapterizer = new AgriAdapterizer<>();
        this.fertilizerAdapterizer = new AgriAdapterizer<>();
        this.mutator = AgriMutationHandler.getInstance();
    }

    @Override
    @Nonnull
    public AgriApiState getState() {
        return AgriApiState.VALID;
    }

    @Override
    @Nonnull
    public IAgriRegistry<IAgriPlant> connectPlantRegistry() {
        return this.plantRegistry;
    }

    @Nonnull
    @Override
    public IAgriRegistry<IAgriGrowthStage> connectGrowthStageRegistry() {
        return this.growthStageRegistry;
    }

    @Nonnull
    @Override
    public IAgriRegistry<IAgriWeed> connectWeedRegistry() {
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
    public IAgriSoilRegistry connectSoilRegistry() {
        return this.soilRegistry;
    }

    @Override
    @Nonnull
    public IAgriStatRegistry connectStatRegistry() {
        return this.statRegistry;
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<AgriSeed> connectSeedRegistry() {
        return this.seedAdapterizer;
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
        return this.fertilizerAdapterizer;
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(World world, BlockPos pos) {
        return CropInstance.get(world, pos);
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(BlockState state, World world, BlockPos pos) {
        return CropInstance.get(state, world, pos);
    }

    @Override
    public IDefaultGrowConditionFactory getDefaultGrowConditionFactory() {
        return Factory.getInstance();
    }

    @Nonnull
    @Override
    public IAgriMutationHandler getAgriMutationHandler() {
        return this.mutator;
    }

    @Nonnull
    @Override
    public IAgriGenome.Builder getAgriGenomeBuilder() {
        return AgriGenome.builder();
    }
}
