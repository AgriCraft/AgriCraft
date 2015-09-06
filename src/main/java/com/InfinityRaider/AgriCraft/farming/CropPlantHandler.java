package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.*;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Level;

public class CropPlantHandler {
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<Item, HashMap<Integer, CropPlant>>();
    private static ArrayList<CropPlant> plantsToRegister = new ArrayList<CropPlant>();

    public static void registerPlant(IAgriCraftPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException {
        registerPlant(new CropPlantAgriCraft(plant));
    }
    
    public static void registerPlant(CropPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException { // Not sure why exceptions are thrown only to be caught in the same class.
        ItemStack stack = plant.getSeed();
        if(SeedHelper.isSeedBlackListed(stack)) {
            throw new BlacklistedCropPlantException();
        }
        LogHelper.debug("Registering plant for " + stack.getUnlocalizedName());
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        HashMap<Integer, CropPlant> entryForSeed = cropPlants.get(seed);
        if(entryForSeed!=null) {
            if(entryForSeed.get(meta)!=null) {
                throw new DuplicateCropPlantException(); //Interesting...
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
    
    private static void suppressedRegisterPlant(CropPlant plant) {
    	try {
    		registerPlant(plant);
    	} catch (DuplicateCropPlantException e) {
    		FMLLog.getLogger().log(Level.ERROR, "Unable to register duplicate plant.", e);
    	} catch (BlacklistedCropPlantException e) {
    		FMLLog.getLogger().log(Level.WARN, "Blacklisted plant was not registered.", e);
    	}
    }

    public static void addCropToRegister(CropPlant plant) {
        plantsToRegister.add(plant);
    }

    public static boolean isValidSeed(ItemStack stack) { //The method name should match the rest of the class.
    	if (stack != null && cropPlants.containsKey(stack.getItem())) { //Is the plant/seed in the registry? If the plant/seed is in the registry it is o.k. to get.
    		return cropPlants.get(stack.getItem()).containsKey(stack.getItemDamage()); // Is the damage value in the registry? If so, this is a valid seed. 
    	}
        return false; // Exceptions are very costly and should generally be avoided.
    }
    
    public static boolean isValidPlant(ItemStack stack) { //The method name should match the rest of the class.
    	if (cropPlants.containsKey(stack.getItem())) { //Is the plant/seed in the registry? If the plant/seed is in the registry it is o.k. to get.
    		return cropPlants.get(stack.getItem()).containsKey(stack.getItemDamage()); // Is the damage value in the registry? If so, this is a valid seed. 
    	}
        return false; // Exceptions are very costly and should generally be avoided.
    }

    public static NBTTagCompound writePlantToNBT(CropPlant plant) {
        return plant.getSeed().writeToNBT(new NBTTagCompound());
    }

    public static CropPlant readPlantFromNBT(NBTTagCompound tag) {
        return getPlantFromStack(ItemStack.loadItemStackFromNBT(tag));
    }

    public static CropPlant getPlantFromStack(ItemStack stack) {
        if (isValidPlant(stack)) { //Is this a valid plant? If so it is safe to lookup.
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage());
        }
        else {
        	return null; //The plant was invalid.
        }
    }

    public static ArrayList<CropPlant> getPlants() {
        ArrayList<CropPlant> plants = new ArrayList<CropPlant>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            plants.addAll(subMap.values());
        }
        return plants;
    }

    public static ArrayList<CropPlant> getPlantsUpToTier(int tier) {
        ArrayList<CropPlant> list = new ArrayList<CropPlant>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            for(CropPlant plant:subMap.values()) {
                if(plant.getTier()<=tier) {
                    list.add(plant);
                }
            }
        }
        return list;
    }

    public static void init() {
    	
        //Register vanilla plants. Now with less duplication.
        OreDictionary.registerOre("seedMelon", Items.melon_seeds);
        OreDictionary.registerOre("cropMelon", Items.melon);
        OreDictionary.registerOre("seedPumpkin", Items.pumpkin_seeds);
        OreDictionary.registerOre("cropPumpkin", Blocks.pumpkin);
        
        suppressedRegisterPlant(new CropPlantVanilla((BlockCrops) net.minecraft.init.Blocks.wheat, (ItemSeeds) net.minecraft.init.Items.wheat_seeds));
        suppressedRegisterPlant(new CropPlantStem((ItemSeeds) Items.melon_seeds, Blocks.melon_block));
        suppressedRegisterPlant(new CropPlantStem((ItemSeeds) Items.pumpkin_seeds, Blocks.pumpkin));
        suppressedRegisterPlant(new CropPlantNetherWart());

        //Register mod crops
        ModHelper.initModPlants();
        
        //register crops specified trough the API
        for (CropPlant plant : plantsToRegister) {
            try {
                registerPlant(plant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plantsToRegister = null;
        
        //register others from ore dictionary
        ArrayList<ItemStack> seeds = OreDictionary.getOres(Names.OreDict.listAllseed);
        for (ItemStack seed : seeds) {
            if (!isValidSeed(seed) && (seed.getItem() instanceof ItemSeeds)) {
            	ArrayList<ItemStack> fruits = OreDictHelper.getFruitsFromOreDict(seed);
                if (fruits != null && fruits.size() > 0) {
                	suppressedRegisterPlant(new CropPlantOreDict((ItemSeeds)seed.getItem()));
                }
            }
        }
    }
}
