package k4unl.minecraft.Hydraulicraft.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IHydraulicTransporter extends IHydraulicMachine {
	/**
	 * 
	 * @param dir
	 * @return Whether or not the machine is connected. It is different to canConnectTo
	 */
	boolean isConnectedTo(ForgeDirection dir);
	
	/**
	 * Called when the block needs to recheck its connections.
	 * Note, you'll probably only need this if you use a model
	 */
	void checkConnectedSides();
	
}
