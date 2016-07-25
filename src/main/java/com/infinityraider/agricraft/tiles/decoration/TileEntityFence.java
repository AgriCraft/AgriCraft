package com.infinityraider.agricraft.tiles.decoration;

import com.infinityraider.agricraft.blocks.decoration.BlockFence;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

public class TileEntityFence extends TileEntityCustomWood {
    public TileEntityFence() {
        super();
    }

    public boolean canConnect(EnumFacing dir) {
        if(this.worldObj == null) {
            return false;
        }
        Block block = this.getBlockType();
        return ((BlockFence) block).canConnect(worldObj, getPos(), dir);
    }
}
