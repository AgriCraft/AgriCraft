package com.infinityraider.agricraft.api.v1.stat;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public interface IAgriStatsMap {
    byte getValue(IAgriStat stat);

    boolean writeToNBT(@Nonnull CompoundNBT tag);

    boolean readFromNBT(@Nonnull CompoundNBT tag);

    default boolean equalStats(IAgriStatsMap other) {
        if(this == other) {
            return true;
        }
        for(IAgriStat stat : AgriApi.getStatRegistry().all()) {
            if(this.getValue(stat) != other.getValue(stat)) {
                return false;
            }
        }
        return true;
    }
}
