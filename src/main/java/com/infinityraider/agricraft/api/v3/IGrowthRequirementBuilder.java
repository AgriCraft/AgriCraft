package com.infinityraider.agricraft.api.v3;

import com.infinityraider.agricraft.api.v3.util.BlockWithMeta;

public interface IGrowthRequirementBuilder {

    /** Adds a required base block to this GrowthRequirement instance */
    IGrowthRequirementBuilder requiredBlock(BlockWithMeta requiredBlock, RequirementType requiredType, boolean oreDict);

	/** Adds a required nearby block. */
	default IGrowthRequirementBuilder nearbyBlock(BlockWithMeta requiredBlock, int distance, boolean oreDict) {
		return requiredBlock(requiredBlock, RequirementType.NEARBY, oreDict);
	}
	
    /** Sets the required soil */
    IGrowthRequirementBuilder soil(BlockWithMeta block);

    /** Sets the brightness range */
    IGrowthRequirementBuilder brightnessRange(int min, int max);

    /** Builds the GrowthRequirement */
    IGrowthRequirement build();
}
