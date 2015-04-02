package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;
import plantmegapack.PlantMegaPack;
import plantmegapack.bin.PMPItems;

import java.lang.reflect.Field;

public class PlantMegaPackHelper {
    public static void init() {
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedOnion"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedSpinach"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCelery"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedLettuce"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedBellPepperYellow"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCorn"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedCucumber"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedTomato"));
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("plantmegapack:seedBeet"));
    }

    public static void initPlants() {
        Class pmp_ItemRegistry = PMPItems.class;
        Field[] fields = pmp_ItemRegistry.getDeclaredFields();
        for (Field field : fields) {
            try {
                Object obj = field.get(PlantMegaPack.items);
                if (obj instanceof ItemSeeds) {
                    ItemSeeds seed = (ItemSeeds) obj;
                    if (seed == PlantMegaPack.items.seedCorn) {

                    } else {
                        CropPlantHandler.registerPlant(new CropPlantPMPSingle(seed));
                    }
                }
            } catch (Exception e) {
                if (ConfigurationHandler.debug) {
                    e.printStackTrace();
                }
            }

        }

    }
}
