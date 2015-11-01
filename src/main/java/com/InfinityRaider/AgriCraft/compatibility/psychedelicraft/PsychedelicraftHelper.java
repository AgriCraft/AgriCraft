package com.InfinityRaider.AgriCraft.compatibility.psychedelicraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class PsychedelicraftHelper extends ModHelper {
    protected void initPlants() {
        Item coffeaSeed = (Item) Item.itemRegistry.getObject("psychedelicraft:coffeaCherries");
        Class pc_ItemRegistry = null;
        try {
            pc_ItemRegistry = Class.forName("ivorius.psychedelicraft.items.PSItems");
        } catch (ClassNotFoundException e) {
            LogHelper.printStackTrace(e);
        }
        assert pc_ItemRegistry != null;
        Field[] fields = pc_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        if(seed == coffeaSeed) {
                            CropPlantHandler.registerPlant(new CropPlantPsychedeliCraftCoffee(seed));
                        } else {
                            CropPlantHandler.registerPlant(new CropPlantPsychedeliCraft(seed));
                        }
                    }
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.psychedelicraft;
    }
}
