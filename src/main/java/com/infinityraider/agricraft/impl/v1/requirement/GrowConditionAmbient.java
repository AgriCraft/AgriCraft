package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class GrowConditionAmbient<T> extends GrowConditionAbstract {
    private final Function<World, T> fetcher;
    private final Predicate<T> predicate;

    public GrowConditionAmbient(int strength, RequirementType type, Function<World, T> fetcher, Predicate<T> predicate,
                                List<ITextComponent> tooltips, CacheType cacheType) {
        super(strength, type, tooltips, cacheType);
        this.fetcher = fetcher;
        this.predicate = predicate;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        return this.predicate.test(this.fetcher.apply(world));
    }
}
