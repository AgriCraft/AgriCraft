package com.infinityraider.agricraft.impl.v1.crop;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IncrementalGrowthLogic {
    private static final Map<Integer, Map<Integer, IncrementalGrowthLogic>> CACHE = Maps.newHashMap();

    public static IncrementalGrowthLogic getOrCreate(int stages, int harvestIndex) {
        Map<Integer, IncrementalGrowthLogic> harvestMap = CACHE.computeIfAbsent(stages, k -> Maps.newHashMap());
        return harvestMap.computeIfAbsent(harvestIndex, (h) -> new IncrementalGrowthLogic(stages, h));
    }

    private final List<IAgriGrowthStage> stages;
    private final int harvestIndex;

    private IncrementalGrowthLogic(int stages, int harvestIndex) {
        Preconditions.checkArgument(stages > 0, "The number of stages must be larger than 0");
        Preconditions.checkArgument(harvestIndex >= 0, "The harvest index can not be negative");
        Preconditions.checkArgument(harvestIndex < stages, "The harvest index must be smaller than the number of stages");
        this.stages = this.generateStages(stages);
        this.harvestIndex = harvestIndex;
    }

    public int getStageCount() {
        return this.stages.size();
    }

    public IAgriGrowthStage getStage(int index) {
        return this.stages.get(Math.max(0, Math.min(this.getStageCount() - 1, index)));
    }

    public IAgriGrowthStage initial() {
        return this.stages.get(0);
    }

    public IAgriGrowthStage harvest() {
        return this.getStage(this.harvestIndex);
    }

    public Collection<IAgriGrowthStage> all() {
        return this.stages;
    }

    protected String getIdPrefix() {
        return  "incremental_" + this.getStageCount() + "("+ this.harvestIndex + ")_";
    }

    protected List<IAgriGrowthStage> generateStages(int amount) {
        ImmutableList.Builder<IAgriGrowthStage> builder = new ImmutableList.Builder<>();
        for(int i = 0; i < amount; i++) {
            builder.add(new Stage(this, i, i == amount - 1));
        }
        return builder.build();
    }

    private static class Stage implements IAgriGrowthStage {
        private final IncrementalGrowthLogic logic;
        private final int stage;
        private final boolean mature;
        private final String id;

        private Stage(IncrementalGrowthLogic logic, int stage, boolean mature) {
            this.logic = logic;
            this.stage = stage;
            this.mature = mature;
            this.id = this.logic.getIdPrefix() + this.getStage();
        }

        public final IncrementalGrowthLogic getLogic() {
            return this.logic;
        }

        public final int getStage() {
            return this.stage;
        }

        @Override
        public boolean isMature() {
            return this.mature;
        }

        @Override
        public boolean canDropSeed() {
            return false;
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getNextStage() {
            return this.getLogic().getStage(this.getStage() + 1);
        }

        @Override
        public double growthPercentage() {
            return (this.getStage() + 1.0) / ((double) this.getLogic().getStageCount());
        }

        @Nonnull
        @Override
        public String getId() {
            return this.id;
        }
    }
}
