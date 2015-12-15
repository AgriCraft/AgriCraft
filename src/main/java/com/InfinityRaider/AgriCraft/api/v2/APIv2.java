package com.InfinityRaider.AgriCraft.api.v2;

import com.InfinityRaider.AgriCraft.api.v1.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <h1>This is the AgriCraft API, version 2.</h1>
 *
 * <p>
 * General notes:
 * </p>
 *
 * <ul>
 * <li>version 2 is backwards compatible with version 1,
 * methods from version 1 are overridden here to clarify that they still work as before,
 * methods not overridden here no longer work and should not be used.
 * <li>The methods of this API will never modify the parameter objects unless
 * explicitly stated.
 * <li>All parameters are required and may not be null unless stated otherwise.
 * <li>Return values will never be null unless stated otherwise.
 * </ul>
 *
 */
@SuppressWarnings("unused")
public interface APIv2 extends APIv1 {

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
    @Override
    boolean isActive(World world);

    /**
     * @return A list of items that are considered "crops". The list may be
     *         empty, but will never be null.
     */
    @Override
    List<ItemStack> getCropsItems();

    /**
     * @return A list of items that are considered "rakes" and can be used to
     *         weed. The list may be empty, but will never be null.
     */
    @Override
    List<ItemStack> getRakeItems();

    /**
     * @return A list of blocks that are placed "crops". The list may be empty,
     *         but will never be null.
     */
    @Override
    List<Block> getCropsBlocks();

    /**
     * Checks if AgriCraft is configured to prevent the given seed to be used in
     * its normal way. Note: This method may ignore its parameter if AgriCraft
     * is configured to prevent all native planting it can prevent.
     *
     * @param seed
     *            Any ItemStack that is a seed.
     * @return True if the seed must be handled by AgriCraft.
     */
    @Override
    boolean isNativePlantingDisabled(ItemStack seed);

    /**
     * Checks if the given seed can be handled by AgriCraft, i.e. can be planted
     * in crops.
     *
     * @param seed
     *            Any ItemStack that is a seed.
     * @return True if the seed can be planted in crops.
     */
    @Override
    boolean isHandledByAgricraft(ItemStack seed);

    /**
     * Gives the statistics for the given seeds.
     *
     * @param seed
     *          Any ItemStack that is a seed.
     * @return An ISeedStats object that describes the given seeds, or null if the
     *         given item was no seed.
     */
    @Override
    ISeedStats getSeedStats(ItemStack seed);

    /**
     * Gets the ICropPlant object containing all the data AgriCraft knows about this seed
     * @param seed Stack holding the seed
     * @return an ICropPlant object if the seed is considered a seed for AgriCraft, or null if not
     */
    @Override
    ICropPlant getCropPlant(ItemStack seed);

    /**
     * Register a cropPlant for AgriCraft to recognise as a valid plant for crops
     */
    @Override
    void registerCropPlant(IAgriCraftPlant plant);

    /**
     * Register a growth requirement for this seed,
     * This will effectively override the IGrowthRequirement previously registered for the given seed
     *
     * @return true if the registering was successful
     */
    @Override
    boolean registerGrowthRequirement(ItemWithMeta seed, IGrowthRequirement requirement);

    /**
     * Register a default soil that any crop that doesn't require a specific soil can grow on
     * @return true if the soil was successfully registered
     */
    @Override
    boolean registerDefaultSoil(BlockWithMeta soil);

    /**
     * Checks the seeds planting requirements.
     *
     * @param seed
     *            Any ItemStack that is a seed.
     * @return A {@link IGrowthRequirement} object or null if the parameter was no
     *         seed or has special requirements.
     */
    @Override
    IGrowthRequirement getGrowthRequirement(ItemStack seed);

    /**
     * Checks if the given crops can be placed at the given position.
     *
     * <p>
     * Note that the position is the air block above the ground, not the ground
     * block a player would click on.
     * </p>
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param crops
     *            An item stack of one of the items getCropsItems() returns.
     * @return True if the crops can be placed.
     */
    @Override
    boolean canPlaceCrops(World world, int x, int y, int z, ItemStack crops);

