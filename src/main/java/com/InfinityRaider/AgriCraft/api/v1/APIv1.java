package com.InfinityRaider.AgriCraft.api.v1;

import java.util.List;

import com.InfinityRaider.AgriCraft.api.APIBase;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * <h1>This is the AgriCraft API, version 1.</h1>
 *
 * <p>
 * General notes:
 * </p>
 * 
 * <ul>
 * <li>The methods of this API will never modify the parameter objects unless
 * explicitly stated.
 * <li>All parameters are required and may not be null unless stated otherwise.
 * <li>Return values will never be null unless stated otherwise.
 * </ul>
 *
 */
public interface APIv1 extends APIBase {

	/**
	 * Return true if AgriCraft is active in the given dimension. If the
	 * parameter is null, it will return true if AgriCraft is active in any
	 * dimension.
	 * 
	 * If this returns false, you can completely ignore AgriCraft (for the given
	 * dimension).
	 * 
	 * This may return false if AgriCraft is disabled (e.g. it is only included
	 * in a modpack for some items), or if a dimension is blacklisted.
	 * 
	 * @param world
	 *            An optional world
	 */
	boolean isActive(World world);

	/**
	 * @return A list of items that are considered "crops". The list may be
	 *         empty, but will never be null.
	 */
	List<ItemStack> getCropsItems();

	/**
	 * @return A list of items that are considered "rakes" and can be used to
	 *         weed. The list may be empty, but will never be null.
	 */
	List<ItemStack> getRakeItems();

	/**
	 * @return A list of blocks that are placed "crops". The list may be empty,
	 *         but will never be null.
	 */
	List<Block> getCropsBlocks();

	/**
	 * Checks if the given item as some kind of seed that AgriCraft knows about.
	 * Please note that this does NOT mean that it can be planted in crops, as
	 * it may be disabled by aconfig setting.
	 * 
	 * @param seed
	 *            Any ItemStack.
	 * @return True if the given item is a seed item.
	 */
	boolean isSeed(ItemStack seed);

	/**
	 * Checks if AgriCraft is configured to prevent the given seed to be used in
	 * its normal way. Note: This method may ignore its parameter if AgriCraft
	 * is configured to prevent all native planting it can prevent.
	 * 
	 * @param seed
	 *            Any ItemStack that is a seed.
	 * @return True if the seed must be handled by AgriCraft.
	 */
	boolean isNativePlantingDisabled(ItemStack seed);

	/**
	 * Checks if the given seed can be handled by AgriCraft, i.e. can be planted
	 * in crops.
	 * 
	 * @param seed
	 *            Any ItemStack that is a seed.
	 * @return True if the seed can be planted in crops.
	 */
	boolean isHandledByAgricraft(ItemStack seed);

	/**
	 * Gives the statistics for the given seeds.
	 * 
	 * @param seed
	 *          Any ItemStack that is a seed.
	 * @return An ISeedStats object that describes the given seeds, or null if the
	 *         given item was no seed.
	 */
	ISeedStats getSeedStats(ItemStack seed);
	
	/**
	 * Checks the seeds planting requirements.
	 * 
	 * @param seed
	 *            Any ItemStack that is a seed.
	 * @return A {@link ISeedRequirements} object or null if the parameter was no
	 *         seed or has special requirements.
	 */
	ISeedRequirements getSeedRequirements(ItemStack seed);

	/**
	 * Checks if the given crops can be placed at the given position.
	 * 
	 * <p>
	 * Note that the position is the air block above the ground, not the ground
	 * block a player would click on.
	 * </p>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param crops
	 *            An item stack of one of the items getCropsItems() returns.
	 * @return True if the crops can be placed.
	 */
	boolean canPlaceCrops(World world, int x, int y, int z, ItemStack crops);

	/**
	 * Will place the given crops at the given position. On success the item
	 * stack's size will be decreased (and may be 0). All world interaction will
	 * be handled by this method.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param crops
	 *            An item stack of at least one of the items getCropsItems()
	 *            returns. Will be modified on success!
	 * @return True if the crops were successfully placed, false otherwise.
	 */
	boolean placeCrops(World world, int x, int y, int z, ItemStack crops);

	/**
	 * Checks if the given position contains crops.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return True if there are crops.
	 */
	boolean isCrops(World world, int x, int y, int z);

	/**
	 * Checks if the given position contains crops with a mature plant in it.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return True if there is a mature (harvestable) plant, false otherwise.
	 */
	boolean isMature(World world, int x, int y, int z);

	/**
	 * Checks if the given position contains crops with weeds in them.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return True if there are weeds, false otherwise.
	 */
	boolean isWeeds(World world, int x, int y, int z);

	/**
	 * Checks if the given position contains empty crops.
	 * 
	 * <p>
	 * Note: Crops with crosscrops are NOT considered empty.
	 * </p>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return True if there are empty crops, false otherwise.
	 */
	boolean isEmpty(World world, int x, int y, int z);

	/**
	 * Checks if the given position contains crops with crosscrops.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return True if there are crosscrops, false otherwise.
	 */
	boolean isCrossCrops(World world, int x, int y, int z);

	/**
	 * Checks if the plant that is in crops at the given position can grow. A
	 * plant cannot grow if:
	 * <ul>
	 * <li>There are no crops at the given position
	 * <li>There is no plant in the crops
	 * <li>The plant is mature
	 * <li>There are weeds instead of a plant
	 * <li>Not all growth conditions are met
	 * </ul>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return True if the given plant can grow, false otherwise.
	 */
	boolean canGrow(World world, int x, int y, int z);

