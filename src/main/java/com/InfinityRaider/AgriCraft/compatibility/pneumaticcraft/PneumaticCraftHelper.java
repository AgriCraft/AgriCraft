package com.InfinityRaider.AgriCraft.compatibility.pneumaticcraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.Block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PneumaticCraftHelper extends ModHelper {
    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {
        Method getPlantsMethod = null;
        int maxMeta = 16;
        try {
            Class PC_SeedClass = Class.forName("pneumaticCraft.common.item.ItemPlasticPlants");
            Method[] methods = PC_SeedClass.getDeclaredMethods();
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers()) && method.getReturnType() == Block.class) {
                    getPlantsMethod = method;
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < maxMeta; i++) {
            try {
                assert getPlantsMethod != null;
                Block plant = (Block) getPlantsMethod.invoke(null, i);
                if(plant != null) {
                    CropPlantHandler.registerPlant(new CropPlantPneumaticCraft(i, plant));
                }
            } catch (DuplicateCropPlantException e1) {
                e1.printStackTrace();
            } catch (BlacklistedCropPlantException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            } catch (IllegalAccessException e4) {
                e4.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return "PneumaticCraft";
    }
}
