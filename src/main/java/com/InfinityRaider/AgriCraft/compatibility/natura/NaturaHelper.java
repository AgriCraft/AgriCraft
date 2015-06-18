package com.InfinityRaider.AgriCraft.compatibility.natura;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public final class NaturaHelper extends ModHelper {
    @Override
    protected void init() {
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
    protected String modId() {
        return Names.Mods.natura;
    }
}
