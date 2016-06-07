package com.infinityraider.agricraft.farming;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriStat;
import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import static com.infinityraider.agricraft.config.AgriCraftConfig.STAT_FORMAT;
import static com.infinityraider.agricraft.config.AgriCraftConfig.cropStatCap;
import com.infinityraider.agricraft.utility.NBTHelper;
import java.text.MessageFormat;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PlantStats implements ISeedStats {
	
	// Moved here since this class is in control of acess to stats.
	public static final String NBT_STAT = "agri_stat";
	
	// Old Stat NBT Tags
	public static final String NBT_ANALYZED = "analyzed";
	public static final String NBT_GROWTH = "growth";
	public static final String NBT_GAIN = "gain";
	public static final String NBT_STRENGTH = "strength";

	private static final short MAX = (short) AgriCraftConfig.cropStatCap;
	private static final short MIN = 1;

	private int statcode;

	public PlantStats() {
		this(1, 1, 1);
	}

	public PlantStats(int growth, int gain, int strength) {
		this(growth, gain, strength, false);
	}

	public PlantStats(int growth, int gain, int strength, boolean analyzed) {
		this(AgriStat.encode(growth, gain, strength, analyzed));
	}

	public PlantStats(@Nonnull ItemStack stack) {
		this(stack.getTagCompound());
	}

	public PlantStats(NBTTagCompound tag) {
		this();
		this.readFromNBT(tag);
	}

	public PlantStats(int statcode) {
		this.statcode = statcode;
	}
	
	public int getStatCode() {
		return statcode;
	}

	@Override
	public short getGrowth() {
		return (short) AgriStat.getGrowth(statcode);
	}

	@Override
	public short getGain() {
		return (short) AgriStat.getGain(statcode);
	}

	@Override
	public short getStrength() {
		return (short) AgriStat.getStrength(statcode);
	}

	@Override
	public short getMaxGrowth() {
		return MAX;
	}

	@Override
	public short getMaxGain() {
		return MAX;
	}

	@Override
	public short getMaxStrength() {
		return MAX;
	}

	public void setGrowth(int growth) {
		this.statcode = AgriStat.setGrowth(statcode, inRange(growth, 0, MAX));
	}

	public void setGain(int gain) {
		this.statcode = AgriStat.setGain(statcode, inRange(gain, 0, MAX));
	}

	public void setStrength(int strength) {
		this.statcode = AgriStat.setGrowth(statcode, inRange(strength, 0, MAX));
	}

	/**
	 * Brings an integer into a specified range.
	 *
	 * @param value The value to bring into the range.
	 * @param min The minimum value, inclusive.
	 * @param max The maximum value, inclusive.
	 * @return The in-bounded value.
	 */
	public static int inRange(int value, int min, int max) {
		return value < min ? min : value > max ? max : value;
	}

	public PlantStats copy() {
		return new PlantStats(statcode);
	}

	public final void readFromNBT(@Nonnull NBTTagCompound tag) {
		if (NBTHelper.hasKey(tag, NBT_GROWTH, NBT_GAIN, NBT_STRENGTH, NBT_ANALYZED)) {
			this.statcode = AgriStat.encode(
					tag.getShort(NBT_GROWTH),
					tag.getShort(NBT_GAIN),
					tag.getShort(NBT_STRENGTH),
					tag.getBoolean(NBT_ANALYZED)
			);
		} else if (NBTHelper.hasKey(tag, NBT_STAT)) {
			this.statcode = tag.getInteger(NBT_STAT);
		}
	}

	public void writeToNBT(@Nonnull NBTTagCompound tag) {
		tag.setInteger(NBT_STAT, statcode);
	}

	@Override
	public boolean isAnalyzed() {
		return AgriStat.getAnalyzed(statcode);
	}

	@Override
	public void setAnalyzed(boolean value) {
		this.statcode = AgriStat.setAnalyzed(statcode, value);
	}

	public boolean addStats(List<String> lines) {
		try {
			lines.add(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.growth"), getGrowth(), cropStatCap));
			lines.add(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.gain"), getGain(), cropStatCap));
			lines.add(MessageFormat.format(STAT_FORMAT, AgriCore.getTranslator().translate("agricraft_tooltip.strength"), getStrength(), cropStatCap));
			return true;
		} catch (IllegalArgumentException e) {
			lines.add("Invalid Stat Format!");
			return false;
		}
	}

}
