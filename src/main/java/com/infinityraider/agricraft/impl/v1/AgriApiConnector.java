package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.AgriApiState;
import com.infinityraider.agricraft.api.v1.IAgriApiConnector;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
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
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGenome;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationHandler;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationRegistry;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.agricraft.impl.v1.plant.AgriWeedRegistry;
import com.infinityraider.agricraft.impl.v1.requirement.AgriSoilRegistry;
import com.infinityraider.agricraft.impl.v1.requirement.Factory;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
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
    private final IAgriAdapterizer<IAgriPlant> seedSubstituteAdapterizer;
    @Nonnull
    private final IAgriAdapterizer<IAgriFertilizer> fertilizerAdapterizer;
    @Nonnull
    private final AgriMutationHandler mutator;

    public AgriApiConnector() {
        this.plantRegistry = AgriPlantRegistry.getInstance();
        this.growthStageRegistry = AgriGrowthRegistry.getInstance();
        this.mutationRegistry = AgriMutationRegistry.getInstance();
        this.statRegistry = AgriStatRegistry.getInstance();
        this.geneRegistry = AgriGeneRegistry.getInstance();
        this.soilRegistry = AgriSoilRegistry.getInstance();
        this.weedRegistry = AgriWeedRegistry.getInstance();
        this.seedAdapterizer = new AgriAdapterizer<>();
        this.seedSubstituteAdapterizer = new AgriAdapterizer<>();
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
    public IAgriAdapterizer<AgriSeed> connectSeedAdapterizer() {
        return this.seedAdapterizer;
    }

    @Nonnull
    @Override
    public IAgriAdapterizer<IAgriPlant> connectSeedSubstituteAdapterizer() {
        return this.seedSubstituteAdapterizer;
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
        return this.fertilizerAdapterizer;
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityCropSticks ? Optional.of((IAgriCrop) tile) : Optional.empty();
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
}
