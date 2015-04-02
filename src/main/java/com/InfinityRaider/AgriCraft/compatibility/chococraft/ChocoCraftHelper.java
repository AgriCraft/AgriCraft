package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import chococraft.common.config.ChocoCraftItems;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraftforge.oredict.OreDictionary;

public class ChocoCraftHelper {
    public static void init() {
        //OreDict tags
        OreDictionary.registerOre(Names.OreDict.listAllseed, ChocoCraftItems.gysahlSeedsItem);
    }

    public static void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantGhyshal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
