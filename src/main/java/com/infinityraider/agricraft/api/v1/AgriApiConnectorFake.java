package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.api.v1.config.IAgriConfig;
import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationHandler;
import com.infinityraider.agricraft.api.v1.client.IAgriPlantQuadGenerator;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.*;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.api.v1.plant.AgriPlantIngredient;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.IIngredientSerializer;

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
    public IAgriContent connectAgriContent() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
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
    public IIngredientSerializer<AnySoilIngredient> connectAnySoilIngredientSerializer() {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public Optional<IAgriFertilizer> getFertilizer(ItemStack itemStack) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Nonnull
    @Override
    public ItemStack attemptConversionToAgriSeed(ItemStack original) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public void registerVanillaPlantingOverrideException(Item seed) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public <T extends BlockEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance) {
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
    public Optional<IJsonPlantCallback.Factory> getJsonPlantCallback(String id) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public boolean registerJsonPlantCallback(@Nonnull IJsonPlantCallback.Factory callback) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    public boolean isObservingWithMagnifyingGlass(Player player) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerMagnifyingGlassInspector(IMagnifyingGlassInspector inspector) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public <P extends IAgriJournalItem.IPage> void registerJournalDataDrawer(IJournalDataDrawer<P> drawer) {
        throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
    }
}
