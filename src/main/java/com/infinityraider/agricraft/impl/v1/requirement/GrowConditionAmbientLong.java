package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.*;

public class GrowConditionAmbientLong extends GrowConditionAbstract {
    private final ToLongFunction<World> fetcher;
    private final LongPredicate predicate;

    public GrowConditionAmbientLong(int strength, RequirementType type, ToLongFunction<World> fetcher, LongPredicate predicate,
                                    List<ITextComponent> tooltips, CacheType cacheType) {
        super(strength, type, tooltips, cacheType);
        this.fetcher = fetcher;
        this.predicate = predicate;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        return this.predicate.test(this.fetcher.applyAsLong(world));
    }
}
