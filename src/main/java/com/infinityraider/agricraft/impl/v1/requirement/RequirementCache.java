package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.handler.BlockUpdateHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class RequirementCache {
    private static final RequirementCache EMPTY = new RequirementCache() {
        @Override
        public boolean isMet() {
            return true;
        }
    };

    public static RequirementCache create(IAgriCrop crop) {
        if(crop.hasPlant()) {
            return new Impl(crop);
        }
        return EMPTY;
    }

    public abstract boolean isMet();

    public void flush() {}

    private static class Impl extends RequirementCache {

        private final List<Condition> conditions;

        private Impl(IAgriCrop crop) {
            this.conditions = crop.getPlant().getGrowthRequirement(crop.getGrowthStage()).getGrowConditions().stream()
                    .sorted(Comparator.comparingInt(IAgriGrowCondition::getComplexity))
                    .map(condition -> Condition.create(crop, condition, crop.getStats().getStrength()))
                    .collect(Collectors.toList());
        }

        @Override
        public boolean isMet() {
            return this.conditions.stream().allMatch(Condition::isMet);
        }

        @Override
        public void flush() {
            this.conditions.forEach(Condition::flush);
        }

        private static class Condition {
            private static Condition create(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                switch (condition.getCacheType()) {
                    case NONE: return new Condition(crop, condition, strength);
                    case BLOCK_UPDATE: return new BlockUpdateCache(crop, condition, strength);
                    case FULL: return new FullCache(crop, condition, strength);
                }
                return new Condition(crop, condition, strength);
            }

            private final IAgriCrop crop;
            private final IAgriGrowCondition condition;
            private final int strength;

            private Condition(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                this.crop = crop;
                this.condition = condition;
                this.strength = strength;
            }

            public boolean isMet() {
                if(this.getWorld() == null) {
                    return false;
                }
                return this.getCondition().isMet(this.getWorld(), this.getPos(), this.getStrength());
            }

            public void flush() {}

            protected IAgriCrop getCrop() {
                return this.crop;
            }

            @Nullable
            protected World getWorld() {
                return this.getCrop().getWorld();
            }

            protected BlockPos getPos() {
                return this.getCrop().getPosition();
            }

            protected IAgriGrowCondition getCondition() {
                return this.condition;
            }

            protected int getStrength() {
                return this.strength;
            }

            private static class BlockUpdateCache extends Condition implements BlockUpdateHandler.IListener {
                private boolean status;
                private boolean update;
                private final Set<BlockPos> unloadedPositions;

                private BlockUpdateCache(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                    super(crop, condition, strength);
                    this.status = false;
                    this.update = true;
                    this.unloadedPositions = Sets.newHashSet();
                    this.getCondition().offsetsToCheck().forEach(offset -> this.unloadedPositions.add(this.getPos().add(offset)));
                }

                @Override
                public boolean isMet() {
                    // Do not initialize cache before the world is initialized
                    if(this.getWorld() == null) {
                        return this.status;
                    }
                    this.checkUnloaded();
                    if(this.update) {
                        this.status = super.isMet();
                        this.update = false;
                    }
                    return this.status;
                }

                protected void checkUnloaded() {
                    if(this.getWorld() == null) {
                        return;
                    }
                    this.unloadedPositions.removeIf(pos -> {
                        if(this.getWorld().isAreaLoaded(pos, 1)) {
                            BlockUpdateHandler.getInstance().addListener(this.getWorld(), pos, this);
                            return true;
                        }
                        return false;
                    });
                }

                @Override
                public void flush() {
                    this.getCondition().offsetsToCheck().forEach(pos ->
                            BlockUpdateHandler.getInstance().removeListener(
                                    this.getWorld(), this.getPos().add(pos), this));
                }

                @Override
                public void onBlockUpdate(ServerWorld world, BlockPos pos) {
                    this.update = true;
                }

                @Override
                public void onChunkUnloaded(ServerWorld world, BlockPos pos) {
                    this.unloadedPositions.add(pos);
                }

                @Override
                public void onWorldUnloaded(ServerWorld world, BlockPos pos) {}
            }

            private static class FullCache extends Condition {
                private boolean status;
                private boolean update;

                private FullCache(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                    super(crop, condition, strength);
                    this.status = false;
                    this.update = true;
                }

                @Override
                public boolean isMet() {
                    // Do not initialize cache before the world is initialized
                    if(this.getWorld() == null) {
                        return this.status;
                    }
                    if(this.update) {
                        this.status = super.isMet();
                        this.update = false;
                    }
                    return this.status;
                }
            }
        }
    }
}
