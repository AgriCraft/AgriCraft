package com.infinityraider.agricraft.api.v1.fertilizer;

import java.util.Random;
import javax.annotation.Nonnull;

/**
 * An interface for fertilizable things.
 *
 */
public interface IAgriFertilizable {

    /**
     * Checks if the fertilizer can be used on this specific instance of a crop. It depends on if
     * this is a cross crop or a regular crop, and the properties of the fertilizer, and what
     * configs are enabled, and what plant (if any) is here. The return value also should match up
     * with SUCCESS and FAILURE from {@link #acceptsFertilizer(IAgriFertilizer)}.
     *
     * @param fertilizer the fertilizer to be checked
     * @return true if either a) This is a cross crop (so a cross over can happen here), and
     * fertilizer mutations are enabled, and this ferilizer has the ability to trigger mutations (ie
     * cross overs). -OR- b) This is an empty regular crop (so a plant (ie weed) can spawn here).
     * -OR- c) This is a regular crop with a plant that allows fertilizers. false if either a) This
     * is a cross crop, and the config is disabled or this fertilizer can't trigger mutations. -OR-
     * b) This is a regular crop, and there is a plant in it, and the plant does not allow
     * fertilizing.
     */
    boolean acceptsFertilizer(@Nonnull IAgriFertilizer fertilizer);

    /**
     * Called after the specified fertilizer has been applied to this crop. The effects and results may be randomized.
     *
     * @param fertilizer the fertilizer to be applied.
     * @param rand the random number generator to be used.
     */
    void onApplyFertilizer(@Nonnull IAgriFertilizer fertilizer, @Nonnull Random rand);

    /**
     * Applies a growth tick
     */
    void applyGrowthTick();
}
