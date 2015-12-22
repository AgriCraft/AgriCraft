package com.InfinityRaider.AgriCraft.tileentity.decoration;

import com.InfinityRaider.AgriCraft.blocks.BlockFence;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;

public class TileEntityFence extends TileEntityCustomWood {
    public TileEntityFence() {
        super();
    }

    public boolean canConnect(ForgeDirection dir) {
        if(this.worldObj == null) {
            return false;
        }
        Block block = this.getBlockType();
        return ((BlockFence) block).canConnect(worldObj, getPos(), dir);
    }
}
