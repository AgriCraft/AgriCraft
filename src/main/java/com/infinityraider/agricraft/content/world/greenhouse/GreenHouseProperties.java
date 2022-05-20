package com.infinityraider.agricraft.content.world.greenhouse;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

class GreenHouseProperties {
    private final BlockPos min;
    private final BlockPos max;

    private final int ceilingCount;
    private int ceilingGlassCount;

    private int gaps;

    public GreenHouseProperties(BlockPos min, BlockPos max, int ceilingCount, int ceilingGlassCount) {
        this.min = min.immutable();
        this.max = max.immutable();
        this.ceilingCount = ceilingCount;
        this.ceilingGlassCount = ceilingGlassCount;
    }

    public GreenHouseProperties(CompoundTag tag) {
        this.min = AgriNBT.readBlockPos1(tag);
        this.max = AgriNBT.readBlockPos2(tag);
        this.ceilingCount = tag.getInt(AgriNBT.CONTENTS);
        this.ceilingGlassCount = tag.getInt(AgriNBT.KEY);
        this.gaps = tag.contains(AgriNBT.REMOVED) ? tag.getInt(AgriNBT.REMOVED) : 0;
    }

    public GreenHouseState getState() {
        if(this.hasGaps()) {
            return GreenHouseState.GAPS;
        }
        if(this.hasSufficientGlass()) {
            return GreenHouseState.COMPLETE;
        } else {
            return GreenHouseState.INSUFFICIENT_GLASS;
        }
    }

    public BlockPos getMin() {
        return this.min;
    }

    public BlockPos getMax() {
        return this.max;
    }

    public int getCeilingCount() {
        return this.ceilingCount;
    }

    public boolean hasGaps() {
        return this.gaps > 0;
    }

    protected void incrementCeilingGlassCount() {
        this.ceilingGlassCount += 1;
    }

    protected void decrementCeilingGlassCount() {
        this.ceilingGlassCount -= 1;
    }

    protected void addGap() {
        this.gaps += 1;
    }

    protected void removeGap() {
        this.gaps = Math.max(this.gaps - 1, 0);
    }

    public boolean hasSufficientGlass() {
        return this.getCeilingGlassFraction() >= AgriCraft.instance.getConfig().greenHouseCeilingGlassFraction();
    }

    public double getCeilingGlassFraction() {
        return (this.getCeilingGlassCount() + 0.0) / this.getCeilingCount();
    }

    public int getCeilingGlassCount() {
        return this.ceilingGlassCount;
    }

    public CompoundTag writeToNBT() {
        CompoundTag tag = new CompoundTag();
        AgriNBT.writeBlockPos1(tag, this.getMin());
        AgriNBT.writeBlockPos2(tag, this.getMax());
        tag.putInt(AgriNBT.CONTENTS, this.getCeilingCount());
        tag.putInt(AgriNBT.KEY, this.getCeilingGlassCount());
        tag.putInt(AgriNBT.REMOVED, this.gaps);
        return tag;
    }
}