	/**
	 * Checks if AgriCraft is configured to require rakes to remove weeds.
	 * 
	 * @return True if weeds cannot be removed by hand.
	 */
	boolean isRakeRequiredForWeeding();

	/**
	 * Tries to remove the weeds at the given position.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param byHand
	 *            True if the interaction should be considered as a player using
	 *            their bare hand or not. If not, it is assumed that the caller
	 *            is a machine that has an equivalent of rakes built in.
	 * @return True if weeds were removed. False if the removal failed, either
	 *         because there were no weeds, or both isRakeRequiredForWeeding and
	 *         byHand are true.
	 */
	boolean removeWeeds(World world, int x, int y, int z, boolean byHand);

	/**
	 * Tries to remove the weeds at the given position with the given tool. If
	 * the tool takes damage, this method will update the item stack.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param rake
	 *            An item stack of one of the items getRakeItems() returns. Will
	 *            possibly be modified on success!
	 * @return True if weeds were removed. False if the removal failed, either
	 *         because there were no weeds, or the given rake is not valid for
	 *         the job.
	 */
	boolean removeWeeds(World world, int x, int y, int z, ItemStack rake);

	/**
	 * Tries to place crosscrops on the crops at the given position. On success
	 * the item stack's size will be decreased (and may be 0). All world
	 * interaction will be handled by this method.
	 * 
	 * <p>
	 * Possible reasons for failure:
	 * </p>
	 * <ul>
	 * <li>There are no crops at the given position
	 * <li>There is a plant in the crops
	 * <li>The given item is no crops
	 * <li>The given crops don't match the existing ones
	 * </ul>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param crops
	 *            An item stack of at least one of the items getCropsItems()
	 *            returns. Will be modified on success!
	 * @return True if the crosscrops were placed, false otherwise.
	 */
	boolean placeCrossCrops(World world, int x, int y, int z, ItemStack crops);

	/**
	 * Tries to remove crosscrops at the given position. The removed crops will
	 * be returned.
	 * 
	 * Note that the return value is the same if the removal failed and if the
	 * removal gained to item. Check isCrosscrops() afterward if you need to
	 * differentiate these cases.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return An item stack with the removed crops or null if nothing was
	 *         removed.
	 */
	ItemStack removeCrossCrops(World world, int x, int y, int z);

	/**
	 * Checks if the given seeds can be placed into the crops at the given
	 * position. See {@link SeedRequirementStatus} for details.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param seed
	 *            An item stack of seeds.
	 * @return A SeedRequirementStatus object.
	 */
	SeedRequirementStatus canApplySeeds(World world, int x, int y, int z,
			ItemStack seed);

	/**
	 * Tries to place the given seeds into the crops at the given position. On
	 * success the item stack's size will be decreased (and may be 0). All world
	 * interaction will be handled by this method.
	 * 
	 * <p>
	 * Note: There is no need to protect this method by calling canApplySeeds()
	 * before.
	 * </p>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param seed
	 *            An item stack of seeds. Will be modified on success!
	 * @return True if the seeds were placed, false otherwise.
	 */
	boolean applySeeds(World world, int x, int y, int z, ItemStack seed);

	/**
	 * Tries to harvest the content of the crops at the given position. This
	 * equates to a player right-clicking the crops; it will return a harvest
	 * and leave the crops and the harvested plant in place. All world
	 * interaction will be handled by this method.
	 * 
	 * <p>
	 * It will return a list of harvested items which may be empty for crops
	 * with a chance-based harvest result. If the harvest failed for any reason,
	 * the result will be null.
	 * </p>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return A harvest result or null.
	 */
	List<ItemStack> harvest(World world, int x, int y, int z);

	/**
	 * Tries to destroy the crops at the given position. This equates to a
	 * player left-clicking the crops; it will return the broken crops, the seed
	 * and possibly a harvest and set the block to air. All world interaction
	 * will be handled by this method.
	 * 
	 * <p>
	 * It will return a list of dropped items which may be empty. If the
	 * destroying failed for any reason, e.g. the position did not specify a
	 * crops, the result will be null.
	 * </p>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return A list of dropped items or null.
	 */
	List<ItemStack> destroy(World world, int x, int y, int z);

	/**
	 * Checks if the given item is any form of supported fertilizer (e.g. bone
	 * meal).
	 * 
	 * <p>
	 * Note: A "fertilizer" is any item that can be applied to a growing plant,
	 * regardless of the effect.
	 * </p>
	 * 
	 * @param fertilizer
	 *            Any item.
	 * @return True if AgriCraft knows how to handle the given item as
	 *         fertilizer.
	 */
	boolean isSupportedFertilizer(ItemStack fertilizer);

	/**
	 * Checks if the given fertilizer is valid for the plant in the crop at the
	 * given position.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param fertilizer
	 *            Any item, preferable one that is a fertilizer.
	 * @return True if the item is a valid fertilizer and can be applied, false
	 *         otherwise.
	 */
	boolean isValidFertilizer(World world, int x, int y, int z,
			ItemStack fertilizer);

	/**
	 * Tries to apply the given fertilizer to the plant in the crop at the given
	 * position. On success the item stack's size will be decreased (and may be
	 * 0). All world interaction will be handled by this method.
	 * 
	 * <p>
	 * Note: The return value does not state if the plant was effected by the
	 * fertilizer, only if it was applied.
	 * </p>
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param fertilizer
	 *            Any item, preferable one that is a fertilizer. Will be
	 *            modified on success!
	 * @return True if the fertilizer was applied successfully, false otherwise.
	 */
	boolean applyFertilizer(World world, int x, int y, int z,
			ItemStack fertilizer);

}