    /**
     * Will place the given crops at the given position. On success the item
     * stack's size will be decreased (and may be 0). All world interaction will
     * be handled by this method.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param crops
     *            An item stack of at least one of the items getCropsItems()
     *            returns. Will be modified on success!
     * @return True if the crops were successfully placed, false otherwise.
     */
    @Override
    boolean placeCrops(World world, int x, int y, int z, ItemStack crops);

    /**
     * Checks if the given position contains crops.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return True if there are crops.
     */
    @Override
    boolean isCrops(World world, int x, int y, int z);

    /**
     * Checks if the given position contains crops with a mature plant in it.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return True if there is a mature (harvestable) plant, false otherwise.
     */
    @Override
    boolean isMature(World world, int x, int y, int z);

    /**
     * Checks if the given position contains crops with weeds in them.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return True if there are weeds, false otherwise.
     */
    @Override
    boolean isWeeds(World world, int x, int y, int z);

    /**
     * Checks if the given position contains empty crops.
     *
     * <p>
     * Note: Crops with crosscrops or weeds are NOT considered empty.
     * </p>
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return True if there are empty crops, false otherwise.
     */
    @Override
    boolean isEmpty(World world, int x, int y, int z);

    /**
     * Checks if the given position contains crops with crosscrops.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return True if there are crosscrops, false otherwise.
     */
    @Override
    boolean isCrossCrops(World world, int x, int y, int z);

    /**
     * Gets the seed currently planted on the crop sticks at this location
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return an ItemStack with the seed currently planted on this crop, returns null if there is no crop there, or there is no seed planted
     */
    @Override
    ItemStack getPlantedSeed(World world, int x, int y, int z);

    /**
     * Gets the Block instance of the block currently planted on the crop sticks at this location
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return an ItemStack with the seed currently planted on this crop, returns null if there is no crop there, or there is no seed planted
     */
    @Override
    Block getPlantedBlock(World world, int x, int y, int z);

    /**
     * Gets the ICropPlant object containing all the data AgriCraft knows about the seed planted on this crop
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return an ICropPlant object if there is a seed planted here, or null if not
     */
    @Override
    ICropPlant getCropPlant(World world, int x, int y, int z);

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
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return True if the given plant can grow, false otherwise.
     */
    @Override
    boolean canGrow(World world, int x, int y, int z);

    /**
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return true if the crop at the location is analyzed, false if not, or if there is no crop with a plant at the location
     */
    @Override
    boolean isAnalyzed(World world, int x, int y, int z);

    /**
     * Returns the stats of the crop at the given location.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return ISeedStats object holding the stats or null if there is no crop there, or the crop doesn't have a plant
     */
    @Override
    ISeedStats getStats(World world, int x, int y, int z);

    /**
     * Checks if AgriCraft is configured to require rakes to remove weeds.
     *
     * @return True if weeds cannot be removed by hand.
     */
    @Override
    boolean isRakeRequiredForWeeding();

    /**
     * Tries to remove the weeds at the given position.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param byHand
     *            True if the interaction should be considered as a player using
     *            their bare hand or not. If not, it is assumed that the caller
     *            is a machine that has an equivalent of rakes built in.
     * @return True if weeds were removed. False if the removal failed, either
     *         because there were no weeds, or both isRakeRequiredForWeeding and
     *         byHand are true.
     */
    @Override
    boolean removeWeeds(World world, int x, int y, int z, boolean byHand);

    /**
     * Tries to remove the weeds at the given position with the given tool. If
     * the tool takes damage, this method will update the item stack.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param rake
     *            An item stack of one of the items getRakeItems() returns. Will
     *            possibly be modified on success!
     * @return True if weeds were removed. False if the removal failed, either
     *         because there were no weeds, or the given rake is not valid for
     *         the job.
     */
    @Override
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
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param crops
     *            An item stack of at least one of the items getCropsItems()
     *            returns. Will be modified on success!
     * @return True if the crosscrops were placed, false otherwise.
     */
    @Override
    boolean placeCrossCrops(World world, int x, int y, int z, ItemStack crops);

