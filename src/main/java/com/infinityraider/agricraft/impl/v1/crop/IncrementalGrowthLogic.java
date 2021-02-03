package com.infinityraider.agricraft.impl.v1.crop;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class IncrementalGrowthLogic {
    private static final Map<Integer, List<IAgriGrowthStage>> CACHE = Maps.newHashMap();

    public static List<IAgriGrowthStage> getOrGenerateStages(int amount) {
        return CACHE.computeIfAbsent(amount, (value) -> {
            ImmutableList.Builder<IAgriGrowthStage> builder = new ImmutableList.Builder<>();
            for(int i = 0; i < value; i++) {
                builder.add(new Stage(i, value));
            }
            return builder.build();
        });
    }

    public static int getGrowthIndex(IAgriGrowthStage stage) {
        if(stage instanceof Stage) {
            return ((Stage) stage).stage;
        }
        return -1;
    }

    private IncrementalGrowthLogic() {}

    private static class Stage implements IAgriGrowthStage {
        private final int stage;
        private final int total;
        private final double percentage;
        private final boolean mature;
        private final String id;

        private IAgriGrowthStage next;
        private IAgriGrowthStage prev;

        private Stage(int stage, int total) {
            this.stage = stage;
            this.total = total;
            this.percentage = (this.stage + 1.0) / ((double) this.total);
            this.mature = stage >= (total - 1);
            this.id = "agri_incremental_" + (this.stage + 1) + "/" +this.total;
        }

        @Override
        public boolean isMature() {
            return this.isFinal();
        }

        @Override
        public boolean isFinal() {
            return this.mature;
        }

        @Override
        public boolean canDropSeed() {
            return this.isFinal() || !AgriCraft.instance.getConfig().onlyMatureSeedDrops();
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getNextStage(IAgriCrop crop, Random random) {
            if(this.next == null) {
                this.next = this.isFinal() ? this : CACHE.get(this.total).get(this.stage + 1);
            }
            return this.next;
        }

        @Nonnull
        @Override
        public IAgriGrowthStage getPreviousStage(IAgriCrop crop, Random random) {
            if(this.prev == null) {
                this.prev = this.stage <= 0 ? this : CACHE.get(this.total).get(this.stage - 1);
            }
            return this.prev;
        }

        @Override
        public double growthPercentage() {
            return  this.percentage;
        }

        @Nonnull
        @Override
        public String getId() {
            return this.id;
        }
    }
}
