package com.infinityraider.agricraft.api.v1.fertilizer;

import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.Optional;

@FunctionalInterface
public interface IAgriFertilizerProvider {

    @Nonnull
    Optional<IAgriFertilizer> getFertilizer(Item stack);
}
