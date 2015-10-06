package com.InfinityRaider.AgriCraft.items;

import java.util.List;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/**
 * Creative tool to mutate crops.
 * <br>
 * I have no idea what I am doing here...
 */
public class ItemMutator extends ItemAgricraft {

    @Override
    protected String getInternalName() {
        return Names.Objects.mutator;
    }

    /**
     * Not quite sure how the item use gets called...
     */
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
        int[] values = decomposeMeta(stack.getItemDamage());
        
        // If the mutation would go overboard, abort.
	if ((crop.getGrowth() + values[0]) > ConfigurationHandler.cropStatCap || (crop.getGain() + values[1]) > ConfigurationHandler.cropStatCap || (crop.getStrength() + values[2]) > ConfigurationHandler.cropStatCap) {
	    return false;
	}
	
	// Mutate the plant.
	crop.setPlant(crop.getGrowth() + values[0], crop.getGain() + values[1], crop.getStrength() + values[2], false, crop.getPlant(), true);

	// Consume the item if not in creative.
	stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;
	
	return true;

    }


    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, (int)Math.pow(ConfigurationHandler.cropStatCap, 0)));
        list.add(new ItemStack(item, 1, (int)Math.pow(ConfigurationHandler.cropStatCap, 1)));
        list.add(new ItemStack(item, 1, (int)Math.pow(ConfigurationHandler.cropStatCap, 2)));
    }
    
    /**
     * Break up the metavalue into an array of mutation values.
     * The system maxes out at a delta of cropStatCap - 1.
     * 
     * @param meta the metadata to decompose.
     * @return an array of delta values.
     */
    private static int[] decomposeMeta(int meta) {
	int[] values = new int[3];
	for(int i = 0; i < values.length; i++) {
	    values[i] = (meta % ConfigurationHandler.cropStatCap);
	    meta /= ConfigurationHandler.cropStatCap;
	}
	return values;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
	int[] values = decomposeMeta(stack.getItemDamage());
	list.add(StatCollector.translateToLocal("agricraft_tooltip.mutator"));
	list.add(StatCollector.translateToLocal("agricraft_tooltip.growth") + ": " + values[0]);
	list.add(StatCollector.translateToLocal("agricraft_tooltip.gain") + ": " + values[1]);
	list.add(StatCollector.translateToLocal("agricraft_tooltip.strength") + ": " + values[2]);
    }

}
