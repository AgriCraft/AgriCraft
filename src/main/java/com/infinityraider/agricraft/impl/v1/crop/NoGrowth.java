package com.infinityraider.agricraft.impl.v1.crop;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;

import javax.annotation.Nonnull;
import java.util.Random;

public class NoGrowth implements IAgriGrowthStage {
    private static final NoGrowth INSTANCE = new NoGrowth();

    public static IAgriGrowthStage getInstance() {
        return INSTANCE;
    }

    private final String id;

    private NoGrowth() {
        this.id = "none";
    }

    @Override
    public boolean isGrowthStage() {
        return false;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isMature() {
        return true;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public boolean canDropSeed() {
        return false;
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getNextStage(IAgriCrop crop, Random random) {
        return this;
    }

    @Nonnull
    @Override
    public IAgriGrowthStage getPreviousStage(IAgriCrop crop, Random random) {
        return this;
    }

    @Override
    public double growthPercentage() {
        return 0;
    }
}
