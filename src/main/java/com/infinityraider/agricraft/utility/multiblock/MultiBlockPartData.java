package com.infinityraider.agricraft.utility.multiblock;

import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.reference.AgriNBT;

public class MultiBlockPartData implements IMultiBlockPartData {
    private int posX;
    private int posY;
    private int posZ;
    private int sizeX;
    private int sizeY;
    private int sizeZ;

    public MultiBlockPartData(int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    @Override
    public int posX() {
        return posX;
    }

    @Override
    public int posY() {
        return posY;
    }

    @Override
    public int posZ() {
        return posZ;
    }

    @Override
    public int sizeX() {
        return sizeX;
    }

    @Override
    public int sizeY() {
        return sizeY;
    }

    @Override
    public int sizeZ() {
        return sizeZ;
    }

    @Override
    public int size() {
        return sizeX()*sizeY()*sizeZ();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(AgriNBT.X1, posX());
        tag.setInteger(AgriNBT.Y1, posY());
        tag.setInteger(AgriNBT.Z1, posZ());
        tag.setInteger(AgriNBT.X2, sizeX());
        tag.setInteger(AgriNBT.Y2, sizeY());
        tag.setInteger(AgriNBT.Z2, sizeZ());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.posX = tag.getInteger(AgriNBT.X1);
        this.posY = tag.getInteger(AgriNBT.Y1);
        this.posZ = tag.getInteger(AgriNBT.Z1);
        this.sizeX = tag.getInteger(AgriNBT.X2);
        this.sizeY = tag.getInteger(AgriNBT.Y2);
        this.sizeZ = tag.getInteger(AgriNBT.Z2);
    }

    @Override
    public String toString() {
        return "(x: "+posX()+"/"+sizeX()+", y: "+posY()+"/"+sizeY()+", z: "+posZ()+"/"+sizeZ()+")";
    }
}
