package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.crop.IAgriGrowthStage;
import org.jetbrains.annotations.NotNull;

public interface IAgriWeedSpawnable {

	boolean hasWeeds();

	@NotNull
	IAgriWeed getWeeds();

	@NotNull
	IAgriGrowthStage getWeedGrowthStage();

	default double getWeedGrowthPercentage() {
		return this.getWeedGrowthStage().growthPercentage();
	}

	boolean setWeed(@NotNull IAgriWeed weed, @NotNull IAgriGrowthStage stage);

	boolean removeWeed();

}
