/*
 */
package com.infinityraider.agricraft.items.modes;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author RlonRyan
 */
public class DebugModeDirtPlane extends DebugMode {
	
	@AgriConfigurable(
			category = AgriConfigCategory.DEBUG,
			key = "Dirt Plane Radius",
			min = "1", max = "50",
			comment = "The radius of the grass breaking tool."
	)
	private static int radius = 10;
	
	static {
		AgriCore.getConfig().addConfigurable(DebugModeDirtPlane.class);
	}
	
	@Override
	public String debugName() {
		return "place dirt";
	}

	@Override
	public void debugAction(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		pos = pos.add(0, 1, 0).toImmutable();
		for (int x = -radius; x < radius; x++) {
			for (int z = -radius; z < radius; z++) {
				BlockPos loc = pos.add(x, 0, z);
				if (world.isAirBlock(loc)) {
					world.setBlockState(loc, Blocks.GRASS.getDefaultState());
				}
			}
		}
	}
	
}
