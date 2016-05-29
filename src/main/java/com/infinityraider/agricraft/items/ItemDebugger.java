package com.infinityraider.agricraft.items;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.utility.DebugHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDebugger extends ItemBase {

	@AgriConfigurable(
			category = AgriConfigCategory.DEBUG,
			key = "Grass Breaker Radius",
			min = "1", max = "50",
			comment = "The radius of the grass breaking tool."
	)
	private static int radius = 10;

	public ItemDebugger() {
		super("debugger", true);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (!player.isSneaking()) {
			DebugHelper.debug(player, world, pos);
			return EnumActionResult.SUCCESS;
		} else if (!world.isRemote) {
			AgriCore.getCoreLogger().debug("\nCenter: {0}\nRadius: {1}", pos, radius);
			pos = pos.toImmutable();
			for (int x = -radius; x < radius; x++) {
				for (int z = -radius; z < radius; z++) {
					BlockPos loc = pos.add(x, 0, z);
					Block block = world.getBlockState(loc).getBlock();
					if (block instanceof BlockBush) {
						AgriCore.getCoreLogger().debug("Block at {0}: {1}", loc, block);
						world.destroyBlock(loc, false);
					}
				}
			}
			return EnumActionResult.SUCCESS;
			/*
			if(!world.isRemote) {
                EntityVillager entityvillager = new EntityVillagerFarmer(world, WorldGen.getVillagerId());
                entityvillager.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY() + 1, (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
                world.spawnEntityInWorld(entityvillager);
            }
			 */
		}
		return EnumActionResult.PASS;
	}

	static {
		AgriCore.getConfig().addConfigurable(ItemDebugger.class);
	}

}
