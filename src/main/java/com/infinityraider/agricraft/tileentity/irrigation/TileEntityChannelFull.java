package com.infinityraider.agricraft.tileentity.irrigation;

import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChannelFull extends TileEntityChannel {
    /** this is just so client side it'll render like a connected channel while held in hand */
    public TileEntityChannelFull() {
        super();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasNeighbourCheck(AgriForgeDirection direction) {
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
