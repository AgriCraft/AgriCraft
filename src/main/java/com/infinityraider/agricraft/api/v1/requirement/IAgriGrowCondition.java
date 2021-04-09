package com.infinityraider.agricraft.api.v1.requirement;

import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**
 * Interface representing a single grow condition,
 * for a plant to be able to grow, all its grow conditions must be fulfilled.
 *
 * Default implementations can be constructed with using IDefaultGrowConditionFactory, shipped with this API
 */
public interface IAgriGrowCondition {
    /**
     * @return the type of requirement this relates to
     */
    RequirementType getType();

    /**
     * Determines if this condition is satisfied at the given position in the given world.
     *
     * @param world : the world object
     * @param pos : the position in the world
     * @param strength : the strength stat of the crop
     *
     * @return {@literal true} <i>if-and-only-if</i> this condition is met at the provided location
     * in the given world, {@literal false} otherwise.
     */
    boolean isMet(@Nonnull World world, @Nonnull BlockPos pos, int strength);

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

    /**
     * Determines the computational complexity associated with the evaluation of this condition.
     * <p>
     * The higher the value, the higher the cost associated with invoking
     * {@link #isMet(World, BlockPos, int)}, and the later in the sequence that this condition will be
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
     * @return the type of caching that applies to this condition (See javadoc on the enum below).
     */
    CacheType getCacheType();

    /**
     * AgriCraft will try to internally cache calls to isMet() for conditions where possible.
     *
     * This enum defines the type of caching that applies to conditions, which is either:
     *  - NONE: no caching; isMet() is called every time
     *  - BLOCK_UPDATE: cache value changes when a block update happens; isMet() is called after a block update only
     *  - FULL: full caching, the cache value will never change; isMet() is only called once
     *
     *  Examples for each type are given below, when in doubt, return NONE.
     */
    enum CacheType {
        /**
         * No caching; examples include "things" which are volatile and can change without notice:
         *  - Presence of an entity
         *  - Only at specific times of the day
         *  - Specific weather is required
         *  - etc.
         */
        NONE,

        /**
         * Block update caching; examples include "things" which stay the same until a block update happens:
         *  - Presence of a certain block at a certain position
         *  - Presence of a liquid inside the block
         *  - etc.
         */
        BLOCK_UPDATE,

        /**
         * Full caching: examples include "things" which will never change:
         *  - Specific dimension is required
         *  - Specific biome is required
         *  - etc.
         */
        FULL
    }

}
