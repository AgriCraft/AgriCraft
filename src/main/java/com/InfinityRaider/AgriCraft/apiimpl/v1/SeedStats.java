package com.InfinityRaider.AgriCraft.apiimpl.v1;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;

public class SeedStats implements ISeedStats {

	private final int growth;
	private final int gain;
	private final int strength;
	
	public SeedStats(int growth, int gain, int strength) {
		this.growth = growth;
		this.gain = gain;
		this.strength = strength;
	}

	@Override
	public int getGrowth() {
		return growth;
	}

	@Override
	public int getGain() {
		return gain;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getMaxGrowth() {
		return 10;
	}

	@Override
	public int getMaxGain() {
		return 10;
	}

	@Override
	public int getMaxStrength() {
		return 10;
	}

}
