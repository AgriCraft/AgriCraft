/*
 * A intermediate for the water channel blocks.
 */
package com.InfinityRaider.AgriCraft.blocks;

import net.minecraft.block.properties.IProperty;

/**
 *
 * @author RlonRyan
 */
public abstract class AbstractBlockWaterChannel extends BlockCustomWood {

	public AbstractBlockWaterChannel(String internalName) {
		super(internalName, false);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}
	
}
