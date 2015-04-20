package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.util.ArrayList;

public final class PlantMegaPackHelper extends ModHelper {
    private static final String MOD_ID = "plantmegapack";
    private ArrayList<String> names;

    @Override
    protected void init() {
        findNames();
        registerOres();
    }

    private void findNames() {
        Class seedEnum;
        try {
            seedEnum = Class.forName("plantmegapack.common.PMPCropSeed");
        } catch (ClassNotFoundException e) {
            return;
        }
        if(!seedEnum.isEnum()) {
            return;
        }
        names = new ArrayList<String>();
        Object[] constants = seedEnum.getEnumConstants();
        Field nameField = null;
        for(Field field:seedEnum.getDeclaredFields()) {
            if(field.getType()==String.class) {
                nameField = field;
                break;
            }
        }
        if(nameField==null) {
            return;
        }
        for (Object constant : constants) {
            try {
                String name = (String) nameField.get(constant);
                names.add(name.substring(4));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerOres() {
        if(names==null || names.size()==0) {return;}
        for(String name:names) {
            Item seed = (Item) Item.itemRegistry.getObject(MOD_ID+":seed"+name);
            Item food = (Item) Item.itemRegistry.getObject(MOD_ID+":food"+name);
            if(seed==null || food==null) {
                continue;
            }
            OreDictionary.registerOre(Names.OreDict.listAllseed, seed);
            OreDictionary.registerOre("seed"+name, seed);
            OreDictionary.registerOre("crop"+name, food);
        }
    }

    @Override
    protected void initPlants() {
        if(names==null || names.size()==0) {
            return;
        }
        for(String name:names) {
            Item seed = (Item) Item.itemRegistry.getObject(MOD_ID+":seed"+name);
            Item food = (Item) Item.itemRegistry.getObject(MOD_ID+":food"+name);
            if(seed==null || food==null || !(seed instanceof ItemSeeds)) {continue;}
            try {
                if (name.equalsIgnoreCase("Corn")) {
                    CropPlantHandler.registerPlant(new CropPlantPMPDouble((ItemSeeds) seed));
                } else {
                    CropPlantHandler.registerPlant(new CropPlantPMPSingle((ItemSeeds) seed));
                }
            } catch(Exception e) {
                if (ConfigurationHandler.debug) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected String modId() {
        return MOD_ID;
    }
}
