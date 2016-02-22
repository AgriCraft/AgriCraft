package com.InfinityRaider.AgriCraft.compatibility.calculator;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class CalculatorHelper extends ModHelper {
    @Override
    protected void initPlants() {
        Item seed = (Item) Item.itemRegistry.getObject("Calculator:BroccoliSeeds");
        Block plant = (Block) Block.blockRegistry.getObject("Calculator:BroccoliPlant");
        Item fruit = (Item) Item.itemRegistry.getObject("Calculator:Broccoli");

        if(seed != null && plant != null && fruit != null) {
            OreDictionary.registerOre("cropBroccoli", fruit);
            OreDictionary.registerOre("seedBroccoli", seed);
            CropPlant cropPlant = new CropPlantCalculator(seed, plant, fruit);
            try {
                CropPlantHandler.registerPlant(cropPlant);
            } catch(Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.calculator;
    }
}
