package com.infinityraider.agricraft.api.v1.stat;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface IAgriStatsMap {
    int getValue(IAgriStat stat);

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

    default void addTooltips(@Nonnull Consumer<ITextComponent> consumer) {
        AgriApi.getStatRegistry().stream().forEach(stat -> stat.addTooltip(consumer, this.getValue(stat)));
    }
}
