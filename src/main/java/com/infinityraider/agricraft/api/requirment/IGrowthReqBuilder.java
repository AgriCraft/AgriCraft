/*
 */
package com.infinityraider.agricraft.api.requirment;

import com.infinityraider.agricraft.api.requirment.RequirementType;
import com.infinityraider.agricraft.api.util.BlockWithMeta;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirement;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementBuilder;

/**
 * Interface for interacting with the GrowthRequirement Builder.
 */
public interface IGrowthReqBuilder {

    GrowthRequirementBuilder addRequiredBlock(BlockWithMeta block, RequirementType req);

    GrowthRequirement build();

    GrowthRequirementBuilder setMaxBrightness(int maxBrightness);

    GrowthRequirementBuilder setMinBrightness(int minBrightness);

    GrowthRequirementBuilder setSoil(BlockWithMeta soil);
	
}
