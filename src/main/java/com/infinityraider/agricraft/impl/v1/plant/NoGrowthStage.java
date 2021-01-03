package com.infinityraider.agricraft.impl.v1.plant;

import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;

import javax.annotation.Nonnull;

public class NoGrowthStage implements IAgriGrowthStage {
    private static final NoGrowthStage INSTANCE = new NoGrowthStage();

    public static IAgriGrowthStage getInstance() {
        return INSTANCE;
    }

    private final String id;

    private NoGrowthStage() {
        this.id = "none";
    }

    @Override
    public boolean isGrowthStage() {
        return false;
    }

    @Override
    public boolean isMature() {
        return false;
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getNextStage() {
        return this;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }
}
