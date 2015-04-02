package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantAgriCraft;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.init.CustomCrops;
import com.InfinityRaider.AgriCraft.init.ResourceCrops;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
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
        //register vanilla plants
        for(BlockModPlant plant : Crops.defaultCrops) {
            CropPlantAgriCraft cropPlant = new CropPlantAgriCraft(plant);
            try {
                registerPlant(cropPlant);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //register botania plants
        if(ConfigurationHandler.integration_Botania) {
            for(BlockModPlant plant : Crops.botaniaCrops) {
                CropPlantAgriCraft cropPlant = new CropPlantAgriCraft(plant);
                try {
                    registerPlant(cropPlant);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //register resource plants
        if(ConfigurationHandler.resourcePlants) {
            for(BlockModPlant plant : ResourceCrops.vanillaCrops) {
                CropPlantAgriCraft cropPlant = new CropPlantAgriCraft(plant);
                try {
                    registerPlant(cropPlant);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            for(BlockModPlant plant : ResourceCrops.modCrops) {
                CropPlantAgriCraft cropPlant = new CropPlantAgriCraft(plant);
                try {
                    registerPlant(cropPlant);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //register custom crops
        for(BlockModPlant plant : CustomCrops.customCrops) {
            CropPlantAgriCraft cropPlant = new CropPlantAgriCraft(plant);
            try {
                registerPlant(cropPlant);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        //register mod crops
        ModIntegration.initModPlants();
    }

    public static final class DuplicateCropPlantException extends Exception {
        public DuplicateCropPlantException() {
            super("This plant is already registered");
        }
    }

    public static final class BlacklistedCropPlantException extends Exception {
        public BlacklistedCropPlantException() {
            super("This plant is blacklisted");
        }
    }
}
