package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class IrrigationNetworkLayer {
    private final double min;
    private final double max;
    private final int volume;

    public IrrigationNetworkLayer(CompoundNBT tag) {
        this.min = tag.contains(AgriNBT.Y1) ? tag.getDouble(AgriNBT.Y1) : 0;
        this.max = tag.contains(AgriNBT.Y2) ? tag.getDouble(AgriNBT.Y2) : 0;
        this.volume = tag.contains(AgriNBT.LEVEL) ? tag.getInt(AgriNBT.LEVEL) : 0;
    }

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

    public int getCapacity(double height) {
        if(height <= this.getMin()) {
            return 0;
        }
        if(height >= this.getMax()) {
            return this.getVolume();
        }
        return (int) (this.getVolume() * (height - this.getMin()) / (this.getMax() - this.getMin()));
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

    public CompoundNBT writeToTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.putDouble(AgriNBT.Y1, this.getMin());
        tag.putDouble(AgriNBT.Y2, this.getMax());
        tag.putInt(AgriNBT.LEVEL, this.getVolume());
        return tag;
    }

    public void split(double level, Consumer<IrrigationNetworkLayer> consumer) {
        if(level <= this.getMin() || level >= this.getMax()) {
            consumer.accept(this);
        } else {
            double f = (level - this.getMin())/(this.getMax() - this.getMin());
            IrrigationNetworkLayer lower = new IrrigationNetworkLayer(
                    this.getMin(), level, (int) f * this.getVolume());
            IrrigationNetworkLayer upper = new IrrigationNetworkLayer(
                    level, this.getMax(), this.getVolume() - lower.getVolume());
            if(lower.getVolume() > 0) {
                consumer.accept(lower);
            }
            if(upper.getVolume() > 0) {
                consumer.accept(upper);
            }
        }
    }

    public boolean merge(IrrigationNetworkLayer other, Consumer<IrrigationNetworkLayer> consumer) {
        if(this.getMin() != other.getMin() || this.getMax() != other.getMax()) {
            return false;
        }
        consumer.accept(new IrrigationNetworkLayer(this.getMin(), this.getMax(), this.getVolume() + other.getVolume()));
        return true;
    }

    public void distinct(IrrigationNetworkLayer other, Consumer<IrrigationNetworkLayer> consumer) {
        if(this.merge(other, consumer)) {
            // The layers have identical limits, the merging was successful
            return;
        }
        if(this.getMax() <= other.getMin() || this.getMin() >= other.getMax()) {
            // The layers do not overlap, pass them to the consumer
            consumer.accept(this);
            consumer.accept(other);
        } else {
            // The layers overlap
            if(this.getMin() < other.getMin()) {
                if(this.getMax() > other.getMax()) {
                    // this range envelopes the other range
                    this.envelope(other, consumer);
                } else {
                    // overlap from below
                    this.overlapFromBelow(other, consumer);
                }
            } else {
                if(this.getMax() > other.getMax()) {
                    // overlap from above
                    this.overlapFromAbove(other, consumer);
                } else {
                    // the other range envelopes this range
                    other.envelope(this, consumer);
                }
            }
        }
    }

    private void envelope(IrrigationNetworkLayer other, Consumer<IrrigationNetworkLayer> consumer) {
        this.split(other.getMin(), (split1) -> {
            if(split1.getMax() > other.getMax()) {
                split1.split(other.getMax(), (split2) -> {
                    if(split2.getMax() > other.getMax()) {
                        consumer.accept(split2);
                    } else {
                        other.merge(split2, consumer);
                    }
                });
            } else {
                consumer.accept(split1);
            }
        });
    }

    private void overlapFromAbove(IrrigationNetworkLayer other, Consumer<IrrigationNetworkLayer> consumer) {
        other.overlapFromBelow(this, consumer);
    }

    private void overlapFromBelow(IrrigationNetworkLayer other, Consumer<IrrigationNetworkLayer> consumer) {
        this.split(other.getMin(), (split1) -> {
            if(split1.getMax() > other.getMin()) {
                other.split(split1.getMax(), (split2) -> {
                    if(split2.getMax() > this.getMax()) {
                        consumer.accept(split2);
                    } else {
                        split1.merge(split2, consumer);
                    }
                });
            } else {
                consumer.accept(split1);
            }
        });
    }
}
