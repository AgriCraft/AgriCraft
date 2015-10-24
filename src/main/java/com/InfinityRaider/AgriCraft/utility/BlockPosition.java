package com.InfinityRaider.AgriCraft.utility;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPosition {
    private World world;
    private WorldCoordinates coords;

    public BlockPosition(World world, int x, int y, int z) {
        this.world = world;
        this.coords = new WorldCoordinates(x, y, z);
    }

    public World world() {
        return world;
    }

    public int x() {
        return coords.x();
    }

    public int y() {
        return coords.y();
    }

    public int z() {
        return coords.z();
    }

    public Block getBlock() {
        return coords.getBlock(world);
    }

    public int getMetaData() {
        return coords.getMetaData(world);
    }

    public TileEntity getTileEntity() {
        return coords.getTileEntity(world);
    }

    public void setBlock(Block block, int meta, int flag) {
        coords.setBlock(world, block, meta, flag);
    }

    public int dimensionId() {
        return world.provider.dimensionId;
    }

    public BlockPosition getNeighbour(ForgeDirection dir) {
        if(dir == ForgeDirection.UNKNOWN) {
            return this;
        }
        return new BlockPosition(world, x()+dir.offsetX, y()+dir.offsetY, z()+dir.offsetZ);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BlockPosition) {
            BlockPosition pos = (BlockPosition) obj;
            return pos.world == this.world && pos.coords.equals(this.coords);
        }
        return false;
    }
}
