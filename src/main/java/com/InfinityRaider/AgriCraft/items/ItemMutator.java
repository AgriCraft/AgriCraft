
package com.InfinityRaider.AgriCraft.items;

import java.util.List;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Creative tool to mutate crops. <br>
 * I have no idea what I am doing here... Or do I?
 */
public class ItemMutator extends ItemAgricraft {

	@Override
	protected String getInternalName() {
		return Names.Objects.mutator;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		// Determine if the plant is ok to get.
		if (world.isRemote || !(world.getTileEntity(x, y, z) instanceof TileEntityCrop)) {
		LogHelper.debug("Unable to mutate whatever this is...");
		return false;
		}

		// Find the plant
		TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);

		// Decompose the meta value.
		int[] values = SeedHelper.decomposeMeta(ConfigurationHandler.cropStatCap, 3, stack.getItemDamage());

		// If the mutation would go overboard, abort.
		if ((crop.getStats().growth + values[0]) > ConfigurationHandler.cropStatCap || (crop.getStats().gain + values[1]) > ConfigurationHandler.cropStatCap || (crop.getStats().strength + values[2]) > ConfigurationHandler.cropStatCap) {
		return false;
		}

		// Mutate the plant.
		// Should the analyzed state be kept?
		crop.setPlant(crop.getStats().growth + values[0], crop.getStats().gain + values[1], crop.getStats().strength + values[2], crop.getStats().isAnalyzed, crop.getPlant(), true);

		// Consume the item if not in creative.
		stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;

		return true;

	}

	@Override
	public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
		list.add(new ItemStack(item, 1, SeedHelper.composeMeta(ConfigurationHandler.cropStatCap, 0, 0, 1)));
		list.add(new ItemStack(item, 1, SeedHelper.composeMeta(ConfigurationHandler.cropStatCap, 0, 1, 0)));
		list.add(new ItemStack(item, 1, SeedHelper.composeMeta(ConfigurationHandler.cropStatCap, 1, 0, 0)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		int[] values = SeedHelper.decomposeMeta(ConfigurationHandler.cropStatCap, 3, stack.getItemDamage());
		list.add(StatCollector.translateToLocal("agricraft_tooltip.mutator"));
		list.add(StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + values[0]);
		list.add(StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + values[1]);
		list.add(StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + values[2]);
	}

}
