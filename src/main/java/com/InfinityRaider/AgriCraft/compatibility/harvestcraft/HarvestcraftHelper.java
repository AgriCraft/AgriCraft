package com.InfinityRaider.AgriCraft.compatibility.harvestcraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class HarvestcraftHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.harvestcraft;
    }

    @Override
    protected void init() {}

    @Override
    protected void initPlants() {
        Class hc_ItemRegistry = null;
        try {
            hc_ItemRegistry = Class.forName("com.pam.harvestcraft.ItemRegistry");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert hc_ItemRegistry != null;
        Field[] fields = hc_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        if(seed== Item.itemRegistry.getObject("harvestcraft:cottonItem")) {
                            //cotton, the produce of cotton seeds is also instance of ItemSeeds, but we want to ignore it
                            continue;
                        }
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
