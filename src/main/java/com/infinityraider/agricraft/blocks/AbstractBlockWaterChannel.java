/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks;

import net.minecraft.block.properties.IProperty;
import net.minecraft.util.math.AxisAlignedBB;

/**
 *
 * @author RlonRyan
 */
public abstract class AbstractBlockWaterChannel extends BlockCustomWood {

	public AbstractBlockWaterChannel(String internalName) {
		super("water_channel_" + internalName, false);
	}

	public AbstractBlockWaterChannel(String internalName, AxisAlignedBB box) {
		super("water_channel_" + internalName, false, box);
	}


	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}
	
}
