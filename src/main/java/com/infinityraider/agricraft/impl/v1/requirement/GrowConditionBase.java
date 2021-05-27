package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.*;

public class GrowConditionBase<T> implements IAgriGrowCondition, BiPredicate<Integer, T>, BiFunction<Integer, T, IAgriGrowthResponse> {
    private final RequirementType type;
    private final BiFunction<Integer, T, IAgriGrowthResponse> response;
    private final BiFunction<World, BlockPos, T> getter;
    private final UnaryOperator<BlockPos> offsetter;
    private final Set<BlockPos> offsets;
    private final List<ITextComponent> descriptions;
    private final int complexity;
    private final CacheType cacheType;

    public GrowConditionBase(RequirementType type, BiFunction<Integer, T, IAgriGrowthResponse> response,
                             BiFunction<World, BlockPos, T> getter, UnaryOperator<BlockPos> offsetter,
                             List<ITextComponent> descriptions, int complexity, CacheType cacheType) {
        this.type = type;
        this.response = response;
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
    public IAgriGrowthResponse check(@Nonnull World world, @Nonnull BlockPos pos, int strength) {
        return this.response.apply(strength, this.getter.apply(world, this.offsetter.apply(pos)));
    }

    @Override
    public Set<BlockPos> offsetsToCheck() {
        return this.offsets;
    }

    @Override
    public void notMetDescription(@Nonnull Consumer<ITextComponent> consumer) {
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
    public boolean test(Integer integer, T t) {
        return this.apply(integer, t).isFertile();
    }

    @Override
    public IAgriGrowthResponse apply(Integer integer, T t) {
        return this.response.apply(integer, t);
    }
}
