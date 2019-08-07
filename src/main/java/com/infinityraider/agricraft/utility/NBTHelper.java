package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.reference.AgriNBT;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class NBTHelper {

    public static final void addCoordsToNBT(int[] coords, NBTTagCompound tag) {
        if (coords != null && coords.length == 3) {
            addCoordsToNBT(coords[0], coords[1], coords[2], tag);
        }
    }

    public static final void addCoordsToNBT(int x, int y, int z, NBTTagCompound tag) {
        tag.setInteger(AgriNBT.X, x);
        tag.setInteger(AgriNBT.Y, y);
        tag.setInteger(AgriNBT.Z, z);
    }

    public static final int[] getCoordsFromNBT(NBTTagCompound tag) {
        int[] coords = null;
        if (tag.hasKey(AgriNBT.X) && tag.hasKey(AgriNBT.Y) && tag.hasKey(AgriNBT.Z)) {
            coords = new int[]{tag.getInteger(AgriNBT.X), tag.getInteger(AgriNBT.Y), tag.getInteger(AgriNBT.Z)};
        }
        return coords;
    }

    public static final boolean hasKey(NBTTagCompound tag, String... keys) {
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
    public static final NBTTagCompound asTag(Object obj) {
        if (obj instanceof ItemStack) {
            return ((ItemStack) obj).getTagCompound();
        } else if (obj instanceof NBTTagCompound) {
            return (NBTTagCompound) obj;
        } else {
            return null;
        }
    }

    /**
     * Private constructor to prevent utility class initialization.
     */
    private NBTHelper() {
        // Nothing to see here.
    }

}
