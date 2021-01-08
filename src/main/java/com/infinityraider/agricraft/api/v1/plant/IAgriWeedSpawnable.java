package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;

import javax.annotation.Nonnull;

public interface IAgriWeedSpawnable {

    boolean hasWeeds();

    @Nonnull
    IAgriWeed getWeeds();

    @Nonnull
    IAgriGrowthStage getWeedGrowthStage();

    boolean setWeed(@Nonnull IAgriWeed weed, @Nonnull IAgriGrowthStage stage);

    boolean removeWeed();
}
