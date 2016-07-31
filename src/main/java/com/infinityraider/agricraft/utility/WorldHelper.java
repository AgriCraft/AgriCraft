/*
 */
package com.infinityraider.agricraft.utility;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldHelper {
	
	public static <T> T getBlock(IBlockAccess world, BlockPos pos, Class<T> type) {
		Block b = world.getBlockState(pos).getBlock();
		if (b != null && type.isAssignableFrom(b.getClass())) {
			return type.cast(b);
		} else {
			return null;
		}
	}
	
	public static <T> T getTile(IBlockAccess world, BlockPos pos, Class<T> type) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && type.isAssignableFrom(te.getClass())) {
			return type.cast(te);
		} else {
			return null;
		}
	}
	
	public static <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type) {
		return getTileNeighbors(world, pos, type, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST);
	}

	public static <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type, EnumFacing... dirs) {
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
