package com.infinityraider.agricraft.content.world.greenhouse;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

class GreenHouseProperties {
    private BlockPos min;
    private BlockPos max;

    private int interiorCount;
    private int ceilingCount;
    private int ceilingGlassCount;

    public GreenHouseProperties(BlockPos min, BlockPos max, int interiorCount, int ceilingCount, int ceilingGlassCount) {
        this.min = min.immutable();
        this.max = max.immutable();
        this.interiorCount = interiorCount;
        this.ceilingCount = ceilingCount;
        this.ceilingGlassCount = ceilingGlassCount;
    }

    public GreenHouseProperties(CompoundTag tag) {
        this.min = AgriNBT.readBlockPos1(tag);
        this.max = AgriNBT.readBlockPos2(tag);
        this.interiorCount = tag.getInt(AgriNBT.ENTRIES);
        this.ceilingCount = tag.getInt(AgriNBT.CONTENTS);
        this.ceilingGlassCount = tag.getInt(AgriNBT.KEY);
    }

    public BlockPos getMin() {
        return this.min;
    }

    public BlockPos getMax() {
        return this.max;
    }

    public int getInteriorCount() {
        return this.interiorCount;
    }

    public int getCeilingCount() {
        return this.ceilingCount;
    }

    protected void incrementCeilingGlassCount() {
        this.ceilingGlassCount += 1;
    }

    protected void decrementCeilingGlassCount() {
        this.ceilingGlassCount -= 1;
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
        AgriNBT.writeBlockPos1(tag, this.getMax());
        tag.putInt(AgriNBT.ENTRIES, this.getInteriorCount());
        tag.putInt(AgriNBT.CONTENTS, this.getCeilingCount());
        tag.putInt(AgriNBT.KEY, this.getCeilingGlassCount());
        return tag;
    }
}
