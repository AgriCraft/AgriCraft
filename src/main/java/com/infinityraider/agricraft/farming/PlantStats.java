package com.infinityraider.agricraft.farming;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import static com.infinityraider.agricraft.config.AgriCraftConfig.STAT_FORMAT;
import static com.infinityraider.agricraft.config.AgriCraftConfig.cropStatCap;
import java.text.MessageFormat;
import java.util.List;
import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.reference.Constants;

public class PlantStats implements IAgriStat {

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
		this.growth = (byte)MathHelper.inRange(growth, MIN, MAX);
		this.gain = (byte)MathHelper.inRange(gain, MIN, MAX);
		this.strength = (byte)MathHelper.inRange(strength, MIN, MAX);
		this.analyzed = analyzed;
		this.meta = (byte)MathHelper.inRange(meta, 0, Constants.MATURE);
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
