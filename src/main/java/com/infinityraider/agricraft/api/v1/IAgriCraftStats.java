package com.infinityraider.agricraft.api.v1;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;

/**
 * To be deprecated upon switch to using condensed stat codes.
 * 
 * But having the stats as an objects presents a nice way to handle them.
 * 
 * @author RlonRyan
 */
public interface IAgriCraftStats {
	
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
    short getGrowth();

    /**
     * @return The gain value of the seed.
     */
    short getGain();

    /**
     * @return The strength value of the seed.
     */
    short getStrength();

    /**
     * @return The maximum growth value a seed of this kind can have.
     */
    short getMaxGrowth();

    /**
     * @return The maximum gain value a seed of this kind can have.
     */
    short getMaxGain();

    /**
     * @return The maximum strength value a seed of this kind can have.
     */
    short getMaxStrength();
	
	/**
	 * Writes the stat to an NBTTagcompound.
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
	
	/**
	 * Duplicates the stats.
	 * @return  A copy of the stat object.
	 */
	IAgriCraftStats copy();

}
