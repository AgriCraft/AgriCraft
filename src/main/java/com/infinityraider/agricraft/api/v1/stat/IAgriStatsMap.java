package com.infinityraider.agricraft.api.v1.stat;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Interface representing the AgriCraft stats of a crop or seed, can be retrieved from any IAgriStatProvider.
 * Examples of IAgriStatProviders are IAgriCrops, AgriSeeds, and IAgriGenomes
 */
public interface IAgriStatsMap {
    /**
     * Fetches the value of a certain stat
     * @param stat the stat
     * @return the value
     */
    int getValue(IAgriStat stat);

    /** @return the value for the gain stat */
    default int getGain() {
        return this.getValue(AgriApi.getStatRegistry().gainStat());
    }

    /** @return the value for the growth stat */
    default int getGrowth() {
        return this.getValue(AgriApi.getStatRegistry().growthStat());
    }

    /** @return the value for the strength stat */
    default int getStrength() {
        return this.getValue(AgriApi.getStatRegistry().strengthStat());
    }

    /** @return the value for the fertility stat */
    default int getFertility() {
        return this.getValue(AgriApi.getStatRegistry().fertilityStat());
    }

    /** @return the value for the resistance stat */
    default int getResistance() {
        return this.getValue(AgriApi.getStatRegistry().resistanceStat());
    }

    /** @return the value for the mutativity stat */
    default int getMutativity() {
        return this.getValue(AgriApi.getStatRegistry().mutativityStat());
    }

    /**
     * @return the sum of all values of all stats
     */
    default int getSum() {
        int sum = 0;
        for(IAgriStat stat : AgriApi.getStatRegistry().all()) {
            sum += this.getValue(stat);
        }
        return sum;
    }

    /**
     * @return the average of all values of all stats
     */
    default double getAverage() {
        return (this.getSum() + 0.0) / AgriApi.getStatRegistry().count();
    }

    /**
     * Serializes the stats map to an NBT tag
     * @param tag the tag to write to
     * @return true if the serialization was successful
     */
    boolean writeToNBT(@Nonnull CompoundNBT tag);


    /**
     * Deserializes the stats map from an NBT tag
     * @param tag the tag to read from
     * @return true if the deserialization was successful
     */
    boolean readFromNBT(@Nonnull CompoundNBT tag);

    /**
     * Checks if the stats are equal
     * @param other another stats map
     * @return true if all stats contain equal values in both stats maps
     */
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

    /**
     * Adds tooltips for the stats
     * @param consumer function to consume the tooltips
     */
    default void addTooltips(@Nonnull Consumer<ITextComponent> consumer) {
        AgriApi.getStatRegistry().stream().forEach(stat -> stat.addTooltip(consumer, this.getValue(stat)));
    }
}
