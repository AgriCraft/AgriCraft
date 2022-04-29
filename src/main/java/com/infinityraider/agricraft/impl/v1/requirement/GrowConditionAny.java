package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.function.UnaryOperator;

public class GrowConditionAny<T> extends GrowConditionBase<T> {
    private static final Map<RequirementType, GrowConditionBase<?>> CACHE = Maps.newEnumMap(RequirementType.class);

    @SuppressWarnings("unchecked")
    public static <T> GrowConditionAny<T> get(RequirementType type) {
        return (GrowConditionAny<T>) CACHE.computeIfAbsent(type, t -> new GrowConditionAny<>(type));
    }

    private GrowConditionAny(RequirementType type) {
        super(type, (str, obj) -> IAgriGrowthResponse.FERTILE, (world, pos) -> null, UnaryOperator.identity(), Collections.emptyList(), 0, CacheType.FULL);
    }

    @Override
    public IAgriGrowthResponse check(IAgriCrop crop, @Nonnull Level world, @Nonnull BlockPos pos, int strength) {
        return IAgriGrowthResponse.FERTILE;
    }
}
