package com.agricraft.agricraft.api;

import com.agricraft.agricraft.api.adapter.IAgriAdapterizer;
import com.agricraft.agricraft.api.config.IAgriConfig;
import com.agricraft.agricraft.api.content.IAgriContent;
import com.agricraft.agricraft.api.content.items.IAgriCropStickItem;
import com.agricraft.agricraft.api.content.items.IAgriJournalItem;
import com.agricraft.agricraft.api.content.world.IAgriGreenHouse;
import com.agricraft.agricraft.api.content.world.IWorldGenPlantManager;
import com.agricraft.agricraft.api.crop.IAgriCrop;
import com.agricraft.agricraft.api.crop.IAgriGrowthStage;
import com.agricraft.agricraft.api.fertilizer.IAgriFertilizer;
import com.agricraft.agricraft.api.genetics.IAgriGeneRegistry;
import com.agricraft.agricraft.api.genetics.IAgriGenome;
import com.agricraft.agricraft.api.genetics.IAgriMutation;
import com.agricraft.agricraft.api.genetics.IAgriMutationHandler;
import com.agricraft.agricraft.api.genetics.IAgriMutationRegistry;
import com.agricraft.agricraft.api.genetics.IJsonMutationCondition;
import com.agricraft.agricraft.api.plant.AgriPlantIngredient;
import com.agricraft.agricraft.api.plant.IAgriGrowthRegistry;
import com.agricraft.agricraft.api.plant.IAgriPlant;
import com.agricraft.agricraft.api.plant.IAgriPlantRegistry;
import com.agricraft.agricraft.api.plant.IAgriWeedRegistry;
import com.agricraft.agricraft.api.plant.IJsonPlantCallback;
import com.agricraft.agricraft.api.requirement.AnySoilIngredient;
import com.agricraft.agricraft.api.requirement.IAgriGrowthRequirement;
import com.agricraft.agricraft.api.requirement.IAgriSeasonLogic;
import com.agricraft.agricraft.api.requirement.IAgriSoil;
import com.agricraft.agricraft.api.requirement.IAgriSoilRegistry;
import com.agricraft.agricraft.api.requirement.IDefaultGrowConditionFactory;
import com.agricraft.agricraft.api.stat.IAgriStatRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A fake API connector, for when the API was not found.
 */
final class AgriApiConnectorFake implements IAgriApiConnector {

	@Override
	@NotNull
	public AgriApiState getState() {
		return AgriApiState.INVALID;
	}

	@NotNull
	@Override
	public IAgriContent connectAgriContent() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Nullable
	@Override
	public IAgriCropStickItem.Variant createCropStickVariant(String name, Material material, SoundType sound, Predicate<Fluid> fluidPredicate) {
		return null;
	}

	@NotNull
	@Override
	public IAgriConfig connectAgriConfig() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriPlantRegistry connectPlantRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IAgriGrowthRegistry connectGrowthStageRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IAgriWeedRegistry connectWeedRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriMutationRegistry connectMutationRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriGeneRegistry connectGeneRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriStatRegistry connectStatRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriSoilRegistry connectSoilRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriAdapterizer<IAgriGenome> connectGenomeAdapterizer() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	@NotNull
	public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IAgriSeasonLogic connectSeasonLogic() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IWorldGenPlantManager connectWorldGenPlantManager() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public ItemStack plantToSeedStack(IAgriPlant plant, int amount) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

// FIXME: update
//	@NotNull
//	@Override
//	public IIngredientSerializer<AgriPlantIngredient> connectPlantIngredientSerializer() {
//		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
//	}
//
//	@NotNull
//	@Override
//	public IIngredientSerializer<AnySoilIngredient> connectAnySoilIngredientSerializer() {
//		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
//	}

	@NotNull
	@Override
	public Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public Optional<IAgriFertilizer> getFertilizer(ItemStack itemStack) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public Optional<IAgriGreenHouse> getGreenHouse(Level world, BlockPos pos) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public Optional<IAgriGreenHouse> createGreenHouse(Level world, BlockPos pos) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public ItemStack attemptConversionToAgriSeed(ItemStack original) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	public void registerVanillaPlantingOverrideException(Item seed) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

// FIXME: update
//	@Override
//	public <T extends BlockEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance) {
//		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
//	}

	@NotNull
	@Override
	public IAgriGrowthRequirement.Builder getGrowthRequirementBuilder() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IDefaultGrowConditionFactory getDefaultGrowConditionFactory() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public List<IAgriGrowthStage> getDefaultGrowthStages(int stages) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IAgriMutationHandler getAgriMutationHandler() {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public IAgriGenome.Builder getAgriGenomeBuilder(@NotNull IAgriPlant plant) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

// FIXME: update
//	@NotNull
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public IAgriPlantQuadGenerator getPlantQuadGenerator() {
//		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
//	}

	@NotNull
	@Override
	public Optional<IJsonPlantCallback.Factory> getJsonPlantCallback(String id) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	public boolean registerJsonPlantCallback(@NotNull IJsonPlantCallback.Factory callback) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@NotNull
	@Override
	public Optional<IJsonMutationCondition.Factory> getJsonMutationCondition(String id) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	public boolean registerJsonMutationCondition(@NotNull IJsonMutationCondition.Factory trigger) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	public IAgriMutation.Condition convertJsonMutationCondition(IJsonMutationCondition condition, boolean isRequired, double guaranteedProbability) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

	@Override
	public boolean isObservingWithMagnifyingGlass(Player player) {
		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
	}

// FIXME: update
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public void registerMagnifyingGlassInspector(IMagnifyingGlassInspector inspector) {
//		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
//	}
//
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public <P extends IAgriJournalItem.IPage> void registerJournalDataDrawer(IJournalDataDrawer<P> drawer) {
//		throw new UnsupportedOperationException("The stand-in version of the AgriCraft API does not support this operation.");
//	}

}
