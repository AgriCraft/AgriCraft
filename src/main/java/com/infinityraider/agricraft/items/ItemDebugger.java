package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.utility.DebugHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDebugger extends ItemBase {

	public ItemDebugger() {
		super("debugger", true);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		DebugHelper.debug(player, world, pos);
		return false;
	}

}
