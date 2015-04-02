package com.InfinityRaider.AgriCraft.compatibility.psychedelicraft;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import ivorius.psychedelicraft.items.PSItems;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PsychedelicraftHelper {
    public static int transformMeta(int meta) {
        return meta==6?10:meta*2;
    }

    public static void initPlants() {
        Class pc_ItemRegistry = PSItems.class;
        Field[] fields = pc_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        CropPlantHandler.registerPlant(new CropPlantPsychedeliCraft(seed));
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
