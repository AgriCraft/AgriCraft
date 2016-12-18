package com.infinityraider.agricraft.api.requirement;

import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Preferred method to use this interface is to read or set data to existing
 * GrowthRequirements, use IGrowthRequirementBuilder to create new ones Creating
 * your own implementation is possible, but should only be used in special
 * cases.
 */
public interface IGrowthRequirement {

    /**
     * @return true, if the given crop has a valid soil below it.
     */
    default boolean hasValidSoil(IBlockAccess world, BlockPos pos) {
        return FuzzyStack.fromBlockState(world.getBlockState(pos.add(0, -1, 0)))
                .filter(soil -> this.getSoils().stream().anyMatch(e -> e.isVarient(soil)))
                .isPresent();
    }

    default boolean hasValidConditions(IBlockAccess world, BlockPos pos) {
        return this.getConditions().stream()
                .sorted((a, b) -> Integer.compare(a.getComplexity(), b.getComplexity()))
                .allMatch(c -> c.isMet(world, pos));
    }

    default boolean hasValidLight(World world, BlockPos pos) {
        BlockPos above = pos.add(0, 1, 0);
        int lvl = Math.max(world.getLightFor(EnumSkyBlock.BLOCK, above), world.getLightFor(EnumSkyBlock.SKY, above));
        return this.getMinLight() <= lvl && lvl < this.getMaxLight();
    }

    default boolean isMet(World world, BlockPos pos) {
        return this.hasValidSoil(world, pos) && this.hasValidLight(world, pos) && this.hasValidConditions(world, pos);
    }

    //Methods to change specific requirements
    //--------------------------------------
    Collection<IAgriSoil> getSoils();

    Collection<ICondition> getConditions();

    int getMinLight();

    int getMaxLight();

    default Optional<FuzzyStack> getConditionStack() {
        return this.getConditions().stream()
                .filter(c -> c instanceof BlockCondition)
                .map(c -> ((BlockCondition) c).getStack())
                .findFirst();
    }

}
