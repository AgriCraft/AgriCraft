/*
 */
package com.infinityraider.agricraft.utility;

/**
 *
 * @author RlonRyan
 */
public class MathHelper {
	
	/**
	 * Brings an integer into a specified range.
	 *
	 * @param value The value to bring into the range.
	 * @param min The minimum value, inclusive.
	 * @param max The maximum value, inclusive.
	 * @return The in-bounded value.
	 */
	public static int inRange(int value, int min, int max) {
		return value < min ? min : value > max ? max : value;
	}
	
}
