package com.infinityraider.agricraft.api.v1.fertilizer;

import java.util.Random;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * An interface for managing AgriCraft fertilizers.
 */
public interface IAgriFertilizer extends IAgriRegisterable<IAgriFertilizer>, IAgriAdapter<IAgriFertilizer> {

    /**
     * Whether or not this fertilizer can be used on a cross crop to trigger a mutation (does not override
     * configuration option).
     *
     * @return If the fertilizer can trigger a mutation event.
     */
    boolean canTriggerMutation();

    /**
     * Whether or not this fertilizer can spawn weeds.
     *
     * @return If the fertilizer can trigger a mutation event.
     */
    boolean canTriggerWeeds();

    /**
     * This is called when the fertilizer is used on a crop
     *
     * @param world The world in which the IAgriFertilizable is placed
     * @param pos The position of the IAgriFertilizable in the world
     * @param target The fertilizable object to which the fertilizer was applied.
     * @param stack The stack that the player was holding that triggered the fertilizer to be applied.
     * @param random A random for use in generating probabilities.
     * @param entity The entity applying the fertilizer (can be null if fertilized through automation)
     *
     * @return ActionResultType to handle the item use call chain
     */
    InteractionResult applyFertilizer(Level world, BlockPos pos, IAgriFertilizable target, ItemStack stack, Random random, @Nullable LivingEntity entity);

    /**
     * Check if the target can be fertilized by the fertilizer
     *
     * @param target the fertilizable object
     * @return true if the target can be fertilized
     */
    boolean canFertilize(IAgriFertilizable target);

}
