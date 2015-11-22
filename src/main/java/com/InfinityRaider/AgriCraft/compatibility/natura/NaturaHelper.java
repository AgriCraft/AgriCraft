package com.InfinityRaider.AgriCraft.compatibility.natura;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public final class NaturaHelper extends ModHelper {
    @Override
    protected void onInit() {
        try {
            Class naturaContent = Class.forName("mods.natura.common.NContent");
            Item seed = (Item) naturaContent.getField("seeds").get(null);
            OreDictionary.registerOre(Names.OreDict.listAllseed, seed);
        } catch (Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantNatura(0));
        } catch(Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
        try {
            CropPlantHandler.registerPlant(new CropPlantNatura(1));
        } catch(Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected List<Item> getTools() {
        ArrayList<Item> list = new ArrayList<Item>();
        list.add((Item) Item.itemRegistry.getObject("Natura:boneBag"));
        return list;
    }

    @Override
    protected boolean useTool(World world, int x, int y, int z, EntityPlayer player, ItemStack stack, BlockCrop block, TileEntityCrop crop) {
        if(stack==null || stack.getItem()==null) {
            return false;
        }
        Item item = stack.getItem();
        if(item!=Item.itemRegistry.getObject("Natura:boneBag")) {
            return false;
        }
        for(int dx=-1;dx<=1;dx++) {
            for(int dz=-1;dz<=1;dz++) {
                Block blockAt = world.getBlock(x+dx, y, z+dz);
                if(blockAt instanceof IGrowable) {
                    if(((IGrowable) blockAt).func_149851_a(world, x+dx, y, z+dz, world.isRemote)) {
                        ((IGrowable) blockAt). func_149853_b(world, world.rand, x+dx, y, z+dz);
                    }
                }
            }
        }
        if(!player.capabilities.isCreativeMode) {
            player.getCurrentEquippedItem().stackSize =player.getCurrentEquippedItem().stackSize-1;
        }
        return true;
    }

    @Override
    protected String modId() {
        return Names.Mods.natura;
    }
}
