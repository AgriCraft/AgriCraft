package com.InfinityRaider.AgriCraft.api.v1;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own ICropPlant object, it will be wrapped by the api.
 * Meaning if you query the ICropPlant object you registered, it will return a different object.
 *
 * DEPRECATED: THIS ONE WILL MOSTLY STILL WORK, BUT IT IS ADVISED TO USE THE ONE IN APIv2
 */
@Deprecated
public interface ICropPlant {
	/**
	 * This method returns the default tier of this plant, tiers can be overridden with the configs.
	 * This should be in the interval [1, 5].
	 *
	 * @return the default tier
	 */
	int tier();

	/**
	 * This returns a new ItemStack holding the seed for this plant
	 *
	 * @return a new ItemStack
	 */
	ItemStack getSeed();

	/**
	 * This method should return the Block for your in world crop, it is used to read data from.
	 *
	 * @return the Block implementation for this crop
	 */
	Block getBlock();

	/**
	 * This method should return all possible fruits for this crop.
	 * It is used for the NEI handler and the journal
	 *
	 * @return an ArrayList holding all possible fruit drops for this crop, regardless of its stats
	 */
	ArrayList<ItemStack> getAllFruits();

	/**
	 * Returns a new ItemStack with a random fruit
	 *
	 * @param rand a Random object
	 * @return a randomly selected fruit
	 */
	ItemStack getRandomFruit(Random rand);

	/**
	 * This method is called to determine the fruit drops when this plant is harvested.
	 * All ItemStacks passed in the ArrayList will be dropped in the world.
	 *
	 * @param gain the gain level of the crop harvested
	 * @param rand a Random object
	 * @return an ArrayList containing all fruits to be dropped in the world
	 */
	ArrayList<ItemStack> getFruitsOnHarvest(int gain, Random rand);

	/**
	 * This method is called when this crop is harvested, but before any default AgriCraft harvest logic has been executed.
	 * It can be used as a notification to keep track of when one of your crops is harvested.
	 * It can also be used to override AgriCraft's harvesting behaviour.
	 * By returning false from this method, you prevent Agricraft from doing any further harvesting operations, effectively cancelling the harvest,
	 * you will then need to perform your own custom operations in this method.
	 *
	 * @param world the World object for the crop
	 * @param x the x coordinate for the crop
	 * @param y the y coordinate for the crop
	 * @param z the z coordinate for the crop
	 * @param player the player harvesting the crop, this can be null if harvested through automation
	 * @return true to allow the harvest or false to cancel the harvest.
	 */
	boolean onHarvest(World world, int x, int y, int z, EntityPlayer player);

	/**
	 * This method is called when the seed for this crop is planted on a crop.
	 * This can happen when the seed is planted, spread from a neighbour or mutated from two parents
	 *
	 * @param world the world object
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	void onSeedPlanted(World world, int x, int y, int z);

	/**
	 * This is called right before this plant is removed from crop sticks, or when the crop sticks are broken.
	 *
	 * @param world the World object
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	void onPlantRemoved(World world, int x, int y, int z);

	/**
	 * This method determines if bonemeal may be applied to this crop
	 * By default, Agricraft does not allow bonemeal on crops higher than tier 3
	 *
	 * @return if bonemeal may be applied to this plant
	 */
	boolean canBonemeal();

	/**
	 * This is called when a growth tick has been allowed. At this point the growth tick can no longer be cancelled
	 * Returning true from this method will make the crop being rendered again client side.
	 * Return false if the new growth stage has the same icon as the old growth stage.
	 *
	 * @param world the World object
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param oldGrowthStage the old growth stage
	 * @return true to rerender the crop client side
	 */
	boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

	/**
	 * Checks if the plant can grow on this position
	 *
	 * DEPRECATED: THIS METHOD IS NO LONGER CALLED
	 */
	@Deprecated
	boolean isFertile(World world, int x, int y, int z);

	/**
	 * Checks if the plant is mature
	 *
	 * @param world the world object
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return true if the plant is fully grown, false if not
	 */
	boolean isMature(IBlockAccess world, int x, int y, int z);

	/**
	 * This gets the height of the crop, this is used to render the bounding boxes client side.
	 * In AgriCraft, for default, 1-block tall crops this is 13/16th of a block
	 *
	 * @param meta the growth stage of the plant
	 * @return the height for the bounding box
	 */
	float getHeight(int meta);

	/**
	 * Gets the icon for the plant, as a function of the plant's growth stage.
	 *
	 * @param growthStage the growth stage of the plant may range from 0 to 7.
	 * @return the icon to render the crop with
	 */
	IIcon getPlantIcon(int growthStage);

	/**
	 * Determines how the plant is rendered.
	 * Returning true will render the texture as four crosses ('x') on each corner, similar to flowers.
	 * Returning false will render the texture as a hash tag ('#') parallel to each side, similar to wheat
	 *
	 * @return false to render the plant in a hash tag shape, true for a cross shape.
	 */
	boolean renderAsFlower();

	/**
	 * Retrieves information about the plant for the seed journal.
	 * It's possible to pass an unlocalized String, the returned value will be localized if possible.
	 *
	 * @return a string describing the plant for use by the seed journal.
	 */
	String getInformation();

	/** Return true if you want to render the plant yourself, else agricraft will render the plant based on the data returned by the getIcon and renderAsFlower methods */
	@SideOnly(Side.CLIENT)
	boolean overrideRendering();

	/**
	 * A function to render the crop. Called when the plant is rendered.
	 * This is never called when returning false from overrideRendering()
	 *
	 * @param world the world the plant is in.
	 * @param x the x coordinate of the plant.
	 * @param y the y coordinate of the plant.
	 * @param z the z coordinate of the plant.
	 * @param renderer the renderer to use in the rendering of the plant.
	 */
	@SideOnly(Side.CLIENT)
	void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer);

}