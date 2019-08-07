/*
 */
package com.infinityraider.agricraft.api.v1.soil;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.Collection;
import javax.annotation.Nonnull;
import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;

/**
 * Class for interacting with AgriCraft soil definitions.
 */
public interface IAgriSoil extends IAgriRegisterable {

    @Nonnull
    @Override
    String getId();

    @Nonnull
    String getName();

    /**
     * Returns an ItemStack representative of this AgriSoil.
     *
     * @return an ItemStack representing this soil.
     */
    @Nonnull
    Collection<FuzzyStack> getVarients();

    default boolean isVarient(@Nonnull ItemStack stack) {
        // Validate
        Preconditions.checkNotNull(stack);

        // Delegate
        return isVarient(new FuzzyStack(stack));
    }

    default boolean isVarient(@Nonnull FuzzyStack stack) {
        // Validate
        Preconditions.checkNotNull(stack);

        // Evaluate
        return this.getVarients().contains(stack);
    }

}
