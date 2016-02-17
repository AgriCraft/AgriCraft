package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.init.AgriCraftBlocks;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemCrop extends ItemBase {

	public ItemCrop() {
		super("crop_sticks");
	}

    //I'm overriding this just to be sure
    @Override
    public boolean canItemEditBlocks() {return true;}

    //this is called when you right click with this item in hand
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos cropPos = pos.add(0, 1, 0);
            if (GrowthRequirementHandler.isSoilValid(world, pos) && world.getBlockState(cropPos).getBlock().getMaterial()== Material.air && side == EnumFacing.UP) {
                world.setBlockState(pos.add(0, 1, 0), AgriCraftBlocks.blockCrop.getDefaultState());
                int use = 1;
                if(player.isSneaking() && (player.capabilities.isCreativeMode || stack.stackSize>=2)) {
                    TileEntity tile = world.getTileEntity(cropPos);
                    if(tile != null && (tile instanceof TileEntityCrop)) {
                        ((TileEntityCrop) tile).setCrossCrop(true);
                        use = 2;
                    }
                }
                world.playSoundEffect((double)((float) cropPos.getX() + 0.5F), (double)((float) cropPos.getY() + 0.5F), (double)((float) cropPos.getZ() + 0.5F), net.minecraft.init.Blocks.leaves.stepSound.soundName, (net.minecraft.init.Blocks.leaves.stepSound.getVolume() + 1.0F) / 2.0F, net.minecraft.init.Blocks.leaves.stepSound.getFrequency() * 0.8F);
                stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - use;
                return false;
            }
        }
        return false;   //return false or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
    }
	
}
