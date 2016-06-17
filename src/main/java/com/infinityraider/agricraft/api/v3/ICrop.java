package com.infinityraider.agricraft.api.v3;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.Random;
import javax.annotation.Nonnull;

/**
 * Interface to interact with AgriCraft's crops.
 *
 * To retrieve the ICrop instance use:
 * {@code API.getCrop(World world, int x, int y, int z)}
 */
public interface ICrop {

	/**
	 * @return the x coordinate for this crop
	 */
	int xCoord();

	/**
	 * @return the y coordinate for this crop
	 */
	int yCoord();

	/**
	 * @return the z coordinate for this crop
	 */
	int zCoord();

	/**
	 * @return if this crop has a plant
	 */
	boolean hasPlant();

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
	 * @return the ICropPlant instance planted on this crop
	 */
	IAgriCraftPlant getPlant();

	/**
	 * @return the stats for this crop
	 */
	IAgriCraftStats getStats();

	/**
	 * @return if there are weeds on this crop
	 */
	boolean hasWeed();

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
	 * Sets the plant onto this crop
	 *
	 * @param stats the stats for the plant.
	 * @param seed the seed representing the plant
	 * @param seedMeta the metadata for the seed
	 */
	void setPlant(@Nonnull IAgriCraftStats stats, Item seed, int seedMeta);

	/**
	 * Clears the plant from this crop
	 */
	void clearPlant();

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
	 * @return the Block for the plant currently planted on this crop
	 */
	Block getPlantBlock();

	/**
	 * @return the Block state for the plant currently planted on this crop
	 */
	IBlockState getPlantBlockState();
	
	double getWeedSpawnChance();

	/**
	 * Spawns weeds on this crop
	 */
	void spawnWeed();

	/**
	 * Attempts to spread weeds to neighbouring crops
	 */
	void spreadWeed();

	/**
	 * Clears weeds from this crop
	 */
	void clearWeed();

	/**
	 * Checks if a certain fertilizer may be applied to this crop
	 *
	 * @param fertiliser the fertilizer to be checked
	 * @return if the fertilizer may be applied
	 */
	boolean allowFertiliser(IFertiliser fertiliser);

	/**
	 * Apply fertilizer to this crop
	 *
	 * @param fertiliser the fertilizer to be applied
	 * @param rand a Random object
	 */
	void applyFertiliser(IFertiliser fertiliser, Random rand);

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
	
	List<ICrop> getNeighbours();
	
	List<ICrop> getMatureNeighbours();
	
}
