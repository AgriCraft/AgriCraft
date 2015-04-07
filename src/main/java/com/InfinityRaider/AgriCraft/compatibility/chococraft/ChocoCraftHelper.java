package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import chococraft.common.config.ChocoCraftItems;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraftforge.oredict.OreDictionary;

public final class ChocoCraftHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.chococraft;
    }

    @Override
    protected void init() {
        //OreDict tags
        OreDictionary.registerOre(Names.OreDict.listAllseed, ChocoCraftItems.gysahlSeedsItem);
    }

    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantGhyshal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
