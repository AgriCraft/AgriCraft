/*
 */
package com.infinityraider.agricraft.api.v1.util;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

/**
 *
 * @author Ryan
 */
public class ModUtil {

    /**
     * Finds the mod container for the mod with the given mod id.
     *
     * @param modId The id of the mod of the container to search for.
     * @return The container associated with the mod of the given id, or the empty optional.
     */
    @Nonnull
    public static Optional<ModContainer> resolveModContainer(@Nullable String modId) {
        return Loader.instance()
                .getActiveModList()
                .stream()
                .filter(c -> Objects.equals(modId, c.getModId()))
                .findFirst();
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
