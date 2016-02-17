package com.infinityraider.agricraft.tileentity.decoration;

import com.infinityraider.agricraft.blocks.BlockFence;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.block.Block;

public class TileEntityFence extends TileEntityCustomWood {
    public TileEntityFence() {
        super();
    }

    public boolean canConnect(AgriForgeDirection dir) {
        if(this.worldObj == null) {
            return false;
        }
        Block block = this.getBlockType();
        return ((BlockFence) block).canConnect(worldObj, getPos(), dir);
    }
}
