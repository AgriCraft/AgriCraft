/*
 */
package com.infinityraider.agricraft.api.v1.requirement;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Interface representing a condition, which is a subunit of a growth requirement.
 *
 * @see IGrowthRequirement
 */
public interface ICondition {

    /**
     * Determines the computational complexity associated with the evaluation of this condition.
     * <p>
     * The higher the value, the higher the cost associated with invoking
     * {@link #isMet(IBlockAccess, BlockPos)}, and the later in the sequence that this condition is
     * evaluated, as outlined in
     * {@link IGrowthRequirement#hasValidConditions(IBlockAccess, BlockPos)}.
     * <p>
     * <em>It is best to think of this value as the number of blocks that have to be examined as to
     * determine if this condition is met.</em>
     *
     * @return A (hopefully positive) integer representing the approximate cost of evaluating this
     * condition.
     */
    int getComplexity();

    /**
     * Determines if this condition is satisfied at the given position in the given world.
     *
     * @param world The world to test this condition.
     * @param pos The location in the given world at which to test this condition.
     * @return {@literal true} <i>if-and-only-if</i> this condition is met at the provided location
     * in the given world, {@literal false} otherwise.
     */
    boolean isMet(@Nonnull IBlockAccess world, @Nonnull BlockPos pos);

    /**
     * Adds a detailed description of the condition to the provided list.
     *
     * @param consumer a consumer accepting the lines of text of the condition's description.
     */
    void addDescription(@Nonnull Consumer<String> consumer);

}
