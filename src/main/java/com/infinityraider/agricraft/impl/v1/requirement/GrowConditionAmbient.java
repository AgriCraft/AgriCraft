package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class GrowConditionAmbient<T> extends GrowConditionSingle<T> {
    public GrowConditionAmbient(RequirementType type, BiPredicate<Integer, T> predicate, Function<World, T> fetcher,
                                List<ITextComponent> tooltips, CacheType cacheType) {
        super(type, predicate, (world, pos) -> fetcher.apply(world), pos -> pos, tooltips, 0, cacheType);
    }
}
