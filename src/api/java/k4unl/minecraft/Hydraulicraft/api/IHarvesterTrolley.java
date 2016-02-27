package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;

public interface IHarvesterTrolley {

	/**
	 * Gets the name for this trolley. Used as an ID
	 * @return
	 */
	String getName();
	
	/**
	 * Whether or not this trolley can harvest the plant at this location.
	 * Use this to detect whether the metadata is sufficient for your plant.
	 * The Y coordinate is the bottom. It links to the location where the crop is located
	 * If you want something like sugar cane, just change the Y coordinate untill you reach the top
	 * of the sugar cane.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	boolean canHarvest(World world, int x, int y, int z);
	
	/**
	 * Whether or not this trolley can plant a seed at this location.
	 * Not all seeds require the same soil. 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param seed
	 * @return
	 */
	boolean canPlant(World world, int x, int y, int z, ItemStack seed);
	
	/**
	 * Which seeds this trolley can handle. 
	 * @return
	 */
	ArrayList<ItemStack> getHandlingSeeds();
	
	/**
	 * What block gets planted into the soil from this seed?
	 * @param seed
	 * @return
	 */
	Block getBlockForSeed(ItemStack seed);

	/**
	 * Pass a reference to the trolley texture in here. Look at the original trolley textures to see how the map is layed out.
	 * @return
	 */
	ResourceLocation getTexture();
	
	
	/**
	 * Gets the plant height at this location.
	 * Means that canHarvest will be called from this location y+length, to y.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	int getPlantHeight(World world, int x, int y, int z);

}
