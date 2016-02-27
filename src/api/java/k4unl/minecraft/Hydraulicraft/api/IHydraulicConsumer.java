package k4unl.minecraft.Hydraulicraft.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IHydraulicConsumer extends IHydraulicMachine {
	/**
	 * @author Koen Beckers
	 * @date 14-12-2013
	 * Function that gets called to let the machine do its work
	 * if Simulate is true, the machine shouldn't be doing anything. just returning how much it would've used.
	 * @param from TODO
	 * @return The amount of pressure that gets lost when doing this.
	 */
	
	float workFunction(boolean simulate, ForgeDirection from);
	
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
}
