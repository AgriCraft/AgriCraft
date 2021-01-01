package com.infinityraider.agricraft.api.v1.requirement;

import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**
 * Interface representing a condition, for a plant to be able to grow, all its conditions must be fulfilled.
 */
public interface IGrowCondition {
    /**
     * @return the type of requirement this relates to
     */
    RequirementType getType();

    /**
     * Determines the computational complexity associated with the evaluation of this condition.
     * <p>
     * The higher the value, the higher the cost associated with invoking
     * {@link #isMet(World, BlockPos)}, and the later in the sequence that this condition will be
     * evaluated
     * <p>
     * <em>It is best to think of this value as the number of blocks that have to be examined as to
     * determine if this condition is met.</em>
     *
     * @return A (hopefully positive) integer representing the approximate cost of evaluating this
     * condition.
     */
    int getComplexity();

    /**
     * Determines the minimum strength stat required to overcome this growth requirement.
     * If a crop has a strength stat that is equal or larger than this value, this growth requirement will be ignored
     *
     * Returning zero or less will result in this requirement always being ignored (which is silly and should not be implemented)
     * Returning more than 10 will result in this requirement never being ignored.
     *
     * @return the minimum strength to ignore this growth requirement
     */
    int strengthToIgnore();

    /**
     * Determines if this condition is satisfied at the given position in the given world.
     *
     * @return {@literal true} <i>if-and-only-if</i> this condition is met at the provided location
     * in the given world, {@literal false} otherwise.
     */
    boolean isMet(@Nonnull World world, @Nonnull BlockPos pos);

    /**
     * @return a set of all block positions, relative to the crop which need to be checked for this condition, can be empty (e.g in case of dimension)
     */
    Set<BlockPos> offsetsToCheck();

    /**
     * Adds a detailed description of the condition to the provided list.
     *
     * @param consumer a consumer accepting the lines of text of the condition's description.
     */
    void addDescription(@Nonnull Consumer<ITextComponent> consumer);

}
