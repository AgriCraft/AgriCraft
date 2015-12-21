package com.InfinityRaider.AgriCraft.compatibility.millenaire;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class MillenaireHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.millenaire;
    }

    @Override
    protected void initPlants() {
        Block blockRice = (Block) Block.blockRegistry.getObject("millenaire:tile.ml_cropRice");
        Item itemRice = (Item) Item.itemRegistry.getObject("millenaire:item.ml_rice");
        if(blockRice != null && itemRice != null) {
            try {
                CropPlant plant = new CropPlantMillenaire(blockRice, itemRice);
                CropPlantHandler.registerPlant(plant);
                plant.getGrowthRequirement().setSoil(new BlockWithMeta(Blocks.blockWaterPadFull));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }

        Block blockTurmeric = (Block) Block.blockRegistry.getObject("millenaire:tile.ml_cropTurmeric");
        Item itemTurmeric = (Item) Item.itemRegistry.getObject("millenaire:item.ml_turmeric");
        if(blockTurmeric != null && itemTurmeric != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantMillenaire(blockTurmeric, itemTurmeric));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }

        Block blockMaize = (Block) Block.blockRegistry.getObject("millenaire:tile.ml_cropMaize");
        Item itemMaize = (Item) Item.itemRegistry.getObject("millenaire:item.ml_maize");
        if(blockMaize != null && itemMaize != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantMillenaire(blockMaize, itemMaize));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }
    }
}
