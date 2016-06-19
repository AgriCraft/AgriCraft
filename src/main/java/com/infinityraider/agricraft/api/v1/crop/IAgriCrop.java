package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.misc.IHarvestable;
import com.infinityraider.agricraft.api.v1.seed.ISeedAcceptor;
import com.infinityraider.agricraft.api.v1.seed.ISeedProvider;
import java.util.List;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.math.BlockPos;
import com.infinityraider.agricraft.api.v1.misc.IWeedable;
import com.infinityraider.agricraft.api.v1.fertilizer.IFertilizable;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve the ICrop instance use:
 * {@code API.getCrop(World world, int x, int y, int z)}
 */
public interface IAgriCrop extends ISeedProvider, ISeedAcceptor, IWeedable, IFertilizable, IHarvestable {

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

	/**
	 * Utility method to get access to the TileEntity fields and methods for the
	 * crop
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
