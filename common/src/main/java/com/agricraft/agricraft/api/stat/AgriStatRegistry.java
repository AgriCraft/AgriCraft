package com.agricraft.agricraft.api.stat;

import com.agricraft.agricraft.api.AgriRegistry;
import com.agricraft.agricraft.common.config.StatsConfig;
import org.joml.Vector3f;

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
		this.gain = new AgriStat("gain", () -> StatsConfig.gainMin, () -> StatsConfig.gainMax, () -> StatsConfig.gainHidden, new Vector3f(0, 0, 1));
		this.growth = new AgriStat("growth",() -> StatsConfig.growthMin, () -> StatsConfig.growthMax, () -> StatsConfig.growthHidden, new Vector3f(0, 1, 0));
		this.strength = new AgriStat("strength", () -> StatsConfig.strengthMin, () -> StatsConfig.strengthMax, () -> StatsConfig.strengthHidden, new Vector3f(1, 0, 0));
		this.resistance = new AgriStat("resistance",() -> StatsConfig.resistanceMin, () -> StatsConfig.resistanceMax, () -> StatsConfig.resistanceHidden, new Vector3f(1, 1, 0));
		this.fertility = new AgriStat("fertility",() -> StatsConfig.fertilityMin, () -> StatsConfig.fertilityMax, () -> StatsConfig.fertilityHidden, new Vector3f(1, 0.5F, 0));
		this.mutativity = new AgriStat("mutativity",() -> StatsConfig.mutativityMin, () -> StatsConfig.mutativityMax, () -> StatsConfig.mutativityHidden, new Vector3f(0, 1, 1));
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

}
