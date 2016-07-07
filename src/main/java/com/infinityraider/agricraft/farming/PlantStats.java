package com.infinityraider.agricraft.farming;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import static com.infinityraider.agricraft.config.AgriCraftConfig.STAT_FORMAT;
import static com.infinityraider.agricraft.config.AgriCraftConfig.cropStatCap;
import java.text.MessageFormat;
import java.util.List;
import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;

public class PlantStats implements IAgriStat, IAgriAdapter<IAgriStat> {

	public static final String NBT_ANALYZED = "agri_analyzed";
	public static final String NBT_GROWTH = "agri_growth";
	public static final String NBT_GAIN = "agri_gain";
	public static final String NBT_STRENGTH = "agri_strength";
	public static final String NBT_META = "agri_meta";

	private static final byte MAX = (byte) AgriCraftConfig.cropStatCap;
	private static final byte MIN = 1;

	private final byte meta;
	private final byte growth;
	private final byte gain;
	private final byte strength;
	private final boolean analyzed;

	public PlantStats() {
		this(MIN, MIN, MIN, false);
	}

	public PlantStats(int growth, int gain, int strength) {
		this(growth, gain, strength, false, 0);
	}

	public PlantStats(int growth, int gain, int strength, int meta) {
		this(growth, gain, strength, false, meta);
	}

	public PlantStats(int growth, int gain, int strength, boolean analyzed) {
		this(growth, gain, strength, analyzed, 0);
	}

	public PlantStats(int growth, int gain, int strength, boolean analyzed, int meta) {
		this.growth = (byte) MathHelper.inRange(growth, MIN, MAX);
		this.gain = (byte) MathHelper.inRange(gain, MIN, MAX);
		this.strength = (byte) MathHelper.inRange(strength, MIN, MAX);
		this.analyzed = analyzed;
		this.meta = (byte) MathHelper.inRange(meta, 0, Constants.MATURE);
	}

	@Override
	public boolean isAnalyzed() {
		return this.analyzed;
	}

	@Override
	public byte getMeta() {
		return meta;
	}

	@Override
	public byte getGrowth() {
		return growth;
	}

	@Override
	public byte getGain() {
		return gain;
	}

	@Override
	public byte getStrength() {
		return strength;
	}

	@Override
	public IAgriStat withAnalyzed(boolean analyzed) {
		return new PlantStats(growth, gain, strength, analyzed, meta);
	}

	@Override
	public IAgriStat withMeta(int meta) {
		return new PlantStats(growth, gain, strength, analyzed, meta);
	}

	@Override
	public IAgriStat withGrowth(int growth) {
		return new PlantStats(growth, gain, strength, analyzed, meta);
	}

	@Override
	public IAgriStat withGain(int gain) {
		return new PlantStats(growth, gain, strength, analyzed, meta);
	}

	@Override
	public IAgriStat withStrength(int strength) {
		return new PlantStats(growth, gain, strength, analyzed, meta);
	}

	@Override
	public byte getMaxGrowth() {
		return MAX;
	}

	@Override
	public byte getMaxGain() {
		return MAX;
	}

	@Override
	public byte getMaxStrength() {
		return MAX;
	}

	public boolean writeToNBT(NBTTagCompound tag) {
		tag.setBoolean(NBT_ANALYZED, analyzed);
		tag.setByte(NBT_GAIN, gain);
		tag.setByte(NBT_GROWTH, growth);
		tag.setByte(NBT_STRENGTH, strength);
		tag.setByte(NBT_META, meta);
		return true;
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

	@Override
	public boolean accepts(Object obj) {
		NBTTagCompound tag = NBTHelper.asTag(obj);
		return tag != null && NBTHelper.hasKey(tag, NBT_ANALYZED, NBT_GROWTH, NBT_GAIN, NBT_STRENGTH, NBT_META);
	}

	@Override
	public IAgriStat getValue(Object obj) {
		NBTTagCompound tag = NBTHelper.asTag(obj);
		return tag == null ? null : new PlantStats(
				tag.getByte(NBT_GAIN),
				tag.getByte(NBT_GROWTH),
				tag.getByte(NBT_STRENGTH),
				tag.getBoolean(NBT_ANALYZED),
				tag.getByte(NBT_META)
		);
	}

}
