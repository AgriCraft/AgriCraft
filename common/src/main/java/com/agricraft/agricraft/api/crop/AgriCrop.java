package com.agricraft.agricraft.api.crop;

import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.fertilizer.IAgriFertilizable;
import com.agricraft.agricraft.api.genetic.AgriGenomeProvider;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.function.Consumer;

public interface AgriCrop extends AgriGenomeProvider, IAgriFertilizable {

	/**
	 * @return true if the crop has a plant
	 */
	boolean hasPlant();

	/**
	 * @return true if the crop has crop sticks
	 */
	boolean hasCropSticks();

	/**
	 * @return true if the crop has cross crop sticks
	 */
	boolean isCrossCropSticks();

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
	AgriGrowthStage getGrowthStage();

	/**
	 * Change the growth stage of the crop
	 *
	 * @param stage the new stage value
	 */
	void setGrowthStage(AgriGrowthStage stage);

	/**
	 * @return the growth percentage of the crop.
	 */
	default double getGrowthPercent() {
		return this.getGrowthStage().growthPercentage();
	}

	/**
	 * @return true if this crop is fully grown (has no more growth stages)
	 */
	default boolean isFullyGrown() {
		return this.getGrowthStage().isFinal();
	}

	/**
	 * @return true if this crop can be harvested
	 */
	default boolean canBeHarvested() {
		return this.hasPlant() && this.getGrowthStage().isMature();
	}

	/**
	 * @return true if this crop is fertile and thus can grow
	 */
	default boolean isFertile() {
		return this.getFertilityResponse().isFertile();
	}

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

	/**
	 * Retrieve the products that would be produced upon clipping this crop.
	 *
	 * @param addToClipping a consumer that accepts the items that were clipped.
	 * @param clipper the itemstack representing the clipper
	 */
	void getClippingProducts(Consumer<ItemStack> addToClipping, ItemStack clipper);

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

	/**
	 * @return this, but cast to a TileEntity
	 */
	default BlockEntity asBlockEntity() {
		return (BlockEntity) this;
	}

	//#endregion

}
