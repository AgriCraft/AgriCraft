package com.InfinityRaider.AgriCraft.tileentity.irrigation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityChannelFull extends TileEntityChannel {
    /** this is just so client side it'll render like a connected channel while held in hand */
    public TileEntityChannelFull() {
        super();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasNeighbourCheck(ForgeDirection direction) {
        if (this.worldObj == null) {
            return true;
        } else {
            return super.hasNeighbourCheck(direction);
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
