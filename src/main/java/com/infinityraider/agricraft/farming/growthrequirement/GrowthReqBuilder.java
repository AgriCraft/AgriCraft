/*
 */
package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.requirement.IGrowthReqBuilder;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.requirement.RequirementType;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.BlockWithMeta;
import java.util.ArrayList;
import java.util.List;


public class GrowthReqBuilder implements IGrowthReqBuilder {

    private int maxBrightness = 16;
    private int minBrightness = 8;
    private final List<IAgriSoil> soils;
    private BlockWithMeta reqBlock;
    private RequirementType reqType;

    public GrowthReqBuilder() {
        this.soils = new ArrayList<>();
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
    public GrowthReqBuilder addSoil(IAgriSoil soil) {
        this.soils.add(soil);
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
        return new GrowthRequirement(maxBrightness, minBrightness, soils, reqBlock, reqType);
    }
	
}
