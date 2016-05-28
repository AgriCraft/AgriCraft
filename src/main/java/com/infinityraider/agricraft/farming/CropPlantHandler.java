package com.infinityraider.agricraft.farming;

import com.infinityraider.agricraft.api.v1.ItemWithMeta;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.farming.cropplant.*;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.IOHelper;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;
import java.util.stream.Collectors;

public class CropPlantHandler {
    /** None object to avoid NPE's with block states */
    public static final ICropPlant NONE = CropPlantNone.NONE;
    /** HashMap containing all plants known to AgriCraft */
    private static HashMap<Item, HashMap<Integer, ICropPlant>> cropPlants = new HashMap<>();
    /** Queue to store plants registered via the API before the cropPlants HashMap has been initialized */
    private static ArrayList<ICropPlant> plantsToRegister = new ArrayList<>();
    /** Queue to store BlackListed seeds which are not recognized as seeds by agricraft */
    private static ArrayList<ItemStack> blacklist = new ArrayList<>();
    
    /**
     * Registers the plant into the cropPlants HashMap.
     * 
     * @param plant the plant to be registered.
     * @throws DuplicateCropPlantException thrown if the plant has already been registered. This could signal a major issue.
     */
    public static void registerPlant(ICropPlant plant) throws DuplicateCropPlantException {
        ItemStack stack = plant.getSeed();
        AgriCore.getLogger("AgriCraft").debug("Registering plant for " + stack.getUnlocalizedName());
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        HashMap<Integer, ICropPlant> entryForSeed = cropPlants.get(seed);
        if(entryForSeed!=null) {
            if(entryForSeed.get(meta)!=null) {
                throw new DuplicateCropPlantException();
            }
            else {
                entryForSeed.put(meta, plant);
            }
        }
        else {
            entryForSeed = new HashMap<>();
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
    private static void suppressedRegisterPlant(ICropPlant plant) {
    	try {
    		registerPlant(plant);
            GrowthRequirementHandler.addSoil(plant.getGrowthRequirement().getSoil());
    	} catch (DuplicateCropPlantException e) {
    		AgriCore.getLogger("AgriCraft").debug("Unable to register duplicate plant: " + plant.getSeed().getUnlocalizedName());
    		AgriCore.getLogger("AgriCraft").trace(e);
    	}
    }

    /**
     * Adds a crop to the registration queue, to be registered during the initialization phase.
     * @param plant the plant to be registered.
     */
    public static void addCropToRegister(ICropPlant plant) {
        if(plantsToRegister != null) {
            plantsToRegister.add(plant);
        }
    }

    public static boolean isAnalyzedSeed(ItemStack seedStack) {
        return isValidSeed(seedStack)
                && (seedStack.hasTagCompound())
                && (seedStack.getTagCompound().hasKey(AgriCraftNBT.ANALYZED))
                && (seedStack.getTagCompound().getBoolean(AgriCraftNBT.ANALYZED));
    }
    
    /**
     * Tests to see if the provided stack is a valid {@link #cropPlants SEED}.
     * @param seed the stack to test as a SEED.
     * @return if the stack is a valid SEED.
     */
    public static boolean isValidSeed(ItemStack seed) {
        return (seed != null) && (seed.getItem() != null) && isValidSeed(seed.getItem(), seed.getItemDamage());
    }

    /**
     * Tests to see if the provided ItemWithMeta is a valid {@link #cropPlants SEED}.
     * @param seed the item to test as a SEED.
     * @return if the item is a valid SEED.
     */
    public static boolean isValidSeed(ItemWithMeta seed) {
        return (seed !=null) && isValidSeed(seed.getItem(), seed.getMeta());
    }

    /**
     * Tests to see if the provided stack is a valid {@link #cropPlants SEED}, this takes the blacklist into account.
     * @param seed the item to test as a SEED.
     * @param meta the META for the SEED
     * @return if the item is a valid SEED.
     */
    public static boolean isValidSeed(Item seed, int meta) {
        return isRecognizedByAgriCraft(seed, meta);
    }

    /**
     * Checks if the SEED is recognized by AgriCraft, this does not take into account if the SEED is blacklisted or not
     * @param seed the item to test as a SEED.
     * @param meta the META for the SEED
     * @return if the item is recognized as a SEED
     */
    private static boolean isRecognizedByAgriCraft(Item seed, int meta) {
        return (seed != null) && cropPlants.containsKey(seed) && cropPlants.get(seed).containsKey(meta);
    }

    /**
     * Writes the plant (or SEED) to an NBTTag.
     * 
     * @see #readPlantFromNBT(NBTTagCompound)
     * 
     * @param plant the plant (or SEED) to write to an NBTTag.
     * @return a NBTTagCompound, the serialized representation of the plant.
     */
    public static NBTTagCompound writePlantToNBT(ICropPlant plant) {
        return plant.getSeed().writeToNBT(new NBTTagCompound());
    }

    /**
     * Reads a plant (a.k.a SEED) from an NBTTag.
     * 
     * @see #writePlantToNBT(CropPlant)
     * 
     * @param tag the serialized version of the plant.
     * @return the deserialized plant.
     */
    public static ICropPlant readPlantFromNBT(NBTTagCompound tag) {
        return getPlantFromStack(ItemStack.loadItemStackFromNBT(tag));
    }

    /**
     * Retrieves the {@link #cropPlants plant} from a stack.
     * 
     * @param stack the stack (possibly) containing the SEED to retrieve.
     * @return the plant in the stack, or null, if the stack does not contain a valid plant. 
     */
    public static ICropPlant getPlantFromStack(ItemStack stack) {
        if (isValidSeed(stack)) {
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage());
        }
        else {
        	return null;
        }
    }

    public static IGrowthRequirement getGrowthRequirement(Item seed, int meta) {
        ICropPlant plant = cropPlants.get(seed).get(meta);
        return plant==null? GrowthRequirementHandler.NULL:plant.getGrowthRequirement();

    }

    public static IGrowthRequirement getGrowthRequirement(ItemStack stack) {
        if(stack==null || stack.getItem()==null) {
            return GrowthRequirementHandler.NULL;
        }
        return getGrowthRequirement(stack.getItem(), stack.getItemDamage());
    }

    /**
     * Retrieves a list of registered plants.
     * 
     * @return the registered plants, taken from the internal HashMap, and placed into an ArrayList.
     */
    public static ArrayList<ICropPlant> getPlants() {
        ArrayList<ICropPlant> plants = new ArrayList<>();
        for(HashMap<Integer, ICropPlant> subMap : cropPlants.values()) {
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
    public static ArrayList<ICropPlant> getPlantsUpToTier(int tier) {
        ArrayList<ICropPlant> plants = new ArrayList<>();
        for(HashMap<Integer, ICropPlant> subMap:cropPlants.values()) {
            plants.addAll(subMap.values().stream().filter(plant -> plant.getTier() <= tier).collect(Collectors.toList()));
        }
        return plants;
    }

    /**
     * Gets a random SEED which is recognized as a plant by AgriCraft
     * @param rand Random object to be used
     * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG containing random stats
     * @return an ItemStack containing a random SEED
     */
    public static ItemStack getRandomSeed(Random rand, boolean setTag) {
        return getRandomSeed(rand, setTag, 5);
    }

    /**
     * Gets a random SEED which is recognized as a plant by AgriCraft
     * @param rand Random object to be used
     * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG containing random stats
     * @param maxTier The maximum tier of the SEED (inclusive)
     * @return an ItemStack containing a random SEED
     */
    public static ItemStack getRandomSeed(Random rand, boolean setTag, int maxTier) {
        return getRandomSeed(rand, setTag, CropPlantHandler.getPlantsUpToTier(maxTier));
    }

    /**
     * Gets a random SEED from a list of plants
     * @param rand Random object to be used
     * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG containing random stats
     * @param plants List of plants to grab a random SEED from
     * @return an ItemStack containing a random SEED
     */
    public static ItemStack getRandomSeed(Random rand, boolean setTag, List<ICropPlant> plants) {
        boolean flag = false;
        ItemStack seed = null;
        while(!flag) {
            ICropPlant plant = plants.get(rand.nextInt(plants.size()));
            seed = plant.getSeed().copy();
            flag = (seed.getItem()!=null);
        }
        if(setTag) {
            NBTTagCompound tag = new NBTTagCompound();
            setSeedNBT(tag, (short) (rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1), (short) (rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1), (short) (rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1), false);
            seed.setTagCompound(tag);
        }
        return seed;
    }

    /**
     * Sets the AgriCraftNBT TAG for a SEED to have stats, this method modifies the NBTTagCompound it is given to add the needed data and then returns it again
     * @param tag the AgriCraftNBT TAG of the ItemStack, is returned again
     * @param growth the GROWTH stat
     * @param gain the GAIN stat
     * @param strength the STRENGTH stat
     * @param analyzed if the SEED is ANALYZED
     * @return the AgriCraftNBT TAG
     */
    public static NBTTagCompound setSeedNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        short cap = (short) AgriCraftConfig.cropStatCap;
        tag.setShort(AgriCraftNBT.GROWTH, growth==0? Constants.DEFAULT_GROWTH:growth>cap?cap:growth);
        tag.setShort(AgriCraftNBT.GAIN, gain==0?Constants.DEFAULT_GAIN:gain>cap?cap:gain);
        tag.setShort(AgriCraftNBT.STRENGTH, strength==0?Constants.DEFAULT_GAIN:strength>cap?cap:strength);
        tag.setBoolean(AgriCraftNBT.ANALYZED, analyzed);
        return tag;
    }

    public static List<Mutation> getDefaultMutations() {
        List<Mutation> list = new ArrayList<>();
        for(ICropPlant plant : getPlants()) {
            list.addAll(plant.getDefaultMutations().stream().map(Mutation::new).collect(Collectors.toList()));
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

        //Register crops specified through the API.
        plantsToRegister.forEach(CropPlantHandler::suppressedRegisterPlant);
        plantsToRegister = null;

        //Register mod crops.
        CompatibilityHandler.getInstance().getCropPlants().forEach(CropPlantHandler::suppressedRegisterPlant);
        
        //Register crops found in the ore dictionary.
//        List<ItemStack> seeds = OreDictionary.getOres("listAllseed");
//        seeds.stream().filter(seed -> !isValidSeed(seed) && (seed.getItem() instanceof ItemSeeds)).forEach(seed -> {
//            ArrayList<ItemStack> fruits = OreDictHelper.getFruitsFromOreDict(seed);
//            if (fruits != null && fruits.size() > 0) {
//                suppressedRegisterPlant(new CropPlantOreDict((ItemSeeds) seed.getItem()));
//            }
//        });

        //Set spread chance overrides
        IOHelper.initSpreadChancesOverrides();

    }
}
