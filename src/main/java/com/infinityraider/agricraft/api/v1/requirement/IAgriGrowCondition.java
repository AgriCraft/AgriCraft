package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

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
     * Checks this condition at the given position in the given world.
     *
     *
     * @param crop : the crop for which to check this condition
     * @param world : the world object
     * @param pos : the position in the world
     * @param strength : the strength stat of the crop
     *
     * @return the response.
     */
    IAgriGrowthResponse check(IAgriCrop crop, @Nonnull Level world, @Nonnull BlockPos pos, int strength);

    /**
     * @return a set of all block positions, relative to the crop which need to be checked for this condition, can be empty (e.g in case of dimension)
     */
    Set<BlockPos> offsetsToCheck();

    /**
     * Adds a detailed description of the condition when it is not met to the list.
     *
     * @param consumer a consumer accepting the lines of text for a tooltip when the condition is not met.
     */
    void notMetDescription(@Nonnull Consumer<Component> consumer);

    /**
     * Determines the computational complexity associated with the evaluation of this condition.
     * <p>
     * The higher the value, the higher the cost associated with invoking
     * {@link #check(IAgriCrop, Level, BlockPos, int)}, and the later in the sequence that this condition will be
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
