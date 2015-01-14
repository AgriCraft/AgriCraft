package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.interfaces.IDebuggable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class TileEntityChannel extends TileEntityCustomWood implements IDebuggable{
    protected int lvl;
    protected int lastLvl = 0;
    protected int timer = 0;
    
    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.lvl>0) {
            tag.setInteger(Names.NBT.level, this.lvl);
        }
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey(Names.NBT.level)) {
            this.lvl = tag.getInteger(Names.NBT.level);
        }
        else {
            this.lvl=0;
        }
        super.readFromNBT(tag);
    }

    public int getFluidLevel() {return this.lvl;}

    public void setFluidLevel(int lvl) {
        if(lvl>=0 && lvl<=Constants.mB/2 && lvl!=this.lvl) {
            this.lvl = lvl;
        }
    }

    public float getFluidHeight() {
        return this.getFluidHeight(this.lvl);
    }

    public float getFluidHeight(int lvl) {
        return 5+7*((float) lvl)/((float) Constants.mB/2);
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
            //Only send update to the client every 5 ticks to reduce network stress (thanks, Marcin212)
        	timer++;
        	if(timer%5==0){
        		timer = 0;
        		if(lastLvl != lvl){
        			lastLvl = lvl;
                    this.markDirty();
        		}
        	}
            //find neighbours
            ArrayList<TileEntityCustomWood> neighbours = new ArrayList<TileEntityCustomWood>();
            if(this.hasNeighbour('x', 1)) {neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord));}
            if(this.hasNeighbour('x', -1)) {neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord));}
            if(this.hasNeighbour('z', 1)) {neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1));}
            if(this.hasNeighbour('z', -1)) {neighbours.add((TileEntityCustomWood) this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1));}
            //calculate total fluid lvl and capacity
            int totalLvl=0;
            int nr = 1;
            int updatedLevel=this.getFluidLevel();
            for(TileEntityCustomWood te:neighbours) {
                //neighbour is a channel: add its volume to the total and increase the count
                if(te instanceof TileEntityChannel) {
                    if(!(te instanceof TileEntityValve && ((TileEntityValve) te).isPowered())) {
                        totalLvl = totalLvl + ((TileEntityChannel) te).lvl;
                        nr++;
                    }
                }
                //neighbour is a tank: calculate the fluid levels of the tank and the channel
                else {
                    TileEntityTank tank = (TileEntityTank) te;
                    int Y = tank.getYPosition();
                    float y_c= 16*Y+this.getFluidHeight();  //initial channel water y
                    float y_t = tank.getFluidY();           //initial tank water y
                    float y1 = (float) 5+16*Y;   //minimum y of the channel
                    float y2 = (float) 12+16*Y;  //maximum y of the channel
                    int V_tot = tank.getFluidLevel()+this.lvl;
                    if(y_c!=y_t) {
                        //total volume is below the channel connection
                        if(tank.getFluidY(V_tot)<=y1) {
                            updatedLevel=0;
                            tank.setFluidLevel(V_tot);
                        }
                        //total volume is above the channel connection
                        else if(tank.getFluidY(V_tot-500)>=y2) {
                            updatedLevel=500;
                            tank.setFluidLevel(V_tot-500);
                        }
                        //total volume is between channel connection top and bottom
                        else {
                            //some parameters
                            int tankYSize = tank.getYSize();
                            int C = tank.getTotalCapacity();
                            //calculate the y corresponding to the total volume: y = f(V_tot), V_tank = f(y), V_channel = f(y)
                            float enumerator = ((float) V_tot) + ((500*y1)/(y2-y1)+((float) 2*C)/((float) (16*tankYSize-2)));
                            float denominator = (((float) 500)/(y2-y1)+((float) C)/((float) (16*tankYSize-2)));
                            float y = enumerator/denominator;
                            //convert the y to volumes
                            int channelVolume = (int) Math.floor(500*(y-y1)/(y2-y1));
                            int tankVolume = (int) Math.ceil(C*(y-2)/(16*tankYSize-2));
                            updatedLevel=channelVolume;
                            tank.setFluidLevel(tankVolume);
                        }
                    }
                }
            }
            //equalize water level over all neighbouring channels
            totalLvl = totalLvl + updatedLevel;
            int rest = totalLvl % nr;
            int newLvl = totalLvl / nr;
            if(nr>1) {
                //set fluid levels
                for (TileEntityCustomWood te:neighbours) {
                    if (te instanceof TileEntityChannel) {
                        if(!(te instanceof TileEntityValve && ((TileEntityValve) te).isPowered())) {
                            int lvl = rest==0?newLvl:newLvl+1;
                            rest = rest==0?0:rest-1;
                            ((TileEntityChannel) te).setFluidLevel(lvl);
                        }
                    }
                }
            }
            this.setFluidLevel(newLvl + rest);
        }
    }

    public void drainFluid(int amount) {
        setFluidLevel(lvl - amount);
    }

    @Override
    public void addDebugInfo(List<String> list) {
        list.add("CHANNEL:");
        super.addDebugInfo(list);
        list.add("  - FluidLevel: " + this.getFluidLevel() + "/" + Constants.mB / 2);
        list.add("  - FluidHeight: " + this.getFluidHeight());
    }
}
