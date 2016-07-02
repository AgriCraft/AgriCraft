/*
 */
package com.infinityraider.agricraft.farming;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatHandler;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.utility.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public class PlantStatsHandler implements IAgriStatHandler {

	public static final String NBT_ANALYZED = "agri_analyzed";
	public static final String NBT_GROWTH = "agri_growth";
	public static final String NBT_GAIN = "agri_gain";
	public static final String NBT_STRENGTH = "agri_strength";
	public static final String NBT_META = "agri_meta";

	@Override
	public boolean isValid(NBTTagCompound tag) {
		return NBTHelper.hasKey(tag, NBT_ANALYZED, NBT_GROWTH, NBT_GAIN, NBT_STRENGTH, NBT_META);
	}

	@Override
	public IAgriStat getStat(NBTTagCompound tag) {
		return new PlantStats(
				tag.getByte(NBT_GAIN),
				tag.getByte(NBT_GROWTH),
				tag.getByte(NBT_STRENGTH),
				tag.getBoolean(NBT_ANALYZED),
				tag.getByte(NBT_META)
		);
	}
	
	@Override
	public boolean setStat(NBTTagCompound tag, IAgriStat stat) {
		tag.setBoolean(NBT_ANALYZED, stat.isAnalyzed());
		tag.setByte(NBT_GAIN, stat.getGain());
		tag.setByte(NBT_GROWTH, stat.getGrowth());
		tag.setByte(NBT_STRENGTH, stat.getStrength());
		tag.setByte(NBT_META, stat.getMeta());
		return true;
	}

}
