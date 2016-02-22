package com.InfinityRaider.AgriCraft.compatibility.ganysMods;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

public class GanysSurfaceHelper extends ModHelper {
    @Override
    protected void initPlants() {
        Item seed = (Item) Item.itemRegistry.getObject("ganyssurface:camelliaSeeds");
        Item fruit = (Item) Item.itemRegistry.getObject("ganyssurface:teaLeaves");
        if(seed != null && seed instanceof ItemSeeds && fruit != null) {
            OreDictionary.registerOre("seedCamellia", seed);
            OreDictionary.registerOre("cropCamellia", fruit);
            CropPlantGanysSurface plant = new CropPlantGanysSurface((ItemSeeds) seed, "ganysSurface_camellia");
            try {
                CropPlantHandler.registerPlant(plant);
            } catch (Exception e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.ganysSurface;
    }
}
