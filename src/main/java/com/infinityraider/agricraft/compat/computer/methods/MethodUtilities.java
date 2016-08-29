/*
 * Utilities for the computercraft api methods.
 */
package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import java.util.List;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import java.util.Optional;

/**
 *
 * @author ryeni
 */
public final class MethodUtilities {

    public static boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
        if (journal == null || journal.getItem() == null || !(journal.getItem() instanceof ItemJournal)) {
            return false;
        }
        Optional<AgriSeed> s = SeedRegistry.getInstance().valueOf(seed);
        return s.isPresent() && ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, s.get().getPlant());
    }

    public static IAgriPlant getCropPlant(ItemStack specimen) {
        return SeedRegistry.getInstance().valueOf(specimen).map(seed -> seed.getPlant()).orElse(null);
    }

    public static IAgriPlant getCropPlant(TileEntityCrop crop) {
        return crop.getPlant();
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
