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
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
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

/**
 * A fake API connector, for when the API was not found.
 */
final class AgriApiConnectorFake implements IAgriApiConnector {

    @Override
    @Nonnull
    public AgriApiState getState() {
        return AgriApiState.INVALID;
    }

    @Nonnull
    @Override
    public IAgriConfig connectAgriConfig() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriRegistry<IAgriPlant> connectPlantRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriRegistry<IAgriGrowthStage> connectGrowthStageRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriRegistry<IAgriWeed> connectWeedRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriMutationRegistry connectMutationRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriGeneRegistry connectGeneRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriSoilRegistry connectSoilRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriStatRegistry connectStatRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<AgriSeed> connectSeedAdapterizer() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public ItemStack seedToStack(AgriSeed seed, int amount) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IDefaultGrowConditionFactory getDefaultGrowConditionFactory() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public List<IAgriGrowthStage> getDefaultGrowthStages(int stages) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriMutationHandler getAgriMutationHandler() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriGenome.Builder getAgriGenomeBuilder(@Nonnull IAgriPlant plant) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    @OnlyIn(Dist.CLIENT)
    public IAgriPlantQuadGenerator getPlantQuadGenerator() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IJsonPlantCallback> getJsonPlantCallback(String id) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback callback) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }
}
