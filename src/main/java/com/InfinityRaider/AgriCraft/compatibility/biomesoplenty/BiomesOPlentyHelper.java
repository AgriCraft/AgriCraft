package com.InfinityRaider.AgriCraft.compatibility.biomesoplenty;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BiomesOPlentyHelper extends ModHelper {
    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {
        //turnip
        Block plant = (Block) Block.blockRegistry.getObject("BiomesOPlenty:turnip");
        Item seed = (Item) Item.itemRegistry.getObject("BiomesOPlenty:turnipSeeds");
        Item fruit = (Item) Item.itemRegistry.getObject("BiomesOPlenty:food");
        CropPlant strawberry = new CropPlantBiomesOPlenty(seed, plant, new ItemStack(fruit, 1, 11));
        if(seed==null || plant==null || fruit==null) {
            return;
        }
        try {
            CropPlantHandler.registerPlant(strawberry);
        } catch (DuplicateCropPlantException e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        } catch (BlacklistedCropPlantException e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return "BiomesOPlenty";
    }
}
