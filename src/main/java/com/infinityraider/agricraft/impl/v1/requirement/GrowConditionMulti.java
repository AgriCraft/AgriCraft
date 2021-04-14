package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class GrowConditionMulti<T> extends GrowConditionBase<Stream<T>> {
    private final Set<BlockPos> offsets;

    public GrowConditionMulti(RequirementType type, BiPredicate<Integer, T> predicate, BiFunction<World, BlockPos, Stream<T>> getter,
                              int min, int max, UnaryOperator<BlockPos> offsetter, List<ITextComponent> descriptions, int complexity, CacheType cacheType) {
        this(type, predicate, getter, min, max, offsetter, descriptions, complexity, cacheType, ImmutableSet.of(new BlockPos(0, 0, 0)));
    }

    public GrowConditionMulti(RequirementType type, BiPredicate<Integer, T> predicate, BiFunction<World, BlockPos, Stream<T>> getter,
                              int min, int max, UnaryOperator<BlockPos> offsetter, List<ITextComponent> descriptions, int complexity,
                              CacheType cacheType, Set<BlockPos> positions) {
        super(type, predicate(predicate, min, max), getter, offsetter, descriptions, complexity, cacheType);
        this.offsets = ImmutableSet.copyOf(positions);
    }

    @Override
    public Set<BlockPos> offsetsToCheck() {
        return this.offsets;
    }

    private static <T> BiPredicate<Integer, Stream<T>>  predicate(BiPredicate<Integer, T> predicate, int min, int max) {
        return  (str, stream) -> {
            long count = stream.filter(t -> predicate.test(str, t)).count();
            return count >= min && count <= max;
        };
    }
}
