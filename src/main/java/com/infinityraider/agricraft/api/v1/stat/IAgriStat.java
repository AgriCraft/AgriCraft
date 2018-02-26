package com.infinityraider.agricraft.api.v1.stat;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for representing stats. Stats are immutable objects, to aid in overall safety.
 *
 *
 */
public interface IAgriStat {

    /**
     * Retrieves the stat's serialization ID.
     *
     * @return the stat's id.
     */
    @Nonnull
    default String getId() {
        return this.getClass().getCanonicalName();
    }

    /**
     * Determines if the given instance has been analyzed.
     *
     * @return if the seed stats are analyzed
     */
    boolean isAnalyzed();

    /**
     * Fetches the growth value associated with this instance.
     * <p>
     * The returned value should <em>always</em> be less than the value returned by
     * {@link #getMaxGrowth()}.
     *
     * @return The growth value of the seed.
     */
    byte getGrowth();

    /**
     * Fetches the gain value associated with this instance.
     * <p>
     * The returned value should <em>always</em> be less than the value returned by
     * {@link #getMaxGain()}.
     *
     * @return The gain value of the seed.
     */
    byte getGain();

    /**
     * Fetches the strength value associated with this instance.
     * <p>
     * The returned value should <em>always</em> be less than the value returned by
     * {@link #getMaxStrength()}.
     *
     * @return The strength value of the seed.
     */
    byte getStrength();

    /**
     * Fetches the maximum valid growth value for a stat of this type.
     *
     * @return The maximum growth value a seed of this kind can have.
     */
    byte getMaxGrowth();

    /**
     * Fetches the maximum valid gain value for a stat of this type.
     *
     * @return The maximum gain value a seed of this kind can have.
     */
    byte getMaxGain();

    /**
     * Fetches the maximum valid strength value for a stat of this type.
     *
     * @return The maximum strength value a seed of this kind can have.
     */
    byte getMaxStrength();

    /**
     * Sets the analyzed parameter.
     *
     * @param analyzed
     * @return the new stat.
     */
    @Nonnull
    IAgriStat withAnalyzed(boolean analyzed);

    /**
     * Sets the growth stat.
     *
     * @param growth
     * @return the new stat.
     */
    @Nonnull
    IAgriStat withGrowth(int growth);

    /**
     * Sets the gain stat.
     *
     * @param gain
     * @return the new stat.
     */
    @Nonnull
    IAgriStat withGain(int gain);

    /**
     * Sets the strength stat.
     *
     * @param strength
     * @return the new stat.
     */
    @Nonnull
    IAgriStat withStrength(int strength);

    /**
     * Writes the stat to an NBTTagcompound. Make sure to set the stat id value when implementing
     * this.
     *
     * @param tag The tag to serialize to.
     * @return if the transcription was successful.
     */
    @Nonnull
    boolean writeToNBT(@Nonnull NBTTagCompound tag);

    /**
     * Writes the stat for display.
     *
     * @param consumer The sink to add the lines to.
     * @return If the writing was successful.
     */
    @Nonnull
    boolean addStats(@Nonnull Consumer<String> consumer);

    @Override
    public boolean equals(@Nullable Object obj);

    @Override
    public int hashCode();

}
