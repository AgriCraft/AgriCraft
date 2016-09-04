package com.infinityraider.agricraft.api.requirement;

import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Preferred method to use this interface is to read or set data to existing
 * GrowthRequirements, use IGrowthRequirementBuilder to create new ones Creating
 * your own implementation is possible, but should only be used in special
 * cases.
 */
public interface IGrowthRequirement {

    //Methods to check if a seed can grow
    //-----------------------------------
    /**
     * @return true, if all the requirements are met (position is the position
     * of the crop)
     */
    boolean canGrow(World world, BlockPos pos);

    //public boolean canPlant(World world, int x, int y, int z);
    /**
     * @return true, if the correct base block is present (position is the
     * position of the crop)
     */
    boolean isBaseBlockPresent(World world, BlockPos pos);

    /**
     * @return true, if the given block is a valid soil (position is the
     * position of the soil)
     */
    default boolean isValidSoil(World world, BlockPos pos) {
        return this.isValidSoil(new FuzzyStack(world.getBlockState(pos)));
    }

    /**
     * @return true, if the given block is a valid soil
     */
    default boolean isValidSoil(FuzzyStack soil) {
        return this.getSoils().isEmpty() || this.getSoils().stream()
                .anyMatch(s -> s.isVarient(soil));
    }

    //Methods to change specific requirements
    //--------------------------------------
    Collection<IAgriSoil> getSoils();

    int getMinBrightness();

    int getMaxBrightness();

    Optional<FuzzyStack> getRequiredBlock();

    Optional<RequirementType> getRequiredType();

}
