package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GrowConditionAmbient<T> extends GrowConditionBase<T> {
    public GrowConditionAmbient(RequirementType type, BiFunction<Integer, T, IAgriGrowthResponse> response, Function<Level, T> fetcher,
                                List<Component> tooltips, CacheType cacheType) {
        super(type, response, (world, pos) -> fetcher.apply(world), pos -> pos, tooltips, 0, cacheType);
    }
}
