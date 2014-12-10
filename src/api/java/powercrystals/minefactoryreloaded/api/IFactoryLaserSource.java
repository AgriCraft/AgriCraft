package powercrystals.minefactoryreloaded.api;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author skyboy
 *
 * Defines a target for the laser blocks. TileEntities that implement this interface will sustain the beam.
 */
public interface IFactoryLaserSource {
	/**
	 * @param from The direction the laser is oriented
	 * @return true if the beam should be sustained from this side
	 */
	public boolean canFormBeamFrom(ForgeDirection from);
}
