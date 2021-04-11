package com.infinityraider.agricraft.api.v1.requirement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import net.minecraft.block.BlockState;

import java.util.Optional;

/**
 * An interface for managing AgriCraft soils.
 */
public interface IAgriSoilRegistry extends IAgriRegistry<IAgriSoil>, IAgriAdapter<IAgriSoil> {
    @Override
    default boolean accepts(@Nullable Object obj) {
        return obj instanceof BlockState && this.stream().anyMatch(soil -> soil.isVariant((BlockState) obj));
    }

    @Nonnull
    @Override
    default Optional<IAgriSoil> valueOf(@Nullable Object obj) {
        return obj instanceof BlockState
                ? this.stream().filter(soil ->soil.isVariant((BlockState) obj)).findFirst()
                : Optional.empty();
    }
}
