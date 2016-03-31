/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.math.AxisAlignedBB;

/**
 *
 * @author RlonRyan
 */
public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

	public AbstractBlockWaterChannel(String internalName) {
		super("water_channel_" + internalName, false);
	}

	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}
	
}
