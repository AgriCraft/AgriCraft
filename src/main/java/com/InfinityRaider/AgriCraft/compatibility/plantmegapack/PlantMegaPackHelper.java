package com.InfinityRaider.AgriCraft.compatibility.plantmegapack;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PlantMegaPackHelper extends ModHelper {
    private static final String MOD_ID = Names.Mods.plantMegaPack;
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
                //if only it weren't for you stupid exceptions
                if(name.equalsIgnoreCase("GreenBean")) {
                    name = name+'s';
                    food = (Item) Item.itemRegistry.getObject(MOD_ID+":food"+name);
                }
                else if(name.equalsIgnoreCase("Cassava")) {
                    food = (Item) Item.itemRegistry.getObject(MOD_ID+":food"+name+"Root");
                }
                else {
                    continue;
                }
            }
            OreDictionary.registerOre(Names.OreDict.listAllseed, seed);
            OreDictionary.registerOre("seed" + name, seed);
            cleanOreDict("crop" + name);
            OreDictionary.registerOre("crop"+name, food);
        }
    }

    private void cleanOreDict(String name) {
        List<ArrayList<ItemStack>> idToStack;
        try {
            Field field = OreDictionary.class.getDeclaredField("idToStack");
            field.setAccessible(true);
            idToStack = (List<ArrayList<ItemStack>>) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int id = OreDictionary.getOreID(name);
        ArrayList<ItemStack> entriesForId = idToStack.get(id);
        Iterator<ItemStack> iterator = entriesForId.iterator();
        while(iterator.hasNext()) {
            ItemStack entry = iterator.next();
            if(!(entry.getItem() instanceof ItemBlock)) {
                continue;
            }
            ItemBlock itemBlock = (ItemBlock) entry.getItem();
            Block block = itemBlock.field_150939_a;
            if(block instanceof BlockBush) {
                iterator.remove();
            }
        }
    }

    @Override
    protected void initPlants() {
        if(names==null || names.size()==0) {
            return;
        }
        for(String name:names) {
            Item seed = (Item) Item.itemRegistry.getObject(MOD_ID+":seed"+name);
            if(seed==null || !(seed instanceof ItemSeeds)) {continue;}
            try {
                if (name.equalsIgnoreCase("Corn") || name.equalsIgnoreCase("Cassava")) {
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
