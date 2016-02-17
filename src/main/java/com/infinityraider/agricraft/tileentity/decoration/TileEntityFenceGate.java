package com.infinityraider.agricraft.tileentity.decoration;

import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
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
        tag.setBoolean(AgriCraftNBT.FLAG, zAxis);
        tag.setShort(AgriCraftNBT.META, open);
    }

    //this loads the saved data for the tile entity
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        zAxis = tag.getBoolean(AgriCraftNBT.FLAG);
        open = tag.hasKey(AgriCraftNBT.META)?tag.getShort(AgriCraftNBT.META):0;
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

    public void setZAxis(boolean value) {
        if(zAxis!=value) {
            zAxis = value;
            this.markForUpdate();
        }
    }
}
