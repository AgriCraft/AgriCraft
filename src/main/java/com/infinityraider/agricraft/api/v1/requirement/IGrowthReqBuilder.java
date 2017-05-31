/*
 */
package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import net.minecraft.util.math.BlockPos;

/**
 * Interface for interacting with the GrowthRequirement Builder.
 */
@Deprecated
public interface IGrowthReqBuilder {

    IGrowthReqBuilder addSoil(IAgriSoil soil);

    IGrowthReqBuilder addCondition(ICondition condition);

    IGrowthReqBuilder setMinLight(int minLight);

    IGrowthReqBuilder setMaxLight(int maxLight);

    default IGrowthReqBuilder addRequiredBlock(FuzzyStack stack, BlockPos pos) {
        return this.addCondition(new BlockCondition(stack, new BlockRange(pos, 0)));
    }

    default IGrowthReqBuilder addRequiredBlock(FuzzyStack stack, int radius) {
        return this.addCondition(new BlockCondition(stack, new BlockRange(BlockPos.ORIGIN, 0)));
    }

    IGrowthRequirement build();

}
