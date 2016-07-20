/*
 */
package com.infinityraider.agricraft.utility;

import net.minecraft.util.IStringSerializable;

/**
 *
 * @author RlonRyan
 */
public enum AxisPosition implements IStringSerializable {
	
	X_NEG, X_MID, X_POS,
	Y_NEG, Y_MID, Y_POS,
	Z_NEG, Z_MID, Z_POS;
	
	static AxisPosition[][] resolved = new AxisPosition[][]{
		{X_NEG, X_MID, X_POS},
		{Y_NEG, Y_MID, Y_POS},
		{Z_NEG, Z_MID, Z_POS}
	};
	
	public static AxisPosition convert(Axis axis, int offset) {
		return resolved[axis.ordinal()][offset % 3];
	}

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
	
}
