package com.infinityraider.agricraft.content.world.greenhouse;

public enum GreenHouseBlockType {
    INTERIOR_AIR(true, false),
    INTERIOR_OTHER(true, false),
    BOUNDARY(false, true),
    GLASS(false, true),
    EXTERIOR(false, false);

    private final boolean interior;
    private final boolean boundary;

    GreenHouseBlockType(boolean interior, boolean boundary) {
        this.interior = interior;
        this.boundary = boundary;
    }

    public boolean isAir() {
        return this == INTERIOR_AIR;
    }

    public boolean isInterior() {
        return this.interior;
    }

    public boolean isBoundary() {
        return this.boundary;
    }

    public boolean isGlass() {
        return this == GLASS;
    }

    public boolean isExterior() {
        return this == EXTERIOR;
    }
}
