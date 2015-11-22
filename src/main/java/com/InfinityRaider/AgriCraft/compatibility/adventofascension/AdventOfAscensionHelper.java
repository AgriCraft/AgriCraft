package com.InfinityRaider.AgriCraft.compatibility.adventofascension;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AdventOfAscensionHelper extends ModHelper {
    @Override
    protected void onInit() {
        try {
            Class registry = Class.forName("net.nevermine.izer.Plantizer");
            for(Field field:registry.getDeclaredFields()) {
                Object obj = field.get(null);
                if(!(obj instanceof ItemSeeds)) {
                    continue;
                }
                ItemSeeds seed = (ItemSeeds) obj;
                BlockCrops plant = (BlockCrops) seed.getPlant(null, 0, 0, 0);
                for(Method method:plant.getClass().getDeclaredMethods()) {
                    String name = method.getName();
                    if(!name.equals("func_149865_P")) {
                        continue;
                    }
                    method.setAccessible(true);
                    Item fruit = (Item) method.invoke(plant);
                    registerToOreDict(seed, fruit);
                }
            }
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    private void registerToOreDict(ItemSeeds seed, Item fruit) {
        String name = getName(seed);
        OreDictionary.registerOre(Names.OreDict.listAllseed, seed);
        OreDictionary.registerOre("seed"+name, seed);
        OreDictionary.registerOre("crop"+name, fruit);
    }

    private String getName(ItemSeeds seed) {
        String id = Item.itemRegistry.getNameForObject(seed);
        int index = id.indexOf(':');
        if(index>=0) {
            id = id.substring(index+1);
        }
        index = id.indexOf("Seeds");
        if(index>0) {
            id = id.substring(0, index);
        }
        id = Character.toUpperCase(id.charAt(0)) + id.substring(1);
        return id;
    }

    @Override
    protected void initPlants() {
        try {
            Class registry = Class.forName("net.nevermine.izer.Plantizer");
            for(Field field:registry.getDeclaredFields()) {
                Object obj = field.get(null);
                if(!(obj instanceof ItemSeeds)) {
                    continue;
                }
                ItemSeeds seed = (ItemSeeds) obj;
                CropPlantHandler.registerPlant(new CropPlantAoA(seed, getName(seed)));
            }
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.adventOfAscension;
    }
}
