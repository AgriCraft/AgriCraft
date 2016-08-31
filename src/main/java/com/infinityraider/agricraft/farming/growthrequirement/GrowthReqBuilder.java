/*
 */
package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.requirement.RequirementType;
import com.infinityraider.agricraft.api.util.BlockWithMeta;


public class GrowthReqBuilder implements IGrowthReqBuilder {

    private int maxBrightness = 16;
    private int minBrightness = 8;
    private BlockWithMeta soil;
    private BlockWithMeta reqBlock;
    private RequirementType reqType;

    public GrowthReqBuilder() {
    }

    @Override
    public GrowthReqBuilder setMaxBrightness(int maxBrightness) {
        this.maxBrightness = maxBrightness;
        return this;
    }

    @Override
    public GrowthReqBuilder setMinBrightness(int minBrightness) {
        this.minBrightness = minBrightness;
        return this;
    }

    @Override
    public GrowthReqBuilder setSoil(BlockWithMeta soil) {
        this.soil = soil;
        return this;
    }
    
    @Override
    public GrowthReqBuilder setRequiredBlock(BlockWithMeta reqBlock) {
        this.reqBlock = reqBlock;
        return this;
    }
    
    @Override
    public GrowthReqBuilder setRequiredType(RequirementType reqType) {
        this.reqType = reqType;
        return this;
    }

    @Override
    public IGrowthRequirement build() {
        return new GrowthRequirement(maxBrightness, minBrightness, soil, reqBlock, reqType);
    }
	
}
