package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.items.IClipper;
import com.infinityraider.agricraft.init.AgriItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ItemClipper extends ItemBase implements IClipper {

	public ItemClipper() {
		super("clipper", true);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	//this is called when you right click with this item in hand
	@Override
	public EnumActionResult onItemUse(ItemStack item, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitx, float hity, float hitz) {
		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IAgriCrop) {
			IAgriCrop crop = (IAgriCrop) te;
			if (crop.hasPlant() && crop.getGrowthStage() > 1) {
				crop.setGrowthStage(crop.getGrowthStage() - 1);
				ItemStack clipping = new ItemStack(AgriItems.clipping);
				clipping.setTagCompound(crop.getSeed().toStack().writeToNBT(new NBTTagCompound()));
				world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY() + 1, pos.getZ(), clipping));
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.FAIL;
		}
		return EnumActionResult.PASS;   //return PASS or else no other use methods will be called (for instance "onBlockActivated" on the crops block)
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
		// Nothing to see here...
	}

}
