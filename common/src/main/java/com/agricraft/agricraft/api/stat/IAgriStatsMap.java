package com.agricraft.agricraft.api.stat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Interface representing the AgriCraft stats of a crop or seed, can be retrieved from any IAgriStatProvider.
 * Examples of IAgriStatProviders are IAgriCrops, AgriSeeds, and IAgriGenomes
 */
public interface IAgriStatsMap {

	/**
	 * Fetches the value of a certain stat
	 *
	 * @param stat the stat
	 * @return the value
	 */
	int getValue(IAgriStat stat);

	/**
	 * @return the value for the gain stat
	 */
	default int getGain() {
		return this.getValue(IAgriStatRegistry.getInstance().gainStat());
	}

	/**
	 * @return the value for the growth stat
	 */
	default int getGrowth() {
		return this.getValue(IAgriStatRegistry.getInstance().growthStat());
	}

	/**
	 * @return the value for the strength stat
	 */
	default int getStrength() {
		return this.getValue(IAgriStatRegistry.getInstance().strengthStat());
	}

	/**
	 * @return the value for the fertility stat
	 */
	default int getFertility() {
		return this.getValue(IAgriStatRegistry.getInstance().fertilityStat());
	}

	/**
	 * @return the value for the resistance stat
	 */
	default int getResistance() {
		return this.getValue(IAgriStatRegistry.getInstance().resistanceStat());
	}

	/**
	 * @return the value for the mutativity stat
	 */
	default int getMutativity() {
		return this.getValue(IAgriStatRegistry.getInstance().mutativityStat());
	}

	/**
	 * @return the sum of all values of all stats
	 */
	default int getSum() {
		return IAgriStatRegistry.getInstance().stream().mapToInt(this::getValue).sum();
	}

	/**
	 * @return the average of all values of all stats
	 */
	default double getAverage() {
		return (this.getSum() + 0.0) / IAgriStatRegistry.getInstance().count();
	}

	/**
	 * Serializes the stats map to an NBT tag
	 *
	 * @param tag the tag to write to
	 * @return true if the serialization was successful
	 */
	boolean writeToNBT(@NotNull CompoundTag tag);


	/**
	 * Deserializes the stats map from an NBT tag
	 *
	 * @param tag the tag to read from
	 * @return true if the deserialization was successful
	 */
	boolean readFromNBT(@NotNull CompoundTag tag);

	/**
	 * Checks if the stats are equal
	 *
	 * @param other another stats map
	 * @return true if all stats contain equal values in both stats maps
	 */
	default boolean equalStats(IAgriStatsMap other) {
		return this == other
				|| IAgriStatRegistry.getInstance().stream().allMatch(stat -> this.getValue(stat) == other.getValue(stat));
	}

	/**
	 * Adds tooltips for the stats
	 *
	 * @param consumer function to consume the tooltips
	 */
	default void addTooltips(@NotNull Consumer<Component> consumer) {
		IAgriStatRegistry.getInstance().stream().forEach(stat -> stat.addTooltip(consumer, this.getValue(stat)));
	}

}
