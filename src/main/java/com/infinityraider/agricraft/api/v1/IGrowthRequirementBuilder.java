package com.infinityraider.agricraft.api.v1;

public interface IGrowthRequirementBuilder {

    /** Adds a required base block to this GrowthRequirement instance */
    IGrowthRequirementBuilder requiredBlock(BlockWithMeta requiredBlock, RequirementType requiredType, boolean oreDict);

    /** Sets the required soil */
    IGrowthRequirementBuilder soil(BlockWithMeta block);

    /** Sets the brightness range */
    IGrowthRequirementBuilder brightnessRange(int min, int max);

    /** Builds the GrowthRequirement */
    IGrowthRequirement build();
}
