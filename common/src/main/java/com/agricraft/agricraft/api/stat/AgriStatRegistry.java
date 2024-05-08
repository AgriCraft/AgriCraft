package com.agricraft.agricraft.api.stat;

import com.agricraft.agricraft.api.AgriRegistry;
import com.agricraft.agricraft.api.config.StatsConfig;
import com.agricraft.agricraft.common.item.SeedBagItem;

public class AgriStatRegistry extends AgriRegistry<AgriStat> {

	private static AgriStatRegistry INSTANCE;

	public static AgriStatRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AgriStatRegistry();
			INSTANCE.add(INSTANCE.gain);
			INSTANCE.add(INSTANCE.growth);
			INSTANCE.add(INSTANCE.strength);
			INSTANCE.add(INSTANCE.resistance);
			INSTANCE.add(INSTANCE.fertility);
			INSTANCE.add(INSTANCE.mutativity);
		}
		return INSTANCE;
	}

	private final AgriStat gain;
	private final AgriStat growth;
	private final AgriStat strength;
	private final AgriStat resistance;
	private final AgriStat fertility;
	private final AgriStat mutativity;

	private AgriStatRegistry() {
		super();
		//initiate default stats
		this.gain = new AgriStat("gain", () -> StatsConfig.gainMin, () -> StatsConfig.gainMax, () -> StatsConfig.gainHidden, 0xff0000ff);
		this.growth = new AgriStat("growth",() -> StatsConfig.growthMin, () -> StatsConfig.growthMax, () -> StatsConfig.growthHidden, 0xff00ff00);
		this.strength = new AgriStat("strength", () -> StatsConfig.strengthMin, () -> StatsConfig.strengthMax, () -> StatsConfig.strengthHidden, 0xffff0000);
		this.resistance = new AgriStat("resistance",() -> StatsConfig.resistanceMin, () -> StatsConfig.resistanceMax, () -> StatsConfig.resistanceHidden, 0xffffff00);
		this.fertility = new AgriStat("fertility",() -> StatsConfig.fertilityMin, () -> StatsConfig.fertilityMax, () -> StatsConfig.fertilityHidden, 0xffff7f00);
		this.mutativity = new AgriStat("mutativity",() -> StatsConfig.mutativityMin, () -> StatsConfig.mutativityMax, () -> StatsConfig.mutativityHidden, 0xff00ffff);
	}

	public AgriStat gainStat() {
		return this.gain;
	}

	public AgriStat growthStat() {
		return this.growth;
	}

	public AgriStat strengthStat() {
		return this.strength;
	}

	public AgriStat fertilityStat() {
		return this.fertility;
	}

	public AgriStat resistanceStat() {
		return this.resistance;
	}

	public AgriStat mutativityStat() {
		return this.mutativity;
	}

	@Override
	public boolean add(AgriStat object) {
		boolean r = super.add(object);
		if (r) {
			SeedBagItem.addStatSorter(object);
		}
		return r;
	}

}
