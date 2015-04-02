package com.InfinityRaider.AgriCraft.compatibility.magicalcrops;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.mark719.magicalcrops.MagicalCrops;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MagicalCropsHelper {
    public static void init() {
        Item[] seeds = {
                MagicalCrops.SeedsBlackberry,
                MagicalCrops.SeedsBlueberry,
                MagicalCrops.SeedsChili,
                MagicalCrops.SeedsCucumber,
                MagicalCrops.SeedsGrape,
                MagicalCrops.SeedsRaspberry,
                MagicalCrops.SeedsStrawberry,
                MagicalCrops.SeedsSweetcorn,
                MagicalCrops.SeedsTomato,
                MagicalCrops.SeedsSugarCane,
        };

        String[] tags = {
                "Blackberry",
                "Blueberry",
                "Chili",
                "Cucumber",
                "Grape",
                "Raspberry",
                "Strawberry",
                "Sweetcorn",
                "Tomato",
                "Sugarcane"
        };

        for(int i=0;i<seeds.length;i++) {
            Item fruit = i==seeds.length-1? Items.reeds:MagicalCrops.CropProduce;
            int meta = i==seeds.length-1?0:i;
            OreDictionary.registerOre(Names.OreDict.listAllseed, seeds[i]);
            OreDictionary.registerOre("seed"+tags[i], seeds[i]);
            OreDictionary.registerOre("crop"+tags[i], new ItemStack(fruit, 1, meta));
        }
    }

    public static void initPlants() {
        Class mc_ItemRegistry = MagicalCrops.class;
        Field[] fields = mc_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        CropPlantHandler.registerPlant(new CropPlantMagicalCrops(seed));
                    }
                } catch (Exception e) {
                    if (ConfigurationHandler.debug) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