    /**
     * Tries to remove crosscrops at the given position. The removed crops will
     * be returned.
     *
     * Note that the return value is the same if the removal failed and if the
     * removal gained to item. Check isCrosscrops() afterward if you need to
     * differentiate these cases.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return An item stack with the removed crops or null if nothing was
     *         removed.
     */
    @Override
    ItemStack removeCrossCrops(World world, int x, int y, int z);

    /**
     * Checks if the given seeds can be placed into the crops at the given
     * position. See {@link SeedRequirementStatus} for details.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param seed
     *            An item stack of seeds.
     * @return A SeedRequirementStatus object.
     */
    @Override
    SeedRequirementStatus canApplySeeds(World world, int x, int y, int z, ItemStack seed);

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
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param seed
     *            An item stack of seeds. Will be modified on success!
     * @return True if the seeds were placed, false otherwise.
     */
    @Override
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
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return A harvest result or null.
     */
    @Override
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
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return A list of dropped items or null.
     */
    @Override
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
    @Override
    boolean isSupportedFertilizer(ItemStack fertilizer);

    /**
     * Checks if the given fertilizer is valid for the plant in the crop at the
     * given position.
     *
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param fertilizer
     *            Any item, preferable one that is a fertilizer.
     * @return True if the item is a valid fertilizer and can be applied, false
     *         otherwise.
     */
    @Override
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
     * @param world World object
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param fertilizer
     *            Any item, preferable one that is a fertilizer. Will be
     *            modified on success!
     * @return True if the fertilizer was applied successfully, false otherwise.
     */
    @Override
    boolean applyFertilizer(World world, int x, int y, int z,
                            ItemStack fertilizer);

    /**
     * Gets a list of all mutations currently registered
     * Mutations are populated onServerAboutToStartEvent, so any calls before that will return null
     */
    @Override
    IMutation[] getRegisteredMutations();

    /**
     * Gets a list of all mutations that have this stack as a parent
     * Mutations are populated onServerAboutToStartEvent, so any calls before that will return null
     */
    @Override
    IMutation[] getRegisteredMutationsForParent(ItemStack parent);

    /**
     * Gets a list of all mutations that have this stack as a child
     * Mutations are populated onServerAboutToStartEvent, so any calls before that will return null
     */
    @Override
    IMutation[] getRegisteredMutationsForChild(ItemStack child);


    /**
     * Registers a new mutation: result = parent1 + parent2
     * @param result ItemStack containing the resulting seed of the mutation
     * @param parent1 ItemStack containing one parent for the mutation
     * @param parent2 ItemStack containing the other parent for the mutation
     * @return True if successful
     */
    @Override
    boolean registerMutation(ItemStack result, ItemStack parent1, ItemStack parent2);

    /**
     * Registers a new mutation: result = parent1 + parent2
     * @param result ItemStack containing the resulting seed of the mutation
     * @param parent1 ItemStack containing one parent for the mutation
     * @param parent2 ItemStack containing the other parent for the mutation
     * @param chance the chance for this mutation to occur, default is 0.2D
     * @return True if successful
     */
    @Override
    boolean registerMutation(ItemStack result, ItemStack parent1, ItemStack parent2, double chance);

    /**
     * Removes all mutations that give this stack as a result
     * @param result ItemStack containing the resulting seed for all the mutations to be removed
     * @return True if successful
     */
    @Override
    boolean removeMutation(ItemStack result);

    /**
     * This method creates the block and the seed for a new plant that is fully compatible with agricraft.
     * This method also registers this block and the item for the seed to the minecraft item/block registry and to the AgriCraft CropPlantHandler.
     * @param args: Arguments can be given in any order, parameters can be:
     *               String name (needed)
     *               ItemStack fruit(needed)
     *               BlockWithMeta soil (optional, pass this argument before the RequirementType, else it will be interpreted as a baseblock)
     *               RequirementType (optional)
     *               BlockWithMeta baseBlock (optional, only if a RequirementType is specified first, else it will be set a a soil)
     *               int tier (necessary)
     *               RenderMethod renderType (necessary)
     *               ItemStack shearDrop (optional, first ItemStack argument will be the regular fruit, second ItemStack argument is the shear drop)
     * @return
     * The Block corresponding with the plant, return type will always be instance of Block as well IAgriCraftPlant.
     * The seed is instance of Item and implements IAgriCraftSeed. It can be obtained by calling getSeed() on the returned object.
     * This method will return null when not all necessary arguments are given.
     */
    @Override
    IAgriCraftPlant createNewCrop(Object... args);

