package com.InfinityRaider.AgriCraft.tileentity.irrigation;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityChannelFull extends TileEntityChannel {
    /** this is just so client side it'll render like a connected channel while held in hand */
    public TileEntityChannelFull() {
        super();
    }

    @Override
    public boolean hasNeighbour(char axis, int direction) {
        if (this.worldObj == null) {
            return true;
        } else {
            return super.hasNeighbour(axis, direction);
        }
    }


    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }
}
