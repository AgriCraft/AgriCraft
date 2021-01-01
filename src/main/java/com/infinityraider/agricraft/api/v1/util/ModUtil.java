package com.infinityraider.agricraft.api.v1.util;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModUtil {
    /**
     * Finds the mod container for the mod with the given mod id.
     *
     * @param modId The id of the mod of the container to search for.
     * @return The container associated with the mod of the given id, or the empty optional.
     */
    @Nonnull
    public static Optional<? extends ModContainer> resolveModContainer(@Nullable String modId) {
        return ModList.get().getModContainerById(modId);
    }

    /**
     * Finds the mod instance for the mod with the given mod id.
     *
     * @param <T> The type of the mod instance.
     * @param modId The id of the mod to find the instance of.
     * @param modClass The type token of the mod class.
     * @return The instance of the mod with the given mod id and given type, or the empty optional.
     */
    @Nonnull
    public static <T> Optional<T> resolveModInstance(@Nullable String modId, @Nonnull Class<T> modClass) {
        Objects.requireNonNull(modClass);
        return resolveModContainer(modId)
                .map(ModContainer::getMod)
                .filter(modClass::isInstance)
                .map(modClass::cast);
    }

}
