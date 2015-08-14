package com.InfinityRaider.AgriCraft.tileentity.decoration;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityFenceGate extends TileEntityCustomWood {
    private boolean zAxis;
    private short open = 0;

    public TileEntityFenceGate() {
        super();
    }

    //this saves the data on the tile entity
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(Names.NBT.flag, zAxis);
        tag.setShort(Names.NBT.meta, open);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        zAxis = tag.getBoolean(Names.NBT.flag);
        open = tag.hasKey(Names.NBT.meta)?tag.getShort(Names.NBT.meta):0;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    public boolean isOpen() {
        return open!=0;
    }

    public short getOpenDirection() {
        return open;
    }

    public void open(boolean forward) {
        if(!isOpen()) {
            open = forward ? (short) 1 : (short) -1;
            this.markForUpdate();
        }
    }

    public void close() {
        if(this.isOpen()) {
            open = 0;
            this.markForUpdate();
        }
    }

    public boolean isZAxis() {
        return zAxis;
    }

    public void setAxis(char c) {
        if(c=='z' || c=='Z') {
            setZAxis(true);
        } else if(c=='x' || c=='X') {
            setZAxis(false);
        }
    }

    public void setZAxis(boolean value) {
        if(zAxis!=value) {
            zAxis = value;
            this.markForUpdate();
        }
    }
}
