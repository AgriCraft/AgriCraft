package com.infinityraider.agricraft.api.v1.requirement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * An interface for managing AgriCraft soils.
 */
public interface IAgriSoilRegistry extends IAgriRegistry<IAgriSoil>, IAgriAdapter<IAgriSoil> {
    /**
     * @return the AgriCraft IAgriSoilRegistry instance
     */
    @SuppressWarnings("unused")
    static IAgriSoilRegistry getInstance() {
        return AgriApi.getSoilRegistry();
    }

    @Override
    default boolean accepts(@Nullable Object obj) {
        return obj instanceof BlockState && this.stream().anyMatch(soil -> soil.isVariant((BlockState) obj));
    }

    @Nonnull
    @Override
    default Optional<IAgriSoil> valueOf(@Nullable Object obj) {
        if(obj instanceof ItemStack) {
            return this.valueOf(((ItemStack) obj).getItem());
        }
        if(obj instanceof BlockItem) {
            return this.valueOf(((BlockItem) obj).getBlock());
        }
        if(obj instanceof Block) {
            return this.valueOf(((Block) obj).getDefaultState());
        }
        if(obj instanceof BlockState) {
            return this.stream().filter(soil -> soil.isVariant((BlockState) obj)).findFirst();
        }
        return Optional.empty();
    }

    void registerSoilProvider(@Nonnull Block block, @Nonnull IAgriSoilProvider soilProvider);

    @Nonnull
    IAgriSoilProvider getProvider(@Nonnull Block block);

    @Nonnull
    IAgriSoil getNoSoil();
}
