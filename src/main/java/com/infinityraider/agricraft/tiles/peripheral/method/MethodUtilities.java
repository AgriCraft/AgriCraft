/*
 * Utilities for the computercraft api methods.
 */
package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.api.v1.ITrowel;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.items.ItemJournal;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.List;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

/**
 *
 * @author ryeni
 */
public final class MethodUtilities {

	public static boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
		if (journal == null || journal.getItem() == null || !(journal.getItem() instanceof ItemJournal)) {
			return false;
		}
		return ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, seed);
	}

	public static IAgriCraftPlant getCropPlant(ItemStack specimen) {
		ItemStack seed = specimen;
		if (specimen == null || specimen.getItem() == null) {
			return null;
		}
		if (specimen.getItem() instanceof ITrowel) {
			seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
		}
		return CropPlantHandler.getPlantFromStack(seed);
	}

	public static IAgriCraftPlant getCropPlant(TileEntityCrop crop) {
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
