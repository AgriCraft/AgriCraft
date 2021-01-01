package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IAgriWeed extends IAgriRegisterable<IAgriWeed> {
    default boolean isWeed() {
        return true;
    }

    void onRake(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player);
}
