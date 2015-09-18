package com.InfinityRaider.AgriCraft.compatibility.psychedelicraft;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.item.ItemSeeds;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;

public final class PsychedelicraftHelper extends ModHelper {
    protected void initPlants() {
        Class pc_ItemRegistry = null;
        try {
            pc_ItemRegistry = Class.forName("ivorius.psychedelicraft.items.PSItems");
        } catch (ClassNotFoundException e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
        assert pc_ItemRegistry != null;
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

    @Override
    protected String modId() {
        return Names.Mods.psychedelicraft;
    }
}
