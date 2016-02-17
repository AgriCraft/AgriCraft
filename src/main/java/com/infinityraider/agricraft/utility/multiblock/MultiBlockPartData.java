package com.infinityraider.agricraft.utility.multiblock;

import com.infinityraider.agricraft.reference.AgriCraftNBT;
import net.minecraft.nbt.NBTTagCompound;

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
        tag.setInteger(AgriCraftNBT.X1, posX());
        tag.setInteger(AgriCraftNBT.Y1, posY());
        tag.setInteger(AgriCraftNBT.Z1, posZ());
        tag.setInteger(AgriCraftNBT.X2, sizeX());
        tag.setInteger(AgriCraftNBT.Y2, sizeY());
        tag.setInteger(AgriCraftNBT.Z2, sizeZ());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.posX = tag.getInteger(AgriCraftNBT.X1);
        this.posY = tag.getInteger(AgriCraftNBT.Y1);
        this.posZ = tag.getInteger(AgriCraftNBT.Z1);
        this.sizeX = tag.getInteger(AgriCraftNBT.X2);
        this.sizeY = tag.getInteger(AgriCraftNBT.Y2);
        this.sizeZ = tag.getInteger(AgriCraftNBT.Z2);
    }

    @Override
    public String toString() {
        return "(x: "+posX()+"/"+sizeX()+", y: "+posY()+"/"+sizeY()+", z: "+posZ()+"/"+sizeZ()+")";
    }
}
