package com.infinityraider.agricraft.tiles.irrigation;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChannelFull extends TileEntityChannel {

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasNeighbor(EnumFacing direction) {
        if (this.world == null) {
            return true;
        } else {
            return super.hasNeighbor(direction);
        }
    }

}
