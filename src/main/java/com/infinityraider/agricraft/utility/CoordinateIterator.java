package com.InfinityRaider.AgriCraft.utility;

public final class CoordinateIterator {
    private boolean x;
    private boolean y;
    private boolean z;
    private int offset = 0;

    public CoordinateIterator() {}

    public CoordinateIterator setX() {
        x = true;
        y = false;
        z = false;
        offset = 0;
        return this;
    }

    public CoordinateIterator setY() {
        x = false;
        y = true;
        z = false;
        offset = 0;
        return this;
    }

    public CoordinateIterator setZ() {
        x = false;
        y = false;
        z = true;
        offset = 0;
        return this;
    }

    public CoordinateIterator reset() {
        this.offset = 0;
        return this;
    }

    public void increment() {
        offset++;
    }

    public int x() {
        return x?offset:0;
    }

    public int y() {
        return y?offset:0;
    }

    public int z() {
        return z?offset:0;
    }

    public int getOffset() {
        return offset;
    }

    public boolean isActive() {
        return x || y || z;
    }
}
