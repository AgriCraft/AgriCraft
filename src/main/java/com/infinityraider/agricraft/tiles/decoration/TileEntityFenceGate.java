package com.infinityraider.agricraft.tiles.decoration;

import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.reference.AgriNBT;

public class TileEntityFenceGate extends TileEntityCustomWood {
    private boolean zAxis;
    private short open = 0;

    public TileEntityFenceGate() {
        super();
    }

    //this saves the data on the tile entity
    @Override
    protected void writeNBT(NBTTagCompound tag) {
        tag.setBoolean(AgriNBT.FLAG, zAxis);
        tag.setShort(AgriNBT.META, open);
    }

    //this loads the saved data for the tile entity
    @Override
    protected void readNBT(NBTTagCompound tag) {
        zAxis = tag.getBoolean(AgriNBT.FLAG);
        open = tag.hasKey(AgriNBT.META)?tag.getShort(AgriNBT.META):0;
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
