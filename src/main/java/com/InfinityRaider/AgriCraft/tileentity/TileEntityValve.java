package com.InfinityRaider.AgriCraft.tileentity;


public class TileEntityValve extends TileEntityChannel {

    private boolean powered = false;

    @Override
    public void updateEntity() {
        // TODO: This is very inefficient, better only check when neighboring blocks change or find another wa
        powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

    public boolean isPowered() {
        return powered;
    }
}
