package com.InfinityRaider.AgriCraft.farming;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class CropPlantHandler {
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<Item, HashMap<Integer, CropPlant>>();

    public static void registerPlant(CropPlant plant) throws DuplicateCropPlantException {
        ItemStack stack = plant.getSeed();
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        HashMap<Integer, CropPlant> entryForSeed = cropPlants.get(seed);
        if(entryForSeed!=null) {
            if(entryForSeed.get(meta)!=null) {
                throw new DuplicateCropPlantException();
            }
            else {
                entryForSeed.put(meta, plant);
            }
        }
        else {
            entryForSeed = new HashMap<Integer, CropPlant>();
            entryForSeed.put(meta, plant);
            cropPlants.put(seed, entryForSeed);
        }
    }

    public static NBTTagCompound writePlantToNBT(CropPlant plant) {
        NBTTagCompound tag = new NBTTagCompound();
        plant.getSeed().writeToNBT(tag);
        return tag;
    }

    public static CropPlant readPlantFromNBT(NBTTagCompound tag) {
        return getPlantFromStack(ItemStack.loadItemStackFromNBT(tag));
    }

    public static CropPlant getPlantFromStack(ItemStack stack) {
        try {
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage());
        } catch(NullPointerException nullPointerException) {
            return null;
        }
    }

    public static final class DuplicateCropPlantException extends Exception {
        public DuplicateCropPlantException() {
            super("This plant is already registered");
        }
    }
}
