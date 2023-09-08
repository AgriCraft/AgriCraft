package com.agricraft.agricraft.api.crop;

import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.genetic.AgriGenomeCarrier;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Consumer;

public interface AgriCrop extends AgriCropBehaviour, AgriGenomeCarrier {

	/**
	 * @return the id of the plant or an empty string if there is no plant
	 */
	String getPlantId();

	/**
	 * @return the plant associated to this crop or {@link AgriPlant#NO_PLANT} if there is no plant
	 */
	AgriPlant getPlant();

	/**
	 * @return the growth stage of the crop.
	 */
	int getGrowthStage();

	/**
	 * @return the growth percentage of the crop.
	 */
	int getGrowthPercent();

	/**
	 * @return true if this crop is fully grown (has no more growth stages)
	 */
	boolean isMaxStage();

	/**
	 * @return true if this crop can be harvested
	 */
	default boolean canBeHarvested() {
		return this.isMaxStage();
	}

	/**
	 * @return true if this crop is fertile and thus can grow
	 */
	boolean isFertile();

	/**
	 * @return the current fertility response for this crop
	 */
	AgriGrowthResponse getFertilityResponse();

	/**
	 * @return the soil this crop is planted on
	 */
	Optional<AgriSoil> getSoil();

	/**
	 * Retrieve the products that would be produced upon harvesting this crop.
	 *
	 * @param addToHarvest a consumer that accepts the items that were harvested.
	 */
	void getHarvestProducts(Consumer<ItemStack> addToHarvest);

	//#region block helper methods

	/**
	 * @return the position of this crop in the world
	 */
	BlockPos getBlockPos();

	/**
	 * @return the block state of this crop in the world
	 */
	BlockState getBlockState();

	/**
	 * @return the World in which this crop exists (may be null if the tile is invalid)
	 */
	Level getLevel();

	//#endregion

}
