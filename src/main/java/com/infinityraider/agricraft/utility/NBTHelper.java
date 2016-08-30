package com.infinityraider.agricraft.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import com.infinityraider.agricraft.reference.AgriNBT;
import javax.annotation.Nullable;

public abstract class NBTHelper {

	public static void addCoordsToNBT(int[] coords, NBTTagCompound tag) {
		if (coords != null && coords.length == 3) {
			addCoordsToNBT(coords[0], coords[1], coords[2], tag);
		}
	}

	public static void addCoordsToNBT(int x, int y, int z, NBTTagCompound tag) {
		tag.setInteger(AgriNBT.X1, x);
		tag.setInteger(AgriNBT.Y1, y);
		tag.setInteger(AgriNBT.Z1, z);
	}

	public static int[] getCoordsFromNBT(NBTTagCompound tag) {
		int[] coords = null;
		if (tag.hasKey(AgriNBT.X1) && tag.hasKey(AgriNBT.Y1) && tag.hasKey(AgriNBT.Z1)) {
			coords = new int[]{tag.getInteger(AgriNBT.X1), tag.getInteger(AgriNBT.Y1), tag.getInteger(AgriNBT.Z1)};
		}
		return coords;
	}

	public static void clearEmptyStacksFromNBT(NBTTagList list) {
		for (int i = list.tagCount() - 1; i >= 0; i--) {
			ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
			if (stack == null || stack.getItem() == null) {
				list.removeTag(i);
			}
		}
	}
	
	public static boolean hasKey(NBTTagCompound tag, String... keys) {
		if (tag == null) {
			return false;
		}
		for (String key : keys) {
			if (!tag.hasKey(key)) {
				return false;
			}
		}
		return true;
	}
	
	@Nullable
	public static NBTTagCompound asTag(Object obj) {
		if (obj instanceof ItemStack) {
			return ((ItemStack) obj).getTagCompound();
		} else if (obj instanceof NBTTagCompound) {
			return (NBTTagCompound) obj;
		} else {
			return null;
		}
	}
	
}
