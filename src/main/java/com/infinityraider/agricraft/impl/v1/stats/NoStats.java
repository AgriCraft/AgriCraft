package com.infinityraider.agricraft.impl.v1.stats;

import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public class NoStats implements IAgriStatsMap {
    private static final NoStats INSTANCE = new NoStats();

    public static IAgriStatsMap getInstance() {
        return INSTANCE;
    }

    private NoStats() {}

    @Override
    public int getValue(IAgriStat stat) {
        return 0;
    }

    @Override
    public boolean writeToNBT(@Nonnull CompoundTag tag) {
        return false;
    }

    @Override
    public boolean readFromNBT(@Nonnull CompoundTag tag) {
        return false;
    }
}
