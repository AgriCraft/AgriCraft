package com.InfinityRaider.AgriCraft.api.v2;

import com.InfinityRaider.AgriCraft.api.v1.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;

public interface APIv2 extends APIv1 {
    /**
     * Register a  soil that crop sticks can be placed on use this if you have your own ICropPlant which doesn't use IGrowthRequirement
     * @return true if the soil was successfully registered
     */
    boolean registerValidSoil(BlockWithMeta soil);

    short getStatCap();

    /**
     * Returns the stats of the crop at the given location.
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @return ISeedStats object holding the stats or null if there is no crop there, or the crop doesn't have a plant
     */
    ISeedStats getStats(World world, int x, int y, int z);

    /**
     * Gets the stats for the seed the given ItemStack
     * @param seed Any ItemStack that is a seed.
     * @return the stats for this stack
     */
    @Override
    ISeedStats getSeedStats(ItemStack seed);

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
     * Gets a new IGrowthRequirementBuilder object used to create new IGrowthRequirements
     * @return a new IGrowthRequirementBuilder instance
     */
    IGrowthRequirementBuilder createGrowthRequirementBuilder();

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
}
