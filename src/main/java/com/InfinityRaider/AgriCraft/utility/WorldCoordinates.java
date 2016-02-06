package com.InfinityRaider.AgriCraft.utility;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

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

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(pos);
    }

    public void setBlock(World world, Block block, int flag) {
        world.setBlockState(pos, block.getDefaultState(), flag);
    }

    public WorldCoordinates getNeighbour(AgriForgeDirection dir) {
        if(dir == AgriForgeDirection.UNKNOWN) {
            return this;
        }
        return new WorldCoordinates(x()+dir.offsetX, y()+dir.offsetY, z()+dir.offsetZ);
    }

    public WorldCoordinates getNeighbour(EnumFacing facing) {
        return getNeighbour(AgriForgeDirection.getFromEnumFacing(facing));
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
