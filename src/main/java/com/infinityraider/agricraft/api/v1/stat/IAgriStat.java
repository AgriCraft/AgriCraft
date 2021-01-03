package com.infinityraider.agricraft.api.v1.stat;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

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

    void writeValueToNBT(CompoundNBT tag, byte value);

    int readValueFromNBT(CompoundNBT tag);

    /**
     * Writes the stat for display.
     *
     * @param consumer The sink to add the lines to.
     * @return If the writing was successful.
     */
    @Nonnull
    void addTooltip(@Nonnull Consumer<ITextComponent> consumer, int value);

    @Override
    boolean equals(@Nullable Object obj);

    @Override
    int hashCode();
}
