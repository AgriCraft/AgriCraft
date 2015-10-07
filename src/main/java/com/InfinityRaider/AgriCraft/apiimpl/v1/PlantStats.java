
package com.InfinityRaider.AgriCraft.apiimpl.v1;

import com.InfinityRaider.AgriCraft.api.v1.ITrowel;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PlantStats {

	public static final short MAX = (short) ConfigurationHandler.cropStatCap;
	public static final short MIN = 1;
	public static final PlantStats DEFAULT = new PlantStats(MIN, MIN, MIN);

	public final short growth;
	public final short gain;
	public final short strength;

	public final boolean isAnalyzed;

	public PlantStats(NBTTagCompound tag) {
		this(tag.getShort(Names.NBT.growth), tag.getShort(Names.NBT.gain), tag.getShort(Names.NBT.strength), tag.getBoolean(Names.NBT.analyzed));
	}

	public PlantStats(int growth, int gain, int strength) {
		this(growth, gain, strength, false);
	}
	
	public PlantStats(int growth, int gain, int strength, boolean isAnalyzed) {
		this.growth = moveIntoBounds(growth);
		this.gain = moveIntoBounds(gain);
		this.strength = moveIntoBounds(strength);
		this.isAnalyzed = isAnalyzed;
	}

	private final short moveIntoBounds(int stat) {
		int lowerLimit = Math.max(MIN, stat);
		return (short) Math.min(MAX, lowerLimit);
	}

	public PlantStats copy() {
		return new PlantStats(this.growth, this.gain, this.strength);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setShort(Names.NBT.growth, growth);
		tag.setShort(Names.NBT.gain, gain);
		tag.setShort(Names.NBT.strength, strength);
		tag.setBoolean(Names.NBT.analyzed, isAnalyzed);
		return tag;
	}
}
