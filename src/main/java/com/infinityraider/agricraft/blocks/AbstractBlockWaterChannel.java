/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;

/**
 *
 * @author RlonRyan
 */
public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

	public AbstractBlockWaterChannel(String internalName) {
		super("water_channel_" + internalName, false);
	}
	
}
