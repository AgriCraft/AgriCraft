package com.InfinityRaider.AgriCraft.api.v2;

import com.InfinityRaider.AgriCraft.api.v1.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface APIv2 extends APIv1 {
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
}
