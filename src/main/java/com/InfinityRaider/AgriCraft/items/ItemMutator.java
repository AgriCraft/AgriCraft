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
        int[] values = decomposeMeta(stack.getItemDamage());
        
        // If the mutation would go overboard, abort.
	if ((crop.getGrowth() + values[0]) > ConfigurationHandler.cropStatCap || (crop.getGain() + values[1]) > ConfigurationHandler.cropStatCap || (crop.getStrength() + values[2]) > ConfigurationHandler.cropStatCap) {
	    return false;
	}
	
	// Mutate the plant.
	// Should the analyzed state be kept?
	crop.setPlant(crop.getGrowth() + values[0], crop.getGain() + values[1], crop.getStrength() + values[2], crop.isAnalyzed(), crop.getPlant(), true);

	// Consume the item if not in creative.
	stack.stackSize = player.capabilities.isCreativeMode ? stack.stackSize : stack.stackSize - 1;
	
	return true;

    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(item, 1, composeMeta(ConfigurationHandler.cropStatCap, 0, 0, 1)));
        list.add(new ItemStack(item, 1, composeMeta(ConfigurationHandler.cropStatCap, 0, 1, 0)));
        list.add(new ItemStack(item, 1, composeMeta(ConfigurationHandler.cropStatCap, 1, 0, 0)));
    }
    
    /**
     * Break up the metavalue into an array of mutation values.
     * The system maxes out at a delta of cropStatCap - 1.
     * 
     * @param meta the metadata to decompose.
     * @return an array of delta values.
     */
    private static final int[] decomposeMeta(int meta) {
	return decomposeMeta(ConfigurationHandler.cropStatCap, 3, meta);
    }
    
    /**
     * Breaks up a meta value into an array of parameters.
     * TODO: Move to utility class???
     * 
     * @param elements the number of parameters to decompose into.
     * @param increment the size of each parameter.
     * @param meta the metavalue to decompose.
     * @return the array of parameters from the decomposed meta.
     */
    public static final int[] decomposeMeta(int increment, int elements, int meta) {
	int[] values = new int[elements];
	for(int i = 0; i < values.length; i++) {
	    values[i] = (meta % increment);
	    meta /= increment;
	}
	return values;
    }
    
    /**
     * Creates a metavalue from an array of elements.
     * 
     * @param increment the max value of the elements.
     * @param elements the elements to combine.
     * @return a meta value representing the combined elements.
     */
    public static final int composeMeta(int increment, int...elements) {
	int meta = 0;
	for(int element : elements) {
	    meta *= increment;
	    meta += element;
	}
	return meta;
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
