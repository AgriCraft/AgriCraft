/*
 */
package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.requirment.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.requirment.RequirementType;
import com.infinityraider.agricraft.api.util.BlockWithMeta;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;


public class GrowthRequirementBuilder implements IGrowthReqBuilder {

    private int maxBrightness = 16;
    private int minBrightness = 8;
    private BlockWithMeta soil;
    private final List<Pair<BlockWithMeta, RequirementType>> requiredBlocks;

    public GrowthRequirementBuilder() {
        this.requiredBlocks = new ArrayList<>();
    }

    @Override
    public GrowthRequirementBuilder setMaxBrightness(int maxBrightness) {
        this.maxBrightness = maxBrightness;
        return this;
    }

    @Override
    public GrowthRequirementBuilder setMinBrightness(int minBrightness) {
        this.minBrightness = minBrightness;
        return this;
    }

    @Override
    public GrowthRequirementBuilder setSoil(BlockWithMeta soil) {
        this.soil = soil;
        return this;
    }

    @Override
    public GrowthRequirementBuilder addRequiredBlock(BlockWithMeta block, RequirementType req) {
        this.requiredBlocks.add(Pair.of(block, req));
        return this;
    }

    @Override
    public GrowthRequirement build() {
        return new GrowthRequirement(maxBrightness, minBrightness, soil, requiredBlocks);
    }
	
}
