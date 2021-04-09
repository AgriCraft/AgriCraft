package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public class GrowConditionWeeds extends GrowConditionAbstract {
    private final BiPredicate<IAgriWeed, IAgriGrowthStage> predicate;

    public GrowConditionWeeds(int strength, BlockPos offset, BiPredicate<IAgriWeed, IAgriGrowthStage> predicate) {
        super(strength, RequirementType.WEEDS, offset, CacheType.BLOCK_UPDATE);
        this.predicate = predicate;
    }

    @Override
    public boolean isMet(@Nonnull World world, @Nonnull BlockPos pos) {
        return AgriApi.getCrop(world, pos).map(crop
                -> this.predicate.test(crop.getWeeds(), crop.getWeedGrowthStage())
        ).orElse(false);
    }
}
