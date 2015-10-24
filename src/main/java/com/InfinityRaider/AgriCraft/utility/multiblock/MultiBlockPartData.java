package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.reference.Names;
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
        tag.setInteger(Names.NBT.x, posX());
        tag.setInteger(Names.NBT.y, posY());
        tag.setInteger(Names.NBT.z, posZ());
        tag.setInteger(Names.NBT.x2, sizeX());
        tag.setInteger(Names.NBT.y2, sizeY());
        tag.setInteger(Names.NBT.z2, sizeZ());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.posX = tag.getInteger(Names.NBT.x);
        this.posY = tag.getInteger(Names.NBT.y);
        this.posZ = tag.getInteger(Names.NBT.z);
        this.sizeX = tag.getInteger(Names.NBT.x2);
        this.sizeY = tag.getInteger(Names.NBT.y2);
        this.sizeZ = tag.getInteger(Names.NBT.z2);
    }

    @Override
    public String toString() {
        return "(x: "+posX()+"/"+sizeX()+", y: "+posY()+"/"+sizeY()+", z: "+posZ()+"/"+sizeZ()+")";
    }
}
