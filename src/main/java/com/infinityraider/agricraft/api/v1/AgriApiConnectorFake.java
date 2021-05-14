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
    public IAgriPlantRegistry connectPlantRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriGrowthRegistry connectGrowthStageRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriWeedRegistry connectWeedRegistry() {
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
    public IAgriStatRegistry connectStatRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriSoilRegistry connectSoilRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<IAgriGenome> connectGenomeAdapterizer() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @Nonnull
    public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriSeasonLogic connectSeasonLogic() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IIngredientSerializer<AgriPlantIngredient> connectPlantIngredientSerializer() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> getSoil(IBlockReader world, BlockPos pos) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IFluidHandler getIrrigationComponentFluidHandler(IAgriIrrigationComponent component) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public void registerVanillaPlantingOverrideException(Item seed) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public <T extends TileEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public IAgriGrowthRequirement.Builder getGrowthRequirementBuilder() {
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

    @Override
    public boolean isObservingWithMagnifyingGlass(PlayerEntity player) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }
}
