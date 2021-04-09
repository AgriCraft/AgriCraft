package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class GrowConditionSingle<T> extends GrowConditionAbstract {
    private final BlockPos offset;
    private final BiFunction<World, BlockPos, T> fetcher;
    private final Predicate<T> predicate;

    public GrowConditionSingle(int strength, RequirementType type, BlockPos offset, BiFunction<World, BlockPos, T> fetcher,
                               Predicate<T> predicate, CacheType cacheType) {
        super(strength, type, offset, cacheType);
        this.offset = offset;
        this.fetcher = fetcher;
        this.predicate = predicate;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        return this.predicate.test(this.fetcher.apply(world, pos.add(this.offset)));
    }
}
