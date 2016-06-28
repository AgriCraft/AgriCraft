package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.items.ITrowel;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import com.infinityraider.agricraft.api.v1.seed.ISeedHandler;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.v1.PlantRegistry;
import com.infinityraider.agricraft.farming.PlantStats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import com.infinityraider.agricraft.reference.AgriNBT;

public class ItemTrowel extends ItemBase implements ITrowel, ISeedHandler {

	public ItemTrowel() {
		super("trowel", true, "", "full");
		this.maxStackSize = 1;
	}

	//I'm overriding this just to be sure
	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	// this is called when you right click with this item in hand
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitx, float hity, float hitz) {
		if (world.isRemote) {
			return EnumActionResult.PASS;
		}
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IAgriCrop) {
			IAgriCrop crop = (IAgriCrop) te;
			AgriSeed seed = getSeed(stack);
			if (seed == null && crop.hasPlant()) {
				seed = crop.removeSeed();
				if (seed != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString(AgriNBT.SEED, seed.getPlant().getId());
					seed.getStat().writeToNBT(tag);
					stack.setTagCompound(tag);
					stack.setItemDamage(1);
					return EnumActionResult.SUCCESS;
				} else {
					return EnumActionResult.FAIL;
				}
			} else if (seed != null && !crop.hasPlant()) {
				if (crop.setSeed(seed)) {
					stack.setTagCompound(new NBTTagCompound());
					stack.setItemDamage(0);
					return EnumActionResult.SUCCESS;
				} else {
					return EnumActionResult.FAIL;
				}
			}
		}
		return EnumActionResult.PASS;
	}

	// Holder
	@Override
	public boolean isValid(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemTrowel;
	}

	@Override
	public AgriSeed getSeed(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return null;
		}
		IAgriPlant plant = PlantRegistry.getInstance().getPlant(stack.getTagCompound().getString(AgriNBT.SEED));
		IAgriStat stat = new PlantStats(stack);
		if (plant != null) {
			return new AgriSeed(plant, stat);
		} else {
			return null;
		}
	}

}
