package com.InfinityRaider.AgriCraft.tileentity.decoration;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityFence extends TileEntityCustomWood {
    public TileEntityFence() {
        super();
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    public boolean canConnect(ForgeDirection dir) {
        if(this.worldObj == null) {
            return false;
        }
        Block block = worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
        if (block == null) {
            return false;
        }
        if (block.isAir(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)) {
            return false;
        }
        return true;
    }
}
