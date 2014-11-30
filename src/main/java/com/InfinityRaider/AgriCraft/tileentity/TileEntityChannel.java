package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class TileEntityChannel extends TileEntityCustomWood {
    private int lvl;

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

    public int getFluidLevel() {return this.lvl;}

    public void setFluidLevel(int lvl) {
        this.lvl = lvl;
        this.markDirty();
    }

    public float getFluidHeight() {
        return 5+7*((float) this.lvl)/((float) Constants.mB/2);
    }

    public boolean hasNeighbour(char axis, int direction) {
        TileEntity tileEntityAt;
        switch(axis) {
            case 'x': tileEntityAt = this.worldObj.getTileEntity(this.xCoord+direction, this.yCoord, this.zCoord);break;
            case 'z': tileEntityAt = this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord+direction);break;
            default: return false;
        }
        return (tileEntityAt!=null) && (tileEntityAt instanceof TileEntityCustomWood) && (this.isSameMaterial((TileEntityCustomWood) tileEntityAt));
    }

    //updates the tile entity every tick
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            //find neighbours
            ArrayList<TileEntityCustomWood> neighbours = new ArrayList<TileEntityCustomWood>();
            if(this.hasNeighbour('x', 1)) {
                neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord));
            }
            if(this.hasNeighbour('x', -1)) {
                neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord));
            }
            if(this.hasNeighbour('z', 1)) {
                neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1));
            }
            if(this.hasNeighbour('z', -1)) {
                neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1));
            }
            //calculate total fluid lvl and capacity
            int totalLvl = 0;
            int nr = 1;
            for(TileEntityCustomWood te:neighbours) {
                if(te instanceof TileEntityChannel) {
                    totalLvl = totalLvl + ((TileEntityChannel) te).lvl;
                    nr++;
                }
                else {
                    TileEntityTank tank = (TileEntityTank) te;
                    int yPos = tank.getYPosition();
                    int tankArea = tank.getXSize()*tank.getZSize();
                    float channelWaterHeight= 16*yPos+this.getFluidHeight();
                    float tankWaterHeight = 16*((float) tank.getFluidLevel())/((float) tankArea*tank.getSingleCapacity());
                    int totalVolume = tank.getFluidLevel() + this.lvl;
                    //total volume is below the channel connection
                    if(16*((float) totalVolume)/((float) tankArea*tank.getSingleCapacity()) <= 5+16*yPos) {
                        this.setFluidLevel(0);
                        tank.setFluidLevel(totalVolume);
                    }
                    //total volume is above the channel connection
                    else if(16*((float) totalVolume-500)/((float) tankArea*tank.getSingleCapacity()) >= 12+16*yPos) {
                        this.setFluidLevel(500);
                        tank.setFluidLevel(totalVolume-500);
                    }
                    //total volume is between channel connection top and bottom
                    else {
                        int volumeInTank = ((16*yPos+5-2)*tank.getTotalCapacity())/(16*tank.getYSize()-2);
                        int syncvolume = totalVolume-volumeInTank;
                        float y = ((float) (7*syncvolume+5*(8000*tankArea+500)))/((float) (8000*tankArea+500));
                        this.setFluidLevel(Math.round(500*(y-5)/7));
                        tank.setFluidLevel(volumeInTank+Math.round(8000*tankArea*(y-5)/7));
                    }
                }
            }
            totalLvl = totalLvl+this.lvl;
            //set fluid levels
            this.lvl = totalLvl/nr;
            this.markDirty();
            for(TileEntityCustomWood te:neighbours) {
                if(te instanceof TileEntityChannel) {
                    ((TileEntityChannel) te).lvl = this.lvl;
                    te.markDirty();
                }
            }

        }
    }
}
