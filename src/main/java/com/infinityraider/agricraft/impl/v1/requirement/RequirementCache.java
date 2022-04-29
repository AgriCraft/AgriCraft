package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.handler.BlockUpdateHandler;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class RequirementCache {
    private static final RequirementCache EMPTY = new RequirementCache() {
        @Override
        public IAgriGrowthResponse check() {
            return IAgriGrowthResponse.INFERTILE;
        }

        @Override
        public void addTooltip(Consumer<Component> consumer) {
            consumer.accept(AgriToolTips.UNKNOWN);
        }
    };

    public static RequirementCache create(IAgriCrop crop) {
        if(crop.hasPlant()) {
            return new Impl(crop);
        }
        return EMPTY;
    }

    public abstract IAgriGrowthResponse check();

    public abstract void addTooltip(Consumer<Component> consumer);

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
        public IAgriGrowthResponse check() {
            return this.conditions.stream().map(Condition::check).collect(IAgriGrowthResponse.COLLECTOR);
        }

        @Override
        public void addTooltip(Consumer<Component> consumer) {
            IAgriGrowthResponse response = this.check();
            if(response.isFertile()) {
                consumer.accept(AgriToolTips.FERTILE);
            } else {
                consumer.accept(AgriToolTips.NOT_FERTILE);
                this.conditions.stream().filter(condition -> !condition.check().isFertile()).forEach(condition -> condition.addTooltip(consumer));
            }
        }

        @Override
        public void flush() {
            this.conditions.forEach(Condition::flush);
        }

        private static class Condition {
            private static Condition create(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                return switch (condition.getCacheType()) {
                    case NONE -> new Condition(crop, condition, strength);
                    case BLOCK_UPDATE -> new BlockUpdateCache(crop, condition, strength);
                    case FULL -> new FullCache(crop, condition, strength);
                };
            }

            private final IAgriCrop crop;
            private final IAgriGrowCondition condition;
            private final int strength;

            private Condition(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                this.crop = crop;
                this.condition = condition;
                this.strength = strength;
            }

            public IAgriGrowthResponse check() {
                if(this.getWorld() == null) {
                    return IAgriGrowthResponse.INFERTILE;
                }
                return this.getCondition().check(this.crop, this.getWorld(), this.getPos(), this.getStrength());
            }

            public void addTooltip(Consumer<Component> consumer) {
                this.getCondition().notMetDescription(tooltip -> consumer.accept(new TextComponent(" - ").append(tooltip)));
            }

            public void flush() {}

            protected IAgriCrop getCrop() {
                return this.crop;
            }

            @Nullable
            protected Level getWorld() {
                return this.getCrop().world();
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
                private IAgriGrowthResponse status;
                private boolean update;
                private final Set<BlockPos> unloadedPositions;

                private BlockUpdateCache(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                    super(crop, condition, strength);
                    this.status = IAgriGrowthResponse.INFERTILE;
                    this.update = true;
                    this.unloadedPositions = Sets.newHashSet();
                    this.getCondition().offsetsToCheck().forEach(offset -> this.unloadedPositions.add(this.getPos().offset(offset)));
                }

                @Override
                public IAgriGrowthResponse check() {
                    // Do not initialize cache before the world is initialized
                    if(this.getWorld() == null) {
                        return this.status;
                    }
                    this.checkUnloaded();
                    if(this.update) {
                        this.status = super.check();
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
                                    this.getWorld(), this.getPos().offset(pos), this));
                }

                @Override
                public void onBlockUpdate(ServerLevel world, BlockPos pos) {
                    this.update = true;
                }

                @Override
                public void onChunkUnloaded(ServerLevel world, BlockPos pos) {
                    this.unloadedPositions.add(pos);
                }

                @Override
                public void onWorldUnloaded(ServerLevel world, BlockPos pos) {}
            }

            private static class FullCache extends Condition {
                private IAgriGrowthResponse status;
                private boolean update;

                private FullCache(IAgriCrop crop, IAgriGrowCondition condition, int strength) {
                    super(crop, condition, strength);
                    this.status = IAgriGrowthResponse.INFERTILE;
                    this.update = true;
                }

                @Override
                public IAgriGrowthResponse check() {
                    // Do not initialize cache before the world is initialized
                    if(this.getWorld() == null) {
                        return this.status;
                    }
                    if(this.update) {
                        this.status = super.check();
                        this.update = false;
                    }
                    return this.status;
                }
            }
        }
    }
}
