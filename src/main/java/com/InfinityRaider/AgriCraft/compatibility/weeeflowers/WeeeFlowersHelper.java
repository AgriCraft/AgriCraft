package com.InfinityRaider.AgriCraft.compatibility.weeeflowers;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class WeeeFlowersHelper extends ModHelper {
    @Override
    protected void initPlants() {
        Class wf_ItemRegistry;
        Method getMetaMethod;
        try {
            wf_ItemRegistry = Class.forName("com.pam.weeeflowers.weeeflowers");
            getMetaMethod = getMetaMethod(Class.forName("com.pam.weeeflowers.BlockPamFlowerCrop"));
        } catch(Exception e) {
            LogHelper.printStackTrace(e);
            return;
        }
        if(getMetaMethod==null) {
            return;
        }
        Field[] fields = wf_ItemRegistry.getDeclaredFields();
        for(Field field : fields) {
            if(Modifier.isStatic(field.getModifiers())) {
                try {
                    Object obj = field.get(null);
                    if(obj instanceof ItemSeeds) {
                        ItemSeeds seed = (ItemSeeds) obj;
                        String color = Item.itemRegistry.getNameForObject(seed).substring(12);
                        color = color.substring(0, color.indexOf(" Flower"));
                        Block flower = (Block) Block.blockRegistry.getObject("weeeflowers:"+color+" Flower Crop");
                        int meta = (Integer) getMetaMethod.invoke(flower, 0);
                        CropPlantHandler.registerPlant(new CropPlantWeeeFlower(seed, meta));
                    }
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
    }

    private Method getMetaMethod(Class blockClass) {
        for (Method method : blockClass.getDeclaredMethods()) {
            if (method.getReturnType() == int.class) {
                return method;
            }
        }
        return null;
    }

    @Override
    protected String modId() {
        return Names.Mods.weeeFlowers;
    }
}
