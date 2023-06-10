package com.agricraft.agricraft.api.stat;

import com.agricraft.agricraft.api.util.IAgriRegisterable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;

import java.util.function.Consumer;

/**
 * Interface for representing stats. Stats are immutable objects, to aid in overall safety.
 */
public interface IAgriStat extends IAgriRegisterable<IAgriStat> {

	/**
	 * Fetches the minimum valid value for a stat of this type.
	 *
	 * @return The minimum value a seed can have of this stat.
	 */
	int getMin();

	/**
	 * Fetches the maximum valid value for a stat of this type.
	 *
	 * @return The maximum value a seed can have of this stat.
	 */
	int getMax();

	/**
	 * @return true if this is a hidden stat (which does not show up in tooltips)
	 */
	boolean isHidden();

	void writeValueToNBT(CompoundTag tag, byte value);

	int readValueFromNBT(CompoundTag tag);

	/**
	 * Writes the stat for display.
	 *
	 * @param consumer The sink to add the lines to.
	 */
	void addTooltip(@NotNull Consumer<Component> consumer, int value);

	/**
	 * @return an ITextComponent to describe this stat on the client
	 */
	@NotNull
	MutableComponent getDescription();

	/**
	 * @return a color indicative of this stat
	 */
	@NotNull
	AxisAngle4f getColor();

	@Override
	boolean equals(@Nullable Object obj);

	@Override
	int hashCode();

}
