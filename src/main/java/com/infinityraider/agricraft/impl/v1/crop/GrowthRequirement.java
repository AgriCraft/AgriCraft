package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Encodes all requirements a plant needs to mutate and grow Uses the Builder class inside to
 * construct instances.
 */
public final class GrowthRequirement {
    private final Set<IGrowCondition> allConditions;
    private final Map<IGrowCondition, Status> cache;
    private final Set<IGrowCondition> toCheck;

    private GrowthRequirement(Set<IGrowCondition> cachable, Set<IGrowCondition> nonCachable) {
        this.allConditions = new ImmutableSet.Builder<IGrowCondition>().addAll(cachable).addAll(nonCachable).build();
        this.cache = Maps.newIdentityHashMap();
        for(IGrowCondition condition : cachable) {
            this.createStatus(condition);
        }
        this.toCheck = ImmutableSet.copyOf(nonCachable);
    }

    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        // Validate
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);
        return this.cache.values().stream().allMatch(status -> status.isMet(world, pos))
                && this.toCheck.stream().allMatch(condition -> condition.isMet(world, pos));
    }

    public Set<IGrowCondition> getConditions() {
        return this.allConditions;
    }

    private void createStatus(IGrowCondition condition) {
        Status.create(condition, (status) -> this.cache.put(condition, status));
    }

    private static abstract class Status {
        public static Status create(IGrowCondition condition, Consumer<Status> consumer) {
            return new Unknown(condition, consumer);
        }

        private final IGrowCondition condition;

        private Status(IGrowCondition condition, Consumer<Status> consumer) {
            this.condition = condition;
            consumer.accept(this);
        }

        protected IGrowCondition getCondition() {
            return this.condition;
        }

        public abstract boolean isMet(World world, BlockPos pos);

        private static final class Unknown extends Status {
            private final Consumer<Status> consumer;

            private Unknown(IGrowCondition condition, Consumer<Status> consumer) {
                super(condition, consumer);
                this.consumer = consumer;
            }

            @Override
            public boolean isMet(World world, BlockPos pos) {
                Status actual = new Known(this.getCondition(), world, pos, this.consumer);
                return actual.isMet(world, pos);
            }
        }

        private static final class Known extends Status {
            private final boolean status;

            private Known(IGrowCondition condition, World world, BlockPos pos, Consumer<Status> consumer) {
                super(condition, consumer);
                this.status = this.getCondition().isMet(world, pos);
            }

            @Override
            public boolean isMet(World world, BlockPos pos) {
                return this.status;
            }
        }
    }
}
