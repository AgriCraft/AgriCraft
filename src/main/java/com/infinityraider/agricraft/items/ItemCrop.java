package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.items.tabs.AgriTabs;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class ItemCrop extends ItemBase {

	public ItemCrop() {
		super("crop_sticks", true);
		this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
	}

	//I'm overriding this just to be sure
	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	//this is called when you right click with this item in hand
	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (!world.isRemote) {
			BlockPos cropPos = pos.add(0, 1, 0);
			IBlockState state = world.getBlockState(cropPos);
			if (state.getBlock().getMaterial(state) == Material.AIR && GrowthRequirementHandler.isSoilValid(world, pos) && side == EnumFacing.UP) {
				world.setBlockState(pos.add(0, 1, 0), AgriBlocks.getInstance().CROP.getDefaultState());
				int use = 1;
				SoundType type = Blocks.LEAVES.getSoundType();
				world.playSound(null, (double) ((float) cropPos.getX() + 0.5F), (double) ((float) cropPos.getY() + 0.5F), (double) ((float) cropPos.getZ() + 0.5F), type.getPlaceSound(), SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
				stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - use;
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

}
