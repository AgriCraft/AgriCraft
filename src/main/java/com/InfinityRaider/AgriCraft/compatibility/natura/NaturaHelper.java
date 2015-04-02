package com.InfinityRaider.AgriCraft.compatibility.natura;


import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import mods.natura.common.NContent;
import net.minecraftforge.oredict.OreDictionary;

public class NaturaHelper {
    public static void init() {
        OreDictionary.registerOre(Names.OreDict.listAllseed, NContent.plantItem);
    }

    public static void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantNatura(0));
            CropPlantHandler.registerPlant(new CropPlantNatura(1));
        } catch(CropPlantHandler.DuplicateCropPlantException e) {
            e.printStackTrace();
        }
    }


}
