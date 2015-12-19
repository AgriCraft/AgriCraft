package com.InfinityRaider.AgriCraft.utility;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class WorldCoordinates {
    private int x;
    private int y;
    private int z;
    private BlockPos pos;

    public WorldCoordinates(int x,  int y,  int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pos = new BlockPos(x, y, z);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public BlockPos pos() {
        return pos;
    }

    public Block getBlock(World world) {
        return world.getBlockState(pos).getBlock();
    }

    public int getMetaData(World world) {
        return world.getBlockMetadata(x, y, z);
    }

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(pops);
    }

    public void setBlock(World world, Block block, int meta, int flag) {
        world.setBlock(x, y, z, block, meta, flag);
    }

    public WorldCoordinates getNeighbour(ForgeDirection dir) {
        if(dir == ForgeDirection.UNKNOWN) {
            return this;
        }
        return new WorldCoordinates(x()+dir.offsetX, y()+dir.offsetY, z()+dir.offsetZ);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WorldCoordinates) {
            WorldCoordinates coords = (WorldCoordinates) obj;
            return coords.x() == this.x() && coords.y() == this.y() && coords.z() == this.z();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}
