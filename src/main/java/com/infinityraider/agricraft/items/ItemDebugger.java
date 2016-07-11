package com.infinityraider.agricraft.items;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.entity.EntityVillagerFarmer;
import com.infinityraider.agricraft.init.WorldGen;
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

	@AgriConfigurable(
			category = AgriConfigCategory.DEBUG,
			key = "Enable Debugger Grass Breaker",
			comment = "Enable debugger use as grass clearing tool."
	)
	private static boolean enableGrassBreaker = true;

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
			clearGrass(world, pos);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	public static void clearGrass(World world, BlockPos pos) {
		if (enableGrassBreaker && !world.isRemote) {
			pos = pos.toImmutable();
			for (int x = -radius; x < radius; x++) {
				for (int z = -radius; z < radius; z++) {
					BlockPos loc = pos.add(x, 0, z);
					Block block = world.getBlockState(loc).getBlock();
					if (block instanceof BlockBush) {
						world.destroyBlock(loc, false);
					}
				}
			}
		}
	}

	public static void spawnFarmer(World world, BlockPos pos) {
		if (!world.isRemote) {
			EntityVillagerFarmer entityvillager = new EntityVillagerFarmer(world, WorldGen.getVillagerId());
			entityvillager.setLocationAndAngles((double) pos.getX() + 0.5D, (double) pos.getY() + 1, (double) pos.getZ() + 0.5D, 0.0F, 0.0F);
			world.spawnEntityInWorld(entityvillager);
		}
	}

}
