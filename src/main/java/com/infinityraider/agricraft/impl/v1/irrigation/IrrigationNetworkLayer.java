package com.infinityraider.agricraft.impl.v1.irrigation;

import net.minecraft.util.math.MathHelper;

public class IrrigationNetworkLayer {
    private final double min;
    private final double max;
    private final int volume;

    public IrrigationNetworkLayer(double min, double max, int volume) {
        this.min = min;
        this.max = max;
        this.volume = volume;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public int getVolume() {
        return this.volume;
    }

    public double getHeight(int content) {
        if(content <= 0) {
            return this.getMin();
        }
        if(content >= this.getVolume()) {
            return this.getMax();
        }
        double f = (content + 0.0D)/this.getVolume();
        return MathHelper.lerp(f, this.getMin(), this.getMax());
    }
}
