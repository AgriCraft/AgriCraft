/*
 */
package com.infinityraider.agricraft.api.v1.fertilizer;

import com.infinityraider.agricraft.api.v1.util.MethodResult;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    boolean acceptsFertilizer(@Nullable IAgriFertilizer fertilizer);

    /**
     * Apply the specified fertilizer to this crop. The effects and results may be randomized. The
     * return value signals if any fertilizer should be consumed. It should match up with the
     * results of {@link #acceptsFertilizer(IAgriFertilizer)}
     *
     * @param fertilizer the fertilizer to be applied.
     * @param rand the random number generator to be used.
     * @return PASS if this is the client and nothing should be done in this thread. SUCCESS if this
     * operation is allowed, and fertilizer should be consumed. (Even if no growth happened.) FAIL
     * if this this operation isn't allowed, and fertilizer should NOT be consumed.
     */
    @Nonnull
    MethodResult onApplyFertilizer(@Nullable IAgriFertilizer fertilizer, @Nonnull Random rand);

}
