package com.agricraft.agricraft.api.crop;

import com.agricraft.agricraft.api.config.CoreConfig;
import net.minecraft.util.RandomSource;

public class AgriGrowthStage {

	private final int stage;
	private final int total;

	public AgriGrowthStage(int stage, int total) {
		this.stage = stage;
		this.total = total;
	}

	public int index() {
		return this.stage;
	}

	public int total() {
		return this.total;
	}

	public boolean isMature() {
		return this.isFinal();
	}

	public boolean isFinal() {
		return this.stage >= this.total - 1;
	}

	public boolean canDropSeed() {
		return this.isFinal() || !CoreConfig.onlyMatureSeedDrops;
	}

	public AgriGrowthStage getNext(AgriCrop crop, RandomSource random) {
		return this.isFinal() ? this : new AgriGrowthStage(this.stage + 1, this.total);
	}

	public AgriGrowthStage getPrevious(AgriCrop crop, RandomSource random) {
		return this.stage <= 0 ? this : new AgriGrowthStage(this.stage - 1, this.total);
	}

	/**
	 * @return the growth as a value between 0 and 1
	 */
	public double growthPercentage() {
		return (this.stage + 1.0) / this.total;
	}

	@Override
	public String toString() {
		return "AgriGrowthStage{" +
				"stage=" + stage +
				", total=" + total +
				'}';
	}

}
