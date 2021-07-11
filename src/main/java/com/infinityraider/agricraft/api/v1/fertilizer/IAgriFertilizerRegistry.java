package com.infinityraider.agricraft.api.v1.fertilizer;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilProvider;
import com.infinityraider.agricraft.api.v1.util.IAgriRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * An interface for managing AgriCraft fertilizers.
 */
public interface IAgriFertilizerRegistry extends IAgriRegistry<IAgriFertilizer>, IAgriAdapter<IAgriFertilizer> {
    /**
     * @return the AgriCraft IAgriFertilizerRegistry instance
     */
    @SuppressWarnings("unused")
    static IAgriFertilizerRegistry getInstance() {
        return AgriApi.getFertilizerRegistry();
    }

    @Override
    default boolean accepts(@Nullable Object obj) {
        return obj instanceof Item && this.stream().anyMatch(fertilizer -> fertilizer.isVariant((Item) obj));
    }

    @Nonnull
    @Override
    default Optional<IAgriFertilizer> valueOf(@Nullable Object obj) {
        if(obj instanceof ItemStack) {
            return this.valueOf(((ItemStack) obj).getItem());
        }
        if(obj instanceof Item) {
            return this.stream().filter(fertilizer -> fertilizer.isVariant((Item) obj)).findFirst();
        }
        return Optional.empty();
    }

    void registerFertilizerProvider(@Nonnull Item item, @Nonnull IAgriFertilizerProvider fertilizerProvider);

    @Nonnull
    IAgriFertilizerProvider getProvider(@Nonnull Item item);

    @Nonnull
    IAgriFertilizer getNoFertilizer();
}
