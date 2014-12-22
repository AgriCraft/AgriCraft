package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

//Tile Entity to store seed data
public class TileEntitySeedData extends TileEntityAgricraft {
    public int growth;
    public int gain;
    public int strength;
    public boolean analyzed;

    public TileEntitySeedData() {}

    public void setStats(int growth, int gain, int strength, boolean analyzed) {
        this.growth=growth;
        this.gain=gain;
        this.strength=strength;
        this.analyzed=analyzed;
    }

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setShort(Names.NBT.growth, (short) growth);
        tag.setShort(Names.NBT.gain, (short) gain);
        tag.setShort(Names.NBT.strength, (short) strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
        super.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.growth=tag.getInteger(Names.NBT.growth);
        this.gain=tag.getInteger(Names.NBT.gain);
        this.strength=tag.getInteger(Names.NBT.strength);
        this.analyzed=tag.hasKey(Names.NBT.analyzed) && tag.getBoolean(Names.NBT.analyzed);
        super.readFromNBT(tag);
    }

    //this tile entity does not need to receive ticks
    @Override
    public boolean canUpdate() {
        return false;
    }

}
