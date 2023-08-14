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
//import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IAgriApiConnector {

	IAgriApiConnector FAKE = new AgriApiConnectorFake();

	@NotNull
	AgriApiState getState();

	@NotNull
	IAgriContent connectAgriContent();

	// FIXME: update
	@Nullable
	IAgriCropStickItem.Variant createCropStickVariant(String name/*, Material material*/, SoundType sound, Predicate<Fluid> fluidPredicate);

	@NotNull
	IAgriConfig connectAgriConfig();

	@NotNull
	IAgriPlantRegistry connectPlantRegistry();

	@NotNull
	IAgriGrowthRegistry connectGrowthStageRegistry();

	@NotNull
	IAgriWeedRegistry connectWeedRegistry();

	@NotNull
	IAgriMutationRegistry connectMutationRegistry();

	@NotNull
	IAgriGeneRegistry connectGeneRegistry();

	@NotNull
	IAgriStatRegistry connectStatRegistry();

	@NotNull
	IAgriSoilRegistry connectSoilRegistry();

	@NotNull
	IAgriAdapterizer<IAgriGenome> connectGenomeAdapterizer();

	@NotNull
	IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry();

	@NotNull
	IAgriSeasonLogic connectSeasonLogic();

	@NotNull
	IWorldGenPlantManager connectWorldGenPlantManager();

	@NotNull
	ItemStack plantToSeedStack(IAgriPlant plant, int amount);

// FIXME: update
//	@NotNull
//	IIngredientSerializer<AgriPlantIngredient> connectPlantIngredientSerializer();
//
//	@NotNull
//	IIngredientSerializer<AnySoilIngredient> connectAnySoilIngredientSerializer();

	@NotNull
	Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos);

	@NotNull
	Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos);

	@NotNull
	Optional<IAgriFertilizer> getFertilizer(ItemStack itemStack);

	@NotNull
	Optional<IAgriGreenHouse> getGreenHouse(Level world, BlockPos pos);

	@NotNull
	Optional<IAgriGreenHouse> createGreenHouse(Level world, BlockPos pos);

	@NotNull
	ItemStack attemptConversionToAgriSeed(ItemStack original);

	void registerVanillaPlantingOverrideException(Item seed);

// FIXME: update
//	<T extends BlockEntity, C extends IAgriCrop> void registerCapabilityCropInstance(CropCapability.Instance<T, C> instance);

	@NotNull
	IAgriGrowthRequirement.Builder getGrowthRequirementBuilder();

	@NotNull
	IDefaultGrowConditionFactory getDefaultGrowConditionFactory();

	@NotNull
	List<IAgriGrowthStage> getDefaultGrowthStages(int stages);

	@NotNull
	IAgriMutationHandler getAgriMutationHandler();

	@NotNull
	IAgriGenome.Builder getAgriGenomeBuilder(@NotNull IAgriPlant plant);

// FIXME: update
//	@NotNull
//	@OnlyIn(Dist.CLIENT)
//	IAgriPlantQuadGenerator getPlantQuadGenerator();

	@NotNull
	Optional<IJsonPlantCallback.Factory> getJsonPlantCallback(String id);

	boolean registerJsonPlantCallback(@NotNull IJsonPlantCallback.Factory callback);

	@NotNull
	Optional<IJsonMutationCondition.Factory> getJsonMutationCondition(String id);

	boolean registerJsonMutationCondition(@NotNull IJsonMutationCondition.Factory trigger);

	IAgriMutation.Condition convertJsonMutationCondition(IJsonMutationCondition condition, boolean isRequired, double guaranteedProbability);

	boolean isObservingWithMagnifyingGlass(Player player);
// FIXME: update
//	@OnlyIn(Dist.CLIENT)
//	void registerMagnifyingGlassInspector(IMagnifyingGlassInspector inspector);
//
//	@OnlyIn(Dist.CLIENT)
//	<P extends IAgriJournalItem.IPage> void registerJournalDataDrawer(IJournalDataDrawer<P> drawer);

}
