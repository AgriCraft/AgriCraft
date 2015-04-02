package com.InfinityRaider.AgriCraft.compatibility.harvestcraft;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.pam.harvestcraft.ItemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class HarvestcraftHelper {
    public static void initPlants() {
        Class hc_ItemRegistry = ItemRegistry.class;
        Field[] fields = hc_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        CropPlantHandler.registerPlant(new CropPlantHarvestCraft(seed));
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
