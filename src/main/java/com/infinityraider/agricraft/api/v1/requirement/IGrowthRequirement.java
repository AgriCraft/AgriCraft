package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Interface representing the set of conditions, soils, and light levels required for a given plant
 * to grow.
 * <p>
 * This interface will likely be internalized in a future release, favoring instead the sole usage
 * of the modular {@link IConditions} (and possibly a set of valid soils). This transition is
 * largely due to the requisite functionality of the {@link #hasValidConditions(world, pos) not being achievable in a default method.
 * <p>
 * The preferred method to use this interface is to read or set data to existing GrowthRequirements,
 * use IGrowthRequirementBuilder to create new ones Creating your own implementation is possible,
 * but should only be used in special cases.
 */
public interface IGrowthRequirement {

    /**
     * Retrieves a list of all the soils that are valid for this growth requirement.
     *
     * @return A list containing all the soils valid for this growth requirement.
     */
    @Nonnull
    Collection<IAgriSoil> getSoils();

    /**
     * Retrieves a list of all the conditions that make up this growth requirement.
     *
     * @return A list containing all the conditions that compose this growth requirement.
     */
    @Nonnull
    Collection<ICondition> getConditions();

    /**
     * Determines the minimum (inclusive) light level at which the plant can grow.
     *
     * @return The minimum light level at which the plant can grow.
     */
    int getMinLight();

    /**
     * Determines the maximum (inclusive) light level at which the plant can grow.
     *
     * @return The maximum light level at which the plant can grow.
     */
    int getMaxLight();

    /**
     * Determines if the given location in the given world has a valid soil directly beneath it.
     *
     * @param world The world to test the requirement in.
     * @param pos The position in the world to test the requirement at.
     * @return If the block directly below is one of the valid soils.
     */
    boolean hasValidSoil(IBlockAccess world, BlockPos pos);

    /**
     * Determines if the given location of the world meets all of the conditions of this growth
     * requirement as defined by {@link #getConditions()}.
     * <p>
     * <em>This method does not check the light level! Use {@link #isMet(World, BlockPos)}
     * instead!</em>
     * <p>
     * Since some conditions are expected to be more costly to perform than others, this method
     * relies on the fact that the set of all conditions are internally pre-sorted in order of their
     * complexity as reported by {@link ICondition#getComplexity()}. In this manner, since the
     * evaluation uses the short-circuiting {@link Stream#allMatch()}, the most expensive conditions
     * will only be evaluated when absolutely necessary.
     *
     * @param world The world in which to evaluate the growth requirement in.
     * @param pos The position in the given world at which to evaluate the growth requirement at.
     * @return {@literal true} if all of the conditions for this growth requirement have been met,
     * {@literal false} otherwise.
     */
    boolean hasValidConditions(IBlockAccess world, BlockPos pos);

    /**
     * Determines if the light level at the given position in the given world falls within the range
     * defined by {@link #getMinLight()} (inclusive) and {@link #getMaxLight()} (inclusive).
     *
     * @param world The world in which to test the light level.
     * @param pos The position in the given world at which to test the light level.
     * @return {@literal true} if the light level at the given location falls within the accepted
     * range, {@literal} false otherwise.
     */
    boolean hasValidLight(World world, BlockPos pos);

    /**
     * Determines if this growth requirement is met at the given position in the given world.
     * <p>
     * The growth requirement is met <i>if-and-only-if</i> the following sequence of methods all
     * evaluate to true:
     * <ol>
     * <li>{@link #hasValidSoil(IBlockAccess, BlockPos)}
     * <li>{@link #hasValidLight(World, BlockPos)}
     * <li>{@link #hasValidConditions(IBlockAccess, BlockPos)}
     * </ol>
     *
     * @param world The world in which to test this growth requirement in.
     * @param pos The position in the given world at which to test this growth requirement at.
     * @return {@literal true} <i>if-and-only-if</i> all portions of the growth requirement are met,
     * {@literal} false otherwise.
     */
    boolean isMet(@Nonnull World world, @Nonnull BlockPos pos);

    /**
     * Returns a representative ItemStack for display in the 'condition' slot in the JEI interface.
     * <p>
     * In the future this method will be removed, in the expectation that all conditions should be
     * represented in the JEI menu. However, for now, this method should only be called internally
     * by the AgriCraft JEI interface.
     *
     * @return A representative ItemStack for display in JEI.
     */
    @Deprecated
    @Nonnull
    Optional<FuzzyStack> getConditionStack();

}
