
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

import java.util.List;
import java.util.Random;

import com.InfinityRaider.AgriCraft.reference.Constants;

/**
 * This interface is used both for you to read the AgriCraft CropPlants as well as coding your own.
 * If you register your own IAgriCraftPlant object, it will be wrapped by the api.
 * Meaning if you query the IAgriCraftPlant object you registered, it will return a different object.
 */
public interface IAgriCraftPlant {

	/**
	 * Returns the GowthRequirement for this plant.
	 */
	public IGrowthRequirement getGrowthRequirement();

	/**
	 * Returns the tier of the seed as represented as an integer value, or the overriding value. The overriding value may be set in the configuration files.
	 * 
	 * Does not always have same output as {@link #tier()}.
	 * 
	 * Should fall within the range of {@link Constants#GROWTH_TIER}.
	 * 
	 * @return the tier of the seed.
	 */
	int getTier();

	/**
	 * Gets a stack of the seed for this plant.
	 * 
	 * @return a stack of the plant's seeds.
	 */
	ItemStack getSeed();

	/**
	 * Gets the block instance for this plant.
	 *
	 * @return the Block object for this plant.
	 */
	Block getBlock();

	/**
	 * Gets a list of all possible fruit drops from this plant.
	 * 
	 * @return a list containing of all possible fruit drops.
	 */
	List<ItemStack> getAllFruits();

	/**
	 * Returns a random fruit for this plant.
	 * 
	 * @param rand a random for choosing the drop.
	 * @return a random fruit dropped by the plant.
	 */
	ItemStack getRandomFruit(Random rand);

	/**
	 * Returns an ArrayList with amount of random fruit stacks for this plant. Gain is passed to allow for different fruits for higher gain levels
	 *
	 * @param gain the gain, as in number of drops.
	 * @param rand a random for choosing the drop.
	 * @return a list containing random fruit drops from this plant.
	 */
	List<ItemStack> getFruitsOnHarvest(int gain, Random rand);

	/**
	 * Gets called right before a harvest attempt, player may be null if harvested by automation. Should return false to prevent further processing. World object and coordinates are passed in the case you would want to keep track of all planted crops and their status
	 * 
	 * @param world the current world of the plant being harvested.
	 * @param x the x-coordinate of the plant being harvested.
	 * @param y the y-coordinate of the plant being harvested.
	 * @param z the z-coordinate of the plant being harvested.
	 * @param player the player attempting to harvest the plant.
	 * @return true, by default.
	 */
	boolean onHarvest(World world, int x, int y, int z, EntityPlayer player);

	/**
	 * Called right after the plant is added to a crop through planting, mutation, or spreading.
	 * 
	 * @param world the world the plant is in.
	 * @param x the x-coordinate of the plant.
	 * @param y the y-coordinate of the plant.
	 * @param z the z-coordinate of the plant.
	 */
	void onSeedPlanted(World world, int x, int y, int z);

	/**
	 * Called right after the plant is removed from a crop, or a crop holding the plant is broken.
	 * 
	 * @param world the world the plant is in.
	 * @param x the x-coordinate of the plant.
	 * @param y the y-coordinate of the plant.
	 * @param z the z-coordinate of the plant.
	 */
	void onPlantRemoved(World world, int x, int y, int z);

	/**
	 * Determines if the plant may be grown with Bonemeal.
	 * 
	 * @return if the plant may be bonemealed.
	 */
	boolean canBonemeal();

	/**
	 * Attempts to apply a growth tick to the plant, if allowed. Should return true to re-render the crop clientside.
	 * 
	 * @param world the world the plant is in.
	 * @param x the x-coordinate of the plant.
	 * @param y the y-coordinate of the plant.
	 * @param z the z-coordinate of the plant.
	 * @param oldGrowthStage the current/old growth stage of the plant.
	 * @return if the plant should be rendered again client side (e.g. if the next growth stage has a different icon)
	 */
	boolean onAllowedGrowthTick(World world, int x, int y, int z, int oldGrowthStage);

	/**
	 * Determines if the plant can grow at a location.
	 * 
	 * @param world the world the plant is in.
	 * @param x the x-coordinate of the plant.
	 * @param y the y-coordinate of the plant.
	 * @param z the z-coordinate of the plant.
	 * @return if the growth location for the plant is fertile.
	 */
	boolean isFertile(World world, int x, int y, int z);

	/**
	 * Determines the height of the crop in float precision, as a function of the plant's metadata(growth stage). Used to render and define the height of the selection bounding box
	 * 
	 * @param meta the growth stage of the plant (may range from 0 to {@link Constants#MATURE}).
	 * @return the current height of the plant.
	 */
	float getHeight(int meta);

	/**
	 * Gets the icon for the plant, as a function of the plant's growth stage.
	 *
	 * @param growthStage the growthstage of the plant may range from 0 to {@link Constants#MATURE}.
	 * @return the current icon for the plant.
	 */
	IIcon getPlantIcon(int growthStage);

	/**
	 * Determines how the plant is rendered.
	 * 
	 * @return false to render the plant as wheat (#), true to render as a flower (X).
	 */
	boolean renderAsFlower();

	/**
	 * Retrieves information about the plant for the seed journal. It's possible to pass an unlocalized String, the returned value will be localized if possible.
	 * 
	 * @return a string describing the plant for use by the seed journal.
	 */
	String getInformation();

	/** Return true if you want to render the plant yourself, else agricraft will render the plant based on the data returned by the getIcon and renderAsFlower methods */
	@SideOnly(Side.CLIENT)
	boolean overrideRendering();

	/**
	 * A function to render the crop. Called when the plant is rendered.
	 * 
	 * @param world the world the plant is in.
	 * @param x the x-coordinate of the plant.
	 * @param y the y-coordinate of the plant.
	 * @param z the z-coordinate of the plant.
	 * @param renderer the renderer to use in the rendering of the plant.
	 */
	@SideOnly(Side.CLIENT)
	void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer);

}
