package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApiState;
import com.infinityraider.agricraft.api.v1.IAgriApiConnector;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.misc.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.*;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkReference;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.impl.v1.crop.IncrementalGrowthLogic;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGenome;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationHandler;
import com.infinityraider.agricraft.impl.v1.genetics.AgriMutationRegistry;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;
import com.infinityraider.agricraft.impl.v1.plant.AgriPlantRegistry;
import com.infinityraider.agricraft.impl.v1.plant.AgriWeedRegistry;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import com.infinityraider.agricraft.impl.v1.requirement.AgriSoilRegistry;
import com.infinityraider.agricraft.impl.v1.requirement.Factory;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonLogic;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.agricraft.render.plant.AgriPlantQuadGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class AgriApiConnector implements IAgriApiConnector {
    @Nonnull
    private final IAgriPlantRegistry plantRegistry;
    @Nonnull
    private final IAgriGrowthRegistry growthStageRegistry;
    @Nonnull
    private final IAgriWeedRegistry weedRegistry;
    @Nonnull
    private final IAgriMutationRegistry mutationRegistry;
    @Nonnull
    private final IAgriStatRegistry statRegistry;
    @Nonnull
    private final IAgriGeneRegistry geneRegistry;
    @Nonnull
    private final IAgriSoilRegistry soilRegistry;
    @Nonnull
    private final IAgriAdapterizer<AgriSeed> seedAdapterizer;
    @Nonnull
    private final IAgriAdapterizer<IAgriFertilizer> fertilizerAdapterizer;
    @Nonnull
    private final IAgriSeasonLogic seasonLogic;
    @Nonnull
    private final AgriMutationHandler mutator;

    public AgriApiConnector() {
        this.plantRegistry = AgriPlantRegistry.getInstance();
        this.growthStageRegistry = AgriGrowthRegistry.getInstance();
        this.weedRegistry = AgriWeedRegistry.getInstance();
        this.mutationRegistry = AgriMutationRegistry.getInstance();
        this.statRegistry = AgriStatRegistry.getInstance();
        this.geneRegistry = AgriGeneRegistry.getInstance();
        this.soilRegistry = AgriSoilRegistry.getInstance();
        this.seedAdapterizer = new AgriAdapterizer<>();
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

    @Override
    @Nonnull
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
    public ItemStack seedToStack(AgriSeed seed, int amount) {
        return ItemDynamicAgriSeed.toStack(seed, amount);
    }

    @Nonnull
    @Override
    public ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
        return ItemDynamicAgriSeed.toStack(plant, amount);
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

    @Nonnull
    @Override
    public Optional<IJsonPlantCallback> getJsonPlantCallback(String id) {
        return JsonPlantCallback.get(id);
    }

    @Override
    public boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback callback) {
        return JsonPlantCallback.register(callback);
    }

    @Nonnull
    @Override
    public IAgriIrrigationNetwork getIrrigationNetwork(IAgriIrrigationComponent component, Direction side) {
        return CapabilityIrrigationNetworkReference.getInstance().getIrrigationNetwork(component, side);
    }
}
