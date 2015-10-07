package com.InfinityRaider.AgriCraft.compatibility.applemilktea;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AppleMilkTeaHelper extends ModHelper {
    @Override
    protected void initPlants() {
        //mint
        Item seed = (Item) Item.itemRegistry.getObject("DCsAppleMilk:defeatedcrow.seedMint");
        Item fruit = (Item) Item.itemRegistry.getObject("DCsAppleMilk:defeatedcrow.leafTea");
        Block plant = (Block) Block.blockRegistry.getObject("DCsAppleMilk:defeatedcrow.cropMint");
        try {
            CropPlantHandler.registerPlant(new CropPlantAppleMilkTea(new ItemStack(seed), plant, 2, new ItemStack(fruit, 1, 1)));
        } catch (Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return "DCsAppleMilk";
    }
}
