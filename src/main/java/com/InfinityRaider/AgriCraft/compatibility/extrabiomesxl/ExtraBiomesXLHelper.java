package com.InfinityRaider.AgriCraft.compatibility.extrabiomesxl;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ExtraBiomesXLHelper extends ModHelper {
    @Override
    protected void initPlants() {
        //strawberry
        Block plant = (Block) Block.blockRegistry.getObject("ExtrabiomesXL:tile.extrabiomes.crop.strawberry");
        Item seed = (Item) Item.itemRegistry.getObject("ExtrabiomesXL:extrabiomes.seed");
        Item fruit = (Item) Item.itemRegistry.getObject("ExtrabiomesXL:extrabiomes.crop");
        if(seed==null || plant==null || fruit==null) {
            return;
        }
        IAgriCraftPlant strawberry = new CropPlantExtraBiomesXL(new ItemStack(seed), plant, new ItemStack(fruit));
        try {
            CropPlantHandler.registerPlant(strawberry);
        } catch (DuplicateCropPlantException e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        } catch (BlacklistedCropPlantException e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return "ExtrabiomesXL";
    }
}
