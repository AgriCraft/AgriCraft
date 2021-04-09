package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public class GrowConditionBoolean extends GrowConditionAbstract {
    private final BlockPos offset;
    private final BiPredicate<World, BlockPos> fetcher;

    public GrowConditionBoolean(int strength, RequirementType type, BlockPos offset, BiPredicate<World, BlockPos> fetcher, CacheType cacheType) {
        super(strength, type, offset, cacheType);
        this.offset = offset;
        this.fetcher = fetcher;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        return this.fetcher.test(world, pos.add(this.offset));
    }
}
