package com.infinityraider.agricraft.api.crop;

import java.util.List;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.math.BlockPos;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.misc.IAgriHarvestable;
import com.infinityraider.agricraft.api.misc.IAgriWeedable;
import com.infinityraider.agricraft.api.seed.IAgriSeedAcceptor;
import com.infinityraider.agricraft.api.seed.IAgriSeedProvider;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import java.util.Optional;
import java.util.Random;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve the ICrop instance use:
 * {@code API.getCrop(World world, int x, int y, int z)}
 */
public interface IAgriCrop extends IAgriSeedProvider, IAgriSeedAcceptor, IAgriWeedable, IAgriFertilizable, IAgriHarvestable {

	/**
	 * Retrieves the location of the crop instance.
	 *
	 * @return the crop's position.
	 */
	BlockPos getPos();

	/**
	 * @return The growth stage of the crop, between 0 and 7 (both inclusive).
	 */
	int getGrowthStage();

	/**
	 * Sets the growth stage for this crop
	 *
	 * @param stage the growth stage, between 0 and 7 (both inclusive).
	 */
	void setGrowthStage(int stage);

	/**
	 * @return if this crop is a crosscrop
	 */
	boolean isCrossCrop();

	/**
	 * Converts this crop to a crosscrop or a regular crop
	 *
	 * @param status true for crosscrop, false for regular crop
	 */
	void setCrossCrop(boolean status);

	/**
	 * @return if this crop is fertile and thus can grow
	 */
	boolean isFertile();

	/**
	 * @return if this crop is fully grown
	 */
	boolean isMature();
	
	default boolean spawn(Random rand) {
		return false;
	}
	
	default boolean spread(Random rand) {
		return false;
	}

	@Override
	default boolean canWeed() {
		return this.hasPlant() && this.getPlant().isWeedable();
	}
	
	@Override
	default boolean clearWeed() {
		if (this.canWeed()) {
			this.removePlant();
			return true;
		} else {
			return false;
		}
	}
    
    Optional<IAgriSoil> getSoil();

	/**
	 * Utility method to get access to the TileEntity fields and methods for the
	 * crop.
	 *
	 * @return the TileEntity implementing ICrop
	 */
	TileEntity getTileEntity();

	/**
	 * @return Any additional data this crop might hold
	 */
	IAdditionalCropData getAdditionalCropData();

	List<IAgriCrop> getNeighbours();

	List<IAgriCrop> getMatureNeighbours();

}
