package k4unl.minecraft.Hydraulicraft.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IHydraulicGenerator extends IHydraulicMachine {
    /**
     * Function that gets called when there's work to be done
     * You need to take care of adding pressure to the network!
     * @author Koen Beckers
     * @date 14-12-2013
     * @param from TODO
     */
    void workFunction(ForgeDirection from);
    
	/**
	 * Returns whether or not this block can do work on this side.
	 * DO NOT JUST RETURN TRUE!
	 * If it doesn't matter which direction this block can do it's work from,
	 * Only return true on ForgeDirection.UP!
	 * If you return true on all directions, the block will be doing way too much work!
	 * @param dir
	 * @return Whether or not this block can do work from this side.
	 */
	boolean canWork(ForgeDirection dir);
    
    /**
     * @author Koen Beckers
     * @date 14-12-2013
     * @param from TODO
     * @return How much the generator can max output
     */
    int getMaxGenerating(ForgeDirection from);
    
    
    /**
     * @author Koen Beckers
     * @date 14-12-2013
     * @param from TODO
     * @return How much the generator is currently generating
     */
    float getGenerating(ForgeDirection from);
}
