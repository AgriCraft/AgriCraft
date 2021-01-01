package com.infinityraider.agricraft.api.v1.soil;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * An interface for managing AgriCraft soils.
 */
public interface IAgriSoilRegistry extends IAgriRegistry<IAgriSoil> {

    boolean contains(@Nullable BlockState state);

    @Nonnull
    Optional<IAgriSoil> get(@Nullable BlockState state);

}
