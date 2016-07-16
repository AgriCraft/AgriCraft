/*
 */
package com.infinityraider.agricraft.utility;

import net.minecraft.util.IStringSerializable;

/**
 *
 * @author RlonRyan
 */
public enum Axis implements IStringSerializable {
	X, Y, Z;

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
	
}
