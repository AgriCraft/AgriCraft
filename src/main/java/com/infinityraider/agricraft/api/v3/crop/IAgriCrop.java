package com.infinityraider.agricraft.api.v3.crop;

import com.infinityraider.agricraft.api.v3.fertiliser.IFertilizable;
import com.infinityraider.agricraft.api.v3.plant.IPlantAcceptor;
import com.infinityraider.agricraft.api.v3.plant.IPlantProvider;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import com.infinityraider.agricraft.api.v3.stat.IStatProvider;
import com.infinityraider.agricraft.api.v3.weed.IWeedable;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve the ICrop instance use:
 * {@code API.getCrop(World world, int x, int y, int z)}
 */
public interface IAgriCrop extends IPlantProvider, IPlantAcceptor, IStatProvider, IWeedable, IFertilizable {

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
	 * @return if a plant can be planted here, meaning the crop is empty and is
	 * not a cross crop
	 */
	boolean canPlant();

	/**
	 * @return if this crop is fertile and thus can grow
	 */
	boolean isFertile();

	/**
	 * @return if bonemeal can be applied to this crop
	 */
	boolean canBonemeal();

	/**
	 * @return if this crop is fully grown
	 */
	boolean isMature();

	/**
	 * @return an ItemStack containing the seed planted on this crop
	 */
	ItemStack getSeedStack();

	/**
	 * Harvests this crop
	 *
	 * @param player the player which harvests the crop, may be null if it is
	 * harvested by automation
	 * @return if the harvest was successful
	 */
	boolean harvest(@Nullable EntityPlayer player);

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
