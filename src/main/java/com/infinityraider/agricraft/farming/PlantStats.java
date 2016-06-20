package com.infinityraider.agricraft.farming;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import static com.infinityraider.agricraft.config.AgriCraftConfig.STAT_FORMAT;
import static com.infinityraider.agricraft.config.AgriCraftConfig.cropStatCap;
import java.text.MessageFormat;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.utility.MathHelper;
import com.infinityraider.agricraft.utility.StackHelper;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.reference.Constants;

public class PlantStats implements IAgriStat {
	
	// Moved here since this class is in control of acess to stats.
	public static final String NBT_ANALYZED = "analyzed";
	public static final String NBT_GROWTH = "growth";
	public static final String NBT_GAIN = "gain";
	public static final String NBT_STRENGTH = "strength";
	public static final String NBT_META = "meta";

	private static final byte MAX = (byte) AgriCraftConfig.cropStatCap;
	private static final byte MIN = 1;

	private byte meta;
	private byte growth;
	private byte gain;
	private byte strength;
	private boolean analyzed;

	public PlantStats() {
		this(MIN, MIN, MIN, false);
	}
	
	public PlantStats(ItemStack stack) {
		this(StackHelper.getTag(stack));
	}
	
	public PlantStats(@Nonnull NBTTagCompound tag) {
		this(
				tag.getByte(NBT_GAIN),
				tag.getByte(NBT_GROWTH),
				tag.getByte(NBT_STRENGTH),
				tag.getBoolean(NBT_ANALYZED),
				tag.getByte(NBT_META)
		);
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
	public void analyze() {
		this.analyzed = true;
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
	public byte getMeta() {
		return meta;
	}

	@Override
	public void setMeta(int meta) {
		this.meta = (byte)meta;
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

	@Override
	public void writeToNBT(@Nonnull NBTTagCompound tag) {
		tag.setBoolean(NBT_ANALYZED, analyzed);
		tag.setByte(NBT_GAIN, gain);
		tag.setByte(NBT_GROWTH, growth);
		tag.setByte(NBT_STRENGTH, strength);
		tag.setByte(NBT_META, meta);
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
