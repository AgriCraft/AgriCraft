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

public class CropPlantHandler {
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<Item, HashMap<Integer, CropPlant>>();
    private static ArrayList<CropPlant> plantsToRegister = new ArrayList<CropPlant>();

    /**
     * Registers the plant into the cropPlants HashMap.
     * 
     * This command is a wrapper for the registerPlant(CropPlant plant). The IAgriCraft plant is wrapped into a CropPlantAgriCraft in the process.
     * 
     * @see #registerPlant(CropPlant)
     * 
     * @param plant the plant to be registered.
     * @throws DuplicateCropPlantException thrown if the plant has already been registered. This could signal a major issue.
     * @throws BlacklistedCropPlantException thrown if the plant has been blacklisted in the configurations. This is is unlikely to be an issue.
     */
    public static void registerPlant(IAgriCraftPlant plant) throws DuplicateCropPlantException, BlacklistedCropPlantException {
        registerPlant(new CropPlantAgriCraft(plant));
    }
    
    /**
     * Registers the plant into the cropPlants HashMap.
     * 
     * @param plant the plant to be registered.
     * @throws DuplicateCropPlantException thrown if the plant has already been registered. This could signal a major issue.
     * @throws BlacklistedCropPlantException thrown if the plant has been blacklisted in the configurations. This is is unlikely to be an issue.
     */
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
    
    /**
     * Registers the plant into the cropPlants HashMap, and automatically catches DuplicateCropPlantExceptions and BlacklistedCropPlantExeptions.
     * 
     * This method replaces the many switch statements previously found in the init() function. In doing so we cut back on code duplication.
     * Furthermore, this is a targeted switch statement, instead of a catch all, allowing exceptions that should not have been thrown to surface properly.
     * 
     * This function is a wrapper for the registerPlant() function.
     * 
     * @see #registerPlant(CropPlant)
     * 
     * @param plant the plant to be registered.
     */
    private static void suppressedRegisterPlant(CropPlant plant) {
    	try {
    		registerPlant(plant);
    	} catch (DuplicateCropPlantException e) {
    		LogHelper.debug("Unable to register duplicate plant: " + plant.getSeed().getUnlocalizedName());
    		LogHelper.printStackTrace(e);
    	} catch (BlacklistedCropPlantException e) {
    		LogHelper.warn("Blacklisted plant " + plant.getSeed().getUnlocalizedName() + " was not registered.");
    	}
    }

    /**
     * Adds a crop to the registration queue, to be registered during the initialization phase.
     * @param plant the plant to be registered.
     */
    public static void addCropToRegister(CropPlant plant) {
        plantsToRegister.add(plant);
    }

    /**
     * Tests to see if the provided stack is a valid {@link #cropPlants seed}.
     * Tentatively provides the same output as {@link #isValidPlant(ItemStack)}.
     * 
     * @see #isValidPlant(ItemStack)
     * 
     * @param stack the stack to test as a seed.
     * @return if the stack is a valid seed.
     */
    public static boolean isValidSeed(ItemStack stack) { //The method name should match the rest of the class.
    	if (stack != null && cropPlants.containsKey(stack.getItem())) { //Is the plant/seed in the registry? If the plant/seed is in the registry it is o.k. to get.
    		return cropPlants.get(stack.getItem()).containsKey(stack.getItemDamage()); // Is the damage value in the registry? If so, this is a valid seed. 
    	}
        return false; // Exceptions are very costly and should generally be avoided.
    }
    
    /**
     * Tests to see if the provided stack is a valid {@link #cropPlants plant}.
     * Tentatively provides the same output as {@link #isValidSeed(ItemStack)}.
     * 
     * @see #isValidPlant(ItemStack)
     * 
     * @param stack the stack to test as a seed.
     * @return if the stack is a valid seed.
     */
    public static boolean isValidPlant(ItemStack stack) { //The method name should match the rest of the class.
        if(stack==null || stack.getItem()==null) {
            return false;
        }
    	if (cropPlants.containsKey(stack.getItem())) { //Is the plant/seed in the registry? If the plant/seed is in the registry it is o.k. to get.
    		return cropPlants.get(stack.getItem()).containsKey(stack.getItemDamage()); // Is the damage value in the registry? If so, this is a valid seed. 
    	}
        return false; // Exceptions are very costly and should generally be avoided.
    }

    /**
     * Writes the plant (or seed) to an NBTTag.
     * 
     * @see #readPlantFromNBT(NBTTagCompound)
     * 
     * @param plant the plant (or seed) to write to an NBTTag.
     * @return a NBTTagCompound, the serialized representation of the plant.
     */
    public static NBTTagCompound writePlantToNBT(CropPlant plant) {
        return plant.getSeed().writeToNBT(new NBTTagCompound());
    }

    /**
     * Reads a plant (a.k.a seed) from an NBTTag.
     * 
     * @see #writePlantToNBT(CropPlant)
     * 
     * @param tag the serialized version of the plant.
     * @return the deserialized plant.
     */
    public static CropPlant readPlantFromNBT(NBTTagCompound tag) {
        return getPlantFromStack(ItemStack.loadItemStackFromNBT(tag));
    }

    /**
     * Retrieves the {@link #cropPlants plant} from a stack.
     * 
     * @param stack the stack (possibly) containing the seed to retrieve.
     * @return the plant in the stack, or null, if the stack does not contain a valid plant. 
     */
    public static CropPlant getPlantFromStack(ItemStack stack) {
        if (isValidPlant(stack)) { //Is this a valid plant? If so it is safe to lookup.
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage());
        }
        else {
        	return null; //The plant was invalid.
        }
    }

    /**
     * Retrieves a list of registered plants.
     * 
     * @return the registered plants, taken from the internal HashMap, and placed into an ArrayList.
     */
    public static ArrayList<CropPlant> getPlants() {
        ArrayList<CropPlant> plants = new ArrayList<CropPlant>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            plants.addAll(subMap.values());
        }
        return plants;
    }

    /**
     * Retrieves a list of registered plants, with tier equal to or below the provided tier.
     * 
     * @param tier the inclusive tier cap.
     * @return the registered plants within the provided range.
     */
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

    /**
     * The primary plant initialization function.
     * 
     * Begins by initializing vanilla plants, then moves to mod plants, followed by plants in the plantsToRegister queue.
     * Finally initializes plants found in the ore dictionary.
     */
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

        //Register mod crops.
        ModHelper.initModPlants();
        
        //Register crops specified through the API.
        for (CropPlant plant : plantsToRegister) {
        	suppressedRegisterPlant(plant);
        }
        plantsToRegister = null;
        
        //Register crops found in the ore dictionary.
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
