package com.infinityraider.agricraft.api.v1.fertilizer;

import java.util.Random;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAgriFertilizer extends IAgriRegisterable<IAgriFertilizer> {

    /**
     * Whether or not this mod can be used on a cross crop to trigger a mutation (does not override
     * configuration option).
     *
     * @return If the fertilizer can trigger a mutation event.
     */
    boolean canTriggerMutation();

    boolean canTriggerWeeds();

    /**
     * This is called when the fertilizer is used on a crop, this only is called if true is returned
     * from hasSpecialBehaviour.
     *
     * @param target The fertilizable object to which the fertilizer was applied.
     * @param stack The stack that the player was holding that triggered the fertilizer to be
     * applied.
     * @param random A random for use in generating probabilities.
     *
     * @return
     */
    ActionResultType applyFertilizer(IAgriFertilizable target, ItemStack stack, Random random);

    /**
     * Called on the client when the fertilizer is applied, can be used for particles or other
     * visual effects.
     *
     */
    @OnlyIn(Dist.CLIENT)
    void performClientAnimations();

}
