/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.api.v1.ITrowel;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 *
 * @author ryeni
 */
public final class MethodUtilities {

	/**
	 * TODO: Remove
	 *
	 * @param args
	 * @return
	 */
	public static ForgeDirection getDirection(Object... args) {
		for (Object obj : args) {
			ForgeDirection dir = ForgeDirection.UNKNOWN;
			if (obj == null) {
				continue;
			}
			if (obj instanceof Object[]) {
				dir = getDirection((Object[]) obj);
			} else if (obj instanceof String) {
				dir = ForgeDirection.valueOf((String) obj);
			}
			if (dir != null && dir != ForgeDirection.UNKNOWN) {
				return dir;
			}
		}
		return ForgeDirection.UNKNOWN;
	}

	public static boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
		if (journal == null || journal.getItem() == null || !(journal.getItem() instanceof ItemJournal)) {
			return false;
		}
		return ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, seed);
	}

	public static CropPlant getCropPlant(ItemStack specimen) {
		ItemStack seed = specimen;
		if (specimen == null || specimen.getItem() == null) {
			return null;
		}
		if (specimen.getItem() instanceof ITrowel) {
			seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
		}
		return CropPlantHandler.getPlantFromStack(seed);
	}

	public static CropPlant getCropPlant(TileEntityCrop crop) {
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
