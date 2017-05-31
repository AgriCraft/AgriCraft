/*
 */
package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Ryan
 */
public class AgriSoilRegistry extends AgriRegistry<IAgriSoil> implements IAgriSoilRegistry {

    @Override
    public boolean contains(@Nullable IBlockState state) {
        return FuzzyStack.from(state)
                .filter(this::contains)
                .isPresent();
    }

    @Override
    public boolean contains(@Nullable ItemStack stack) {
        return FuzzyStack.from(stack)
                .filter(this::contains)
                .isPresent();
    }

    @Override
    public boolean contains(@Nullable FuzzyStack stack) {
        return (stack != null) && (this.stream().anyMatch(e -> e.isVarient(stack)));
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> get(@Nullable IBlockState state) {
        return FuzzyStack.from(state).flatMap(this::get);
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> get(@Nullable ItemStack stack) {
        return FuzzyStack.from(stack).flatMap(this::get);
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> get(@Nullable FuzzyStack stack) {
        if (stack != null) {
            return this.stream().filter(e -> e.isVarient(stack)).findFirst();
        } else {
            return Optional.empty();
        }
    }

}
