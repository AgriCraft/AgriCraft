package com.InfinityRaider.AgriCraft.compatibility.harderwildlife;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

public class HarderWildLifeHelper extends ModHelper {
    @Override
    protected void initPlants() {
        Item seed = (Item) Item.itemRegistry.getObject("HarderWildlife:winterWheatSeeds");
        Item fruit = (Item) Item.itemRegistry.getObject("minecraft:wheat");
        if(seed != null && seed instanceof ItemSeeds && fruit != null) {
            OreDictionary.registerOre("cropWinterWheat", fruit);
            OreDictionary.registerOre("seedWinterWheat", seed);
            CropPlant cropPlant = new CropPlantHarderWildLife((ItemSeeds) seed, "winterWheat");
            try {
                CropPlantHandler.registerPlant(cropPlant);
            } catch(Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.harderWildLife;
    }
}
