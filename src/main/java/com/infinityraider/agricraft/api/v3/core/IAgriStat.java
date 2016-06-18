package com.infinityraider.agricraft.api.v3.core;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for representing stats.
 * 
 * It may be preferable to make this an actual class...
 * 
 * @author RlonRyan
 */
public interface IAgriStat {
	
	/**
     * @return if the seed stats are analyzed
     */
    boolean isAnalyzed();
	
	/**
	 * Analyzes the stats.
	 */
	void analyze();
	
    /**
     * @return The growth value of the seed.
     */
    byte getGrowth();

    /**
     * @return The gain value of the seed.
     */
    byte getGain();

    /**
     * @return The strength value of the seed.
     */
    byte getStrength();

    /**
     * @return The maximum growth value a seed of this kind can have.
     */
    byte getMaxGrowth();

    /**
     * @return The maximum gain value a seed of this kind can have.
     */
    byte getMaxGain();

    /**
     * @return The maximum strength value a seed of this kind can have.
     */
    byte getMaxStrength();
	
	/**
	 * Writes the stat to an NBTTagcompound.
	 * 
	 * The major issue here is how to read back from the NBTTag...
	 * 
	 * @param tag The tag to serialize to.
	 */
	void writeToNBT(@Nonnull NBTTagCompound tag);
	
	/**
	 * Writes the stat for display.
	 * 
	 * @param lines The line list to add to.
	 * @return If the writing was successful.
	 */
	boolean addStats(@Nonnull List<String> lines);

}