    /**
     * Register a cropPlant for AgriCraft to recognise as a valid plant for crops
     */
    void registerCropPlant(ICropPlant plant);

    /**
     * Register a  soil that crop sticks can be placed on use this if you have your own ICropPlant which doesn't use IGrowthRequirement
     * @return true if the soil was successfully registered
     */
    boolean registerValidSoil(BlockWithMeta soil);

    /**
     * @return the cap imposed to seed stats specified in the config
     */
    short getStatCap();

    /**
     * Analyzes the seed in the stack
     * @param seed the stack
     */
    void analyze(ItemStack seed);

    /**
     * Gets the crop at the given location.
     * Calling this method once and then calling needed methods on the ICrop object is much more efficient then calling the methods separately via the API.
     * This only requires 1 call to world.getTileEntity(x, y, z) instead of multiple api.someMethod(world, x, y, z) and then api.someOtherMethod(world, x, y, z)
     *
     * @param world the World object
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return ICrop object
     */
    ICrop getCrop(World world, int x, int y, int z);

    /**
     * Method used to set custom stat calculation logic
     * @param calculator the IStatCalculator Object to be used when calculating stats
     */
    void setStatCalculator(IStatCalculator calculator);

    /**
     * Gets a new IGrowthRequirementBuilder object used to create new IGrowthRequirement objects
     * @return a new IGrowthRequirementBuilder instance
     */
    IGrowthRequirementBuilder createGrowthRequirementBuilder();

    /**
     * @return a new IGrowthRequirement object which has default vanilla behaviour, meaning no base block, soil is farmland and requires brightness
     */
    IGrowthRequirement createDefaultGrowthRequirement();

    /**
     * Method used to set custom stat displaying methods
     * @param displayer the IStatStringDisplayer Object to be used when displaying stat strings in tooltips
     */
    @SideOnly(Side.CLIENT)
    void setStatStringDisplayer(IStatStringDisplayer displayer);

    /**
     * Checks if a seed is discovered in the journal
     * @param journal an ItemStack holding the journal
     * @param seed an ItemStack containing a seed
     * @return if the seed is discovered in the journal
     */
    boolean isSeedDiscoveredInJournal(ItemStack journal, ItemStack seed);

    /**
     * This adds an entry the journal, for example when a seed is analyzed in the seed analyzer this method is called.
     * This internally checks if the seed is discovered already before adding to prevent duplicate entries
     * @param journal an ItemStack holding the journal
     * @param seed an ItemStack containing a seed
     */
    void addEntryToJournal(ItemStack journal, ItemStack seed);

    /**
     * Gets an ArrayList containing all seeds discovered in this journal
     * @param journal an ItemStack holding the journal
     * @return an ArrayList containing an ItemStack for every discovered seed (the list may be empty but will never be null)
     */
    ArrayList<ItemStack> getDiscoveredSeedsFromJournal(ItemStack journal);

    /**
     * Checks if a seed is BlackListed
     * @param seed the seed to check
     * @return if the seed is blacklisted and should not be plantable on crop sticks
     */
    boolean isSeedBlackListed(ItemStack seed);

    /**
     * Adds a seed to the blacklist
     * @param seed the seed to add to the blacklist
     */
    void addToSeedBlackList(ItemStack seed);

    /**
     * Adds a collection of seeds to the blacklist
     * @param seeds collection containing all seeds to be added to the blacklist
     */
    void addToSeedBlacklist(Collection<? extends ItemStack> seeds);

    /**
     * Removes a seed from the blacklist
     * @param seed the seed to be removed from the blacklist
     */
    void removeFromSeedBlackList(ItemStack seed);

    /**
     * Removes a collection of seeds from the blacklist
     * @param seeds collection containing all seeds to be removed from the blacklist
     */
    void removeFromSeedBlacklist(Collection<? extends ItemStack> seeds);
}
