package com.InfinityRaider.AgriCraft.compatibility.weeeflowers;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.pam.weeeflowers.weeeflowers;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class WeeeFlowersHelper extends ModHelper {
    @Override
    protected void init() {}

    @Override
    protected void initPlants() {
        Class wf_ItemRegistry = weeeflowers.class;
        Field[] fields = wf_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        CropPlantHandler.registerPlant(new CropPlantWeeeFlower(seed));
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
        return Names.Mods.weeeFlowers;
    }
}
