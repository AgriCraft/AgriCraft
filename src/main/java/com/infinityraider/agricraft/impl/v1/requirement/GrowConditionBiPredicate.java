package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.*;

public class GrowConditionBiPredicate<T> implements IAgriGrowCondition, BiPredicate<T, Integer> {
    private final RequirementType type;
    private final BiPredicate<T, Integer> predicate;
    private final BiFunction<World, BlockPos, T> getter;
    private final UnaryOperator<BlockPos> offsetter;
    private final Set<BlockPos> offsets;
    private final Set<ITextComponent> descriptions;
    private final int complexity;
    private final CacheType cacheType;

    public GrowConditionBiPredicate(RequirementType type, BiPredicate<T, Integer> predicate, BiFunction<World, BlockPos, T> getter,
                                    UnaryOperator<BlockPos> offsetter, Set<ITextComponent> descriptions, int complexity, CacheType cacheType) {
        this.type = type;
        this.predicate = predicate;
        this.getter = getter;
        this.offsetter = offsetter;
        this.offsets = ImmutableSet.of(offsetter.apply(new BlockPos(0, 0, 0)));
        this.descriptions = descriptions;
        this.complexity = complexity;
        this.cacheType = cacheType;
    }

    @Override
    public RequirementType getType() {
        return this.type;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos, int strength) {
        return this.predicate.test(this.getter.apply(world, this.offsetter.apply(pos)), strength);
    }

    @Override
    public Set<BlockPos> offsetsToCheck() {
        return this.offsets;
    }

    @Override
    public void addDescription(@Nonnull Consumer<ITextComponent> consumer) {
        this.descriptions.forEach(consumer);
    }

    @Override
    public int getComplexity() {
        return this.complexity;
    }

    @Override
    public CacheType getCacheType() {
        return this.cacheType;
    }

    @Override
    public boolean test(T t, Integer integer) {
        return this.predicate.test(t, integer);
    }
}
