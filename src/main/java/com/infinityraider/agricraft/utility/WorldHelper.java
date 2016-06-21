/*
 */
package com.infinityraider.agricraft.utility;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A class for helping interactions with the world.
 *
 * @author RlonRyan
 */
public class WorldHelper {
	
	public static <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type) {
		return getTileNeighbors(world, pos, type, AgriForgeDirection.NORTH, AgriForgeDirection.EAST, AgriForgeDirection.SOUTH, AgriForgeDirection.WEST);
	}

	public static <T> List<T> getTileNeighbors(World world, BlockPos pos, Class<T> type, AgriForgeDirection... dirs) {
		List<T> neighbours = new ArrayList<>();
		for (AgriForgeDirection dir : dirs) {
			TileEntity te = world.getTileEntity(pos.add(dir.offsetX, dir.offsetY, dir.offsetZ));
			if (te != null && type.isAssignableFrom(te.getClass())) {
				neighbours.add(type.cast(te));
			}
		}
		return neighbours;
	}

}
