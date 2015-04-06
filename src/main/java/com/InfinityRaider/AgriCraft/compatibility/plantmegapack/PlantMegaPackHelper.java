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
        OreDictionary.registerOre("seedOnion", (Item) Item.itemRegistry.getObject("plantmegapack:seedOnion"));
        OreDictionary.registerOre("seedSpinach", (Item) Item.itemRegistry.getObject("plantmegapack:seedSpinach"));
        OreDictionary.registerOre("seedCelery", (Item) Item.itemRegistry.getObject("plantmegapack:seedCelery"));
        OreDictionary.registerOre("seedLettuce", (Item) Item.itemRegistry.getObject("plantmegapack:seedLettuce"));
        OreDictionary.registerOre("seedYellowBellPepper", (Item) Item.itemRegistry.getObject("plantmegapack:seedBellPepperYellow"));
        OreDictionary.registerOre("seedCorn", (Item) Item.itemRegistry.getObject("plantmegapack:seedCorn"));
        OreDictionary.registerOre("seedCucumber", (Item) Item.itemRegistry.getObject("plantmegapack:seedCucumber"));
        OreDictionary.registerOre("seedTomato", (Item) Item.itemRegistry.getObject("plantmegapack:seedTomato"));
        OreDictionary.registerOre("seedBeet", (Item) Item.itemRegistry.getObject("plantmegapack:seedBeet"));

        OreDictionary.registerOre("cropOnion", (Item) Item.itemRegistry.getObject("plantmegapack:foodOnion"));
        OreDictionary.registerOre("cropSpinach", (Item) Item.itemRegistry.getObject("plantmegapack:foodSpinach"));
        OreDictionary.registerOre("cropCelery", (Item) Item.itemRegistry.getObject("plantmegapack:foodCelery"));
        OreDictionary.registerOre("cropLettuce", (Item) Item.itemRegistry.getObject("plantmegapack:foodLettuce"));
        OreDictionary.registerOre("cropYellowBellPepper", (Item) Item.itemRegistry.getObject("plantmegapack:foodBellPepperYellow"));
        OreDictionary.registerOre("cropCorn", (Item) Item.itemRegistry.getObject("plantmegapack:foodCorn"));
        OreDictionary.registerOre("cropCucumber", (Item) Item.itemRegistry.getObject("plantmegapack:foodCucumber"));
        OreDictionary.registerOre("cropTomato", (Item) Item.itemRegistry.getObject("plantmegapack:foodTomato"));
        OreDictionary.registerOre("cropBeet", (Item) Item.itemRegistry.getObject("plantmegapack:foodBeet"));

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
                        CropPlantHandler.registerPlant(new CropPlantPMPDouble(seed));
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
