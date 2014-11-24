package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityChannel extends TileEntityCustomWood {
    private int lvl;

    //OVERRIDES
    //---------
    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.lvl>0) {
            tag.setInteger(Names.level, this.lvl);
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if(tag.hasKey(Names.level)) {
            this.lvl = tag.getInteger(Names.level);
        }
        else {
            this.lvl=0;
        }
    }

    public int getFluidLevel() {
        return this.lvl;
    }


    //CHANNEL METHODS
    //---------------
    public boolean hasNeighbour(char axis, int direction) {
        TileEntity tileEntityAt;
        switch(axis) {
            case 'x': tileEntityAt = this.worldObj.getTileEntity(this.xCoord+direction, this.yCoord, this.zCoord);break;
            case 'z': tileEntityAt = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord+direction);break;
            default: return false;
        }
        return (tileEntityAt!=null) && (tileEntityAt instanceof TileEntityCustomWood) && (this.isSameMaterial((TileEntityCustomWood) tileEntityAt));
    }

}
