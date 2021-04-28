package com.infinityraider.agricraft.util;

import java.util.function.IntSupplier;

/**
 * Utility class which keeps track of the scroll position, and allows to interpolate for smooth transitions
 */
public class AnimatedScrollPosition {
    private final IntSupplier durationSupplier;
    private final IntSupplier maxSupplier;

    private int current;
    private int target;
    private int counter;

    public AnimatedScrollPosition(IntSupplier durationSupplier, IntSupplier maxSupplier) {
        this.durationSupplier = durationSupplier;
        this.maxSupplier = maxSupplier;
        this.reset();
    }

    public int getIndex() {
        return this.current;
    }

    public float getProgress(float partialTick) {
        if(this.target == this.current) {
            return 0;
        }
        return (this.target > this.current ? 1 : -1) * ((this.counter + partialTick) / this.getDuration());
    }

    public int getDuration() {
        return this.durationSupplier.getAsInt();
    }

    public int getMax() {
        return this.maxSupplier.getAsInt();
    }

    public void tick() {
        if(this.current != this.target) {
            this.counter += 1;
            if(counter >= this.getDuration()) {
                this.counter = 0;
                this.current += (this.target > this.current ? 1 : -1);
            }
        } else {
            this.counter = 0;
        }
    }

    public void scroll(int delta) {
        this.target = Math.min(this.getMax() - 1, Math.max(0, this.target - delta));
    }

    public void reset() {
        this.current = 0;
        this.target = 0;
        this.counter = 0;
    }
}
