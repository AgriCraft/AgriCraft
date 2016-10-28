package com.infinityraider.agricraft.blocks.tiles.irrigation;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChannelFull extends TileEntityChannel {

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasNeighbourCheck(EnumFacing direction) {
        if (this.worldObj == null) {
            return true;
        } else {
            return super.hasNeighbourCheck(direction);
        }
    }

}
