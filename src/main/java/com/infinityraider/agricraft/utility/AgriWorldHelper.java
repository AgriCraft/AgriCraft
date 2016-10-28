/*
 */
package com.infinityraider.agricraft.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * A class for helping interactions with the world.
 *
 *
 */
public class AgriWorldHelper {

    public static final <T> Optional<T> getBlock(IBlockAccess world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world.getBlockState(pos))
                .map(s -> s.getBlock())
                .filter(b -> type.isAssignableFrom(b.getClass()))
                .map(b -> type.cast(b));
    }

    public static final <T> Optional<T> getTile(IBlockAccess world, BlockPos pos, Class<T> type) {
        return Optional.ofNullable(world.getTileEntity(pos))
                .filter(te -> type.isAssignableFrom(te.getClass()))
                .map(te -> type.cast(te));
    }

    public static final <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type) {
        return getTileNeighbors(world, pos, type, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST);
    }

    public static final <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type, EnumFacing... dirs) {
        List<T> neighbours = new ArrayList<>();
        for (EnumFacing dir : dirs) {
            TileEntity te = world.getTileEntity(pos.add(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ()));
            if (te != null && type.isAssignableFrom(te.getClass())) {
                neighbours.add(type.cast(te));
            }
        }
        return neighbours;
    }

}
