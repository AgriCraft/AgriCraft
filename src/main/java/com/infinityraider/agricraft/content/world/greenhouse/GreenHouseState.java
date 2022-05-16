package com.infinityraider.agricraft.content.world.greenhouse;

import net.minecraft.util.StringRepresentable;

public enum GreenHouseState implements StringRepresentable {
    COMPLETE(0.0F, 1.0F, 0.0F),
    INSUFFICIENT_GLASS(0.0F, 1.0F, 1.0F),
    GAPS(1.0F, 1.0F, 0.0F),
    REMOVED(1.0F, 0.0F, 0.0F);

    private final float red;
    private final float green;
    private final float blue;

    GreenHouseState(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public boolean isComplete() {
        return this == COMPLETE;
    }

    public boolean hasGaps() {
        return this == GAPS;
    }

    public boolean isRemoved() {
        return this == REMOVED;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
