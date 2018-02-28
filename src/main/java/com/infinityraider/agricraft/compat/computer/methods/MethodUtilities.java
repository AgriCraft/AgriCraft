/*
 * Utilities for the computercraft api methods.
 */
package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public final class MethodUtilities {

    public static boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
        if (journal == null || journal.getItem() == null || !(journal.getItem() instanceof ItemJournal)) {
            return false;
        }
        Optional<AgriSeed> s = AgriApi.getSeedRegistry().valueOf(seed);
        return s.isPresent() && ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, s.get().getPlant());
    }

    public static Optional<IAgriPlant> getCropPlant(ItemStack specimen) {
        return AgriApi.getSeedRegistry().valueOf(specimen).map(seed -> seed.getPlant());
    }

    public static Optional<IAgriPlant> getCropPlant(TileEntityCrop crop) {
        return Optional.ofNullable(crop.getSeed()).map(s -> s.getPlant());
    }

    public static String genSignature(String name, List<MethodParameter> parameters) {
        StringBuilder signature = new StringBuilder(name + "(");
        boolean separator = false;
        if (parameters != null) {
            for (MethodParameter parameter : parameters) {
                if (separator) {
                    signature.append(", ");
                } else {
                    separator = true;
                }
                signature.append(parameter.getName());
            }
        }
        signature.append(")");
        return signature.toString();
    }

}
