package com.infinityraider.agricraft.blocks.tiles.decoration;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

import com.infinityraider.agricraft.blocks.decoration.BlockFence;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCustomWood;

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
