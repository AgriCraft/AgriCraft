/*
 */
package com.infinityraider.agricraft.api.requirement;

import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.FuzzyStack;

/**
 * Interface for interacting with the GrowthRequirement Builder.
 */
public interface IGrowthReqBuilder {

    IGrowthReqBuilder setMaxBrightness(int maxBrightness);

    IGrowthReqBuilder setMinBrightness(int minBrightness);

    IGrowthReqBuilder addSoil(IAgriSoil soil);
    
    IGrowthReqBuilder setRequiredBlock(FuzzyStack reqBlock);
    
    IGrowthReqBuilder setRequiredType(RequirementType reqType);
    
    IGrowthRequirement build();
	
}
