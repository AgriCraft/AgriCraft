package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.api.v1.CropPlant;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantAgriCraft;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.init.CustomCrops;
import com.InfinityRaider.AgriCraft.init.ResourceCrops;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class CropPlantHandler {
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<Item, HashMap<Integer, CropPlant>>();

    public static void registerPlant(IAgriCraftPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException {
        registerPlant(new CropPlantAgriCraft(plant));
    }

    public static void registerPlant(CropPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException {
        ItemStack stack = plant.getSeed();
        if(SeedHelper.isSeedBlackListed(stack)) {
            throw new BlacklistedCropPlantException();
        }
        LogHelper.debug("Registering plant for "+stack.getUnlocalizedName());
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

    public static boolean isValidSeed(ItemStack stack) {
        try {
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage())!=null;
        } catch (NullPointerException e) {
            return false;
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

    //TODO: fix pumpkins, melons, wheat & nether wart
    public static void init() {
        //register mod crops
        ModIntegration.initModPlants();
    }

}
