/*
 */
package com.infinityraider.agricraft.api.soil;

import com.infinityraider.agricraft.api.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IAgriSoilRegistry extends IAgriRegistry<IAgriSoil> {
    
    boolean contains(@Nullable IBlockState state);
    
    boolean contains(@Nullable ItemStack stack);
    
    boolean contains(@Nullable FuzzyStack stack);
    
    @Nonnull
    Optional<IAgriSoil> get(@Nullable IBlockState state);
    
    @Nonnull
    Optional<IAgriSoil> get(@Nullable ItemStack stack);
    
    @Nonnull
    Optional<IAgriSoil> get(@Nullable FuzzyStack stack);

}
