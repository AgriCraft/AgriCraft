/*
 */
package com.infinityraider.agricraft.reference;

import net.minecraft.util.IStringSerializable;

/**
 *
 * @author RlonRyan
 */
public enum WoodType implements IStringSerializable {

	Oak,
	Spruce,
	Birch,
	Jungle,
	Acacia,
	Big_Oak;
	
	final String texture;

	private WoodType() {
		this.texture = "minecraft:blocks/planks_" + name().toLowerCase();
	}

	private WoodType(String texture) {
		this.texture = texture;
	}

	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
	
	public String getTexture() {
		return this.texture;
	}
	
	public static WoodType getType(int id) {
		return values()[id % values().length];
	}

}
