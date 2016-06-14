package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemCrop extends ItemBase {

	public ItemCrop() {
		super("crop_sticks", true);
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
			if (state.getBlock().getMaterial(state) == Material.air && GrowthRequirementHandler.isSoilValid(world, pos) && side == EnumFacing.UP) {
				world.setBlockState(pos.add(0, 1, 0), AgriCraftBlocks.blockCrop.getDefaultState());
				int use = 1;
				SoundType type = Blocks.leaves.getStepSound();
				world.playSound(null, (double) ((float) cropPos.getX() + 0.5F), (double) ((float) cropPos.getY() + 0.5F), (double) ((float) cropPos.getZ() + 0.5F), type.getPlaceSound(), SoundCategory.PLAYERS, (type.getVolume() + 1.0F) / 4.0F, type.getPitch() * 0.8F);
				stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - use;
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
	}

}
