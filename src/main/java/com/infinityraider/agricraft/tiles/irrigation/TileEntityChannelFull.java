package com.infinityraider.agricraft.tiles.irrigation;

import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChannelFull extends TileEntityChannel {

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasNeighbourCheck(AgriForgeDirection direction) {
		if (this.worldObj == null) {
			return true;
		} else {
			return super.hasNeighbourCheck(direction);
		}
	}

}
