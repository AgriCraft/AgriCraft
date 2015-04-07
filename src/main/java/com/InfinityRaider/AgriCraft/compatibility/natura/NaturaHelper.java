package com.InfinityRaider.AgriCraft.compatibility.natura;


import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import mods.natura.common.NContent;
import net.minecraftforge.oredict.OreDictionary;

public final class NaturaHelper extends ModHelper {
    @Override
    protected void init() {
        OreDictionary.registerOre(Names.OreDict.listAllseed, NContent.plantItem);
    }

    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantNatura(0));
            CropPlantHandler.registerPlant(new CropPlantNatura(1));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.natura;
    }
}
