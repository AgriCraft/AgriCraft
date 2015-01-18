package com.InfinityRaider.AgriCraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySeedStorage extends TileEntityCustomWood {
    public ForgeDirection direction;


    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey("direction")) {
            this.setDirection(tag.getByte("direction"));
        }
        super.readFromNBT(tag);
    }

    //sets the direction based on an int
    public void setDirection(int direction) {
        this.direction = ForgeDirection.getOrientation(direction);
    }
}
