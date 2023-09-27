package com.agricraft.agricraft.common.block;

import net.minecraft.util.StringRepresentable;

public enum CropState implements StringRepresentable {
	PLANT,
	PLANT_STICKS,
	SINGLE_STICKS,
	DOUBLE_STICKS;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}

	public boolean hasSticks() {
		return this ==  PLANT_STICKS || this == SINGLE_STICKS || this == DOUBLE_STICKS;
	}

	public boolean hasPlant() {
		return this == PLANT || this == PLANT_STICKS;
	}

}
