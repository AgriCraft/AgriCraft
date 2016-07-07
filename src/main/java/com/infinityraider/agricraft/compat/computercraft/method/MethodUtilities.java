/*
 * Utilities for the computercraft api methods.
 */
package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.List;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;

/**
 *
 * @author ryeni
 */
public final class MethodUtilities {

	public static boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
		if (journal == null || journal.getItem() == null || !(journal.getItem() instanceof ItemJournal)) {
			return false;
		}
		AgriSeed s = SeedRegistry.getInstance().getValue(seed);
		return s != null && ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, s.getPlant());
	}

	public static IAgriPlant getCropPlant(ItemStack specimen) {
		if (specimen != null || specimen.getItem() != null) {
			AgriSeed seed = SeedRegistry.getInstance().getValue(specimen);
			if (seed != null) {
				return seed.getPlant();
			}
		}
		return null;
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
