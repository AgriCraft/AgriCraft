package com.infinityraider.agricraft.items;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.apiimpl.v1.SeedRegistry;

public class ItemTrowel extends ItemBase implements IAgriTrowelItem {
	
	@AgriConfigurable(
			category = AgriConfigCategory.TOOLS,
			key = "Enable Trowel",
			comment = "Set to false to disable the Trowel."
	)
	public static boolean enableTrowel = true;

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
			AgriSeed seed = SeedRegistry.getInstance().getValue(stack);
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

	@Override
	public boolean isEnabled() {
		return enableTrowel;
	}

}
