package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.ItemWithMeta;
import com.InfinityRaider.AgriCraft.farming.cropplant.*;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public class CropPlantHandler {
    /** HashMap containing all plants known to AgriCraft */
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<Item, HashMap<Integer, CropPlant>>();
    /** Queue to store plants registered via the API before the cropPlants HashMap has been initialized */
    private static ArrayList<CropPlant> plantsToRegister = new ArrayList<CropPlant>();
    /** Queue to store BlackListed seeds which are not recognized as seeds by agricraft */
    private static ArrayList<ItemStack> blacklist = new ArrayList<ItemStack>();

    /**
     * Registers the plant into the cropPlants HashMap.
     * 
     * This command is a wrapper for the registerPlant(CropPlant plant). The IAgriCraft plant is wrapped into a CropPlantAgriCraft in the process.
     * 
     * @see #registerPlant(CropPlant)
     * 
     * @param plant the plant to be registered.
     * @throws DuplicateCropPlantException thrown if the plant has already been registered. This could signal a major issue.
     */
    public static void registerPlant(IAgriCraftPlant plant) throws DuplicateCropPlantException {
        registerPlant(new CropPlantAgriCraft(plant));
    }
    
    /**
     * Registers the plant into the cropPlants HashMap.
     * 
     * @param plant the plant to be registered.
     * @throws DuplicateCropPlantException thrown if the plant has already been registered. This could signal a major issue.
     */
    public static void registerPlant(CropPlant plant) throws DuplicateCropPlantException {
        ItemStack stack = plant.getSeed();
        LogHelper.debug("Registering plant for " + stack.getUnlocalizedName());
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
        Iterator<ItemStack> it = blacklist.iterator();
        while(it.hasNext()) {
            ItemStack blackListed = it.next();
            //should never happen
            if(blackListed == null || blackListed.getItem() == null) {
                it.remove();
                continue;
            }
            if(blackListed.getItem() == stack.getItem() && blackListed.getItemDamage() == stack.getItemDamage()) {
                plant.setBlackListStatus(true);
                it.remove();
                break;
            }
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
            GrowthRequirementHandler.addSoil(plant.getGrowthRequirement().getSoil());
    	} catch (DuplicateCropPlantException e) {
    		LogHelper.debug("Unable to register duplicate plant: " + plant.getSeed().getUnlocalizedName());
    		LogHelper.printStackTrace(e);
    	}
    }

    /**
     * Adds a crop to the registration queue, to be registered during the initialization phase.
     * @param plant the plant to be registered.
     */
    public static void addCropToRegister(CropPlant plant) {
        if(plantsToRegister != null) {
            plantsToRegister.add(plant);
        }
        else {
            suppressedRegisterPlant(plant);
        }
    }

    /**
     * Sets the growth requirement for a seed, effectively overriding the previously registered growth requirement
     * @param seed The seed for which to set the growth requirement
     * @param req The growth requirement to be set
     * @return if the growth requirement was successfully set
     */
    public static boolean setGrowthRequirement(ItemWithMeta seed, IGrowthRequirement req) {
        if(seed == null || seed.getItem() == null) {
            return false;
        }
        if(!isValidSeed(seed)) {
            for(CropPlant plant:plantsToRegister) {
                ItemStack plantSeed = plant.getSeed();
                if(plantSeed == null || plantSeed.getItem() == null) {
                    continue;
                }
                if(plantSeed.getItem() == seed.getItem() && plantSeed.getItemDamage() == seed.getMeta()) {
                    plant.setGrowthRequirement(req);
                    return true;
                }
            }
            return false;
        }
        cropPlants.get(seed.getItem()).get(seed.getMeta()).setGrowthRequirement(req);
        return true;
    }

    public static boolean isAnalyzedSeed(ItemStack seedStack) {
        return isValidSeed(seedStack)
                && (seedStack.hasTagCompound())
                && (seedStack.stackTagCompound.hasKey(Names.NBT.analyzed))
                && (seedStack.stackTagCompound.getBoolean(Names.NBT.analyzed));
    }
    
    /**
     * Tests to see if the provided stack is a valid {@link #cropPlants seed}.
     * @param seed the stack to test as a seed.
     * @return if the stack is a valid seed.
     */
    public static boolean isValidSeed(ItemStack seed) {
        return (seed != null) && (seed.getItem() != null) && isValidSeed(seed.getItem(), seed.getItemDamage());
    }

    /**
     * Tests to see if the provided ItemWithMeta is a valid {@link #cropPlants seed}.
     * @param seed the item to test as a seed.
     * @return if the item is a valid seed.
     */
    public static boolean isValidSeed(ItemWithMeta seed) {
        return (seed !=null) && isValidSeed(seed.getItem(), seed.getMeta());
    }

    /**
     * Tests to see if the provided stack is a valid {@link #cropPlants seed}, this takes the blacklist into account.
     * @param seed the item to test as a seed.
     * @param meta the meta for the seed
     * @return if the item is a valid seed.
     */
    public static boolean isValidSeed(Item seed, int meta) {
        return isRecognizedByAgriCraft(seed, meta) && !cropPlants.get(seed).get(meta).isBlackListed();
    }

    /**
     * Checks if the seed is recognized by AgriCraft, this does not take into account if the seed is blacklisted or not
     * @param seed the item to test as a seed.
     * @param meta the meta for the seed
     * @return if the item is recognized as a seed
     */
    private static boolean isRecognizedByAgriCraft(Item seed, int meta) {
        return (seed != null) && cropPlants.containsKey(seed) && cropPlants.get(seed).containsKey(meta);
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
        if (isValidSeed(stack)) {
            return cropPlants.get(stack.getItem()).get(stack.getItemDamage());
        }
        else {
        	return null;
        }
    }

    public static IGrowthRequirement getGrowthRequirement(Item seed, int meta) {
        CropPlant plant = cropPlants.get(seed).get(meta);
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
    public static ArrayList<CropPlant> getPlants() {
        ArrayList<CropPlant> plants = new ArrayList<CropPlant>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            for(CropPlant plant : subMap.values()) {
                if(!plant.isBlackListed()) {
                    plants.add(plant);
                }
            }
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
        ArrayList<CropPlant> plants = new ArrayList<CropPlant>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            for(CropPlant plant : subMap.values()) {
                if(plant.getTier() <= tier && !plant.isBlackListed()) {
                    plants.add(plant);
                }
            }
        }
        return plants;
    }

    /**
     * Gets a random seed which is recognized as a plant by AgriCraft
     * @param rand Random object to be used
     * @param setTag If the seed should be initialized with an NBT tag containing random stats
     * @return an ItemStack containing a random seed
     */
    public static ItemStack getRandomSeed(Random rand, boolean setTag) {
        return getRandomSeed(rand, setTag, 5);
    }

    /**
     * Gets a random seed which is recognized as a plant by AgriCraft
     * @param rand Random object to be used
     * @param setTag If the seed should be initialized with an NBT tag containing random stats
     * @param maxTier The maximum tier of the seed (inclusive)
     * @return an ItemStack containing a random seed
     */
    public static ItemStack getRandomSeed(Random rand, boolean setTag, int maxTier) {
        return getRandomSeed(rand, setTag, CropPlantHandler.getPlantsUpToTier(maxTier));
    }

    /**
     * Gets a random seed from a list of plants
     * @param rand Random object to be used
     * @param setTag If the seed should be initialized with an NBT tag containing random stats
     * @param plants List of plants to grab a random seed from
     * @return an ItemStack containing a random seed
     */
    public static ItemStack getRandomSeed(Random rand, boolean setTag, List<CropPlant> plants) {
        boolean flag = false;
        ItemStack seed = null;
        while(!flag) {
            CropPlant plant = plants.get(rand.nextInt(plants.size()));
            seed = plant.getSeed().copy();
            flag = (seed.getItem()!=null);
        }
        if(setTag) {
            NBTTagCompound tag = new NBTTagCompound();
            setSeedNBT(tag, (short) (rand.nextInt(ConfigurationHandler.cropStatCap) / 2 + 1), (short) (rand.nextInt(ConfigurationHandler.cropStatCap) / 2 + 1), (short) (rand.nextInt(ConfigurationHandler.cropStatCap) / 2 + 1), false);
            seed.stackTagCompound = tag;
        }
        return seed;
    }

    /**
     * Sets the NBT tag for a seed to have stats, this method modifies the NBTTagCompound it is given to add the needed data and then returns it again
     * @param tag the NBT tag of the ItemStack, is returned again
     * @param growth the growth stat
     * @param gain the gain stat
     * @param strength the strength stat
     * @param analyzed if the seed is analyzed
     * @return the NBT tag
     */
    public static NBTTagCompound setSeedNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        short cap = (short) ConfigurationHandler.cropStatCap;
        tag.setShort(Names.NBT.growth, growth==0? Constants.DEFAULT_GROWTH:growth>cap?cap:growth);
        tag.setShort(Names.NBT.gain, gain==0?Constants.DEFAULT_GAIN:gain>cap?cap:gain);
        tag.setShort(Names.NBT.strength, strength==0?Constants.DEFAULT_GAIN:strength>cap?cap:strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
        return tag;
    }

    /**
     * Checks if a seed is BlackListed
     * @param seed the seed to check
     * @return if the seed is blacklisted and should not be plantable on crop sticks
     */
    public static boolean isSeedBlackListed(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return true;
        }
        if(isRecognizedByAgriCraft(seed.getItem(), seed.getItemDamage())) {
            return cropPlants.get(seed.getItem()).get(seed.getItemDamage()).isBlackListed();
        }
        for(ItemStack queued : blacklist) {
            if(queued.getItem() == seed.getItem() || queued.getItemDamage() == seed.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a seed to the blacklist
     * @param seed the seed to add to the blacklist
     */
    public static void addSeedToBlackList(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return;
        }
        if(isSeedBlackListed(seed)) {
            return;
        }
        if(!isRecognizedByAgriCraft(seed.getItem(), seed.getItemDamage())) {
            blacklist.add(seed.copy());
            return;
        }
        cropPlants.get(seed.getItem()).get(seed.getItemDamage()).setBlackListStatus(true);
        LogHelper.debug("Added seed to blacklist: " + Item.itemRegistry.getNameForObject(seed.getItem()) + ":" + seed.getItemDamage());
    }

    /**
     * Adds a collection of seeds to the blacklist
     * @param seeds collection containing all seeds to be added to the blacklist
     */
    public static void addAllToSeedBlacklist(Collection<? extends ItemStack> seeds) {
        for(ItemStack seed : seeds) {
            addSeedToBlackList(seed);
        }
    }

    /**
     * Removes a seed from the blacklist
     * @param seed the seed to be removed from the blacklist
     */
    public static void removeFromSeedBlackList(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return;
        }
        if(!isSeedBlackListed(seed)) {
            return;
        }
        if(isRecognizedByAgriCraft(seed.getItem(), seed.getItemDamage())) {
            cropPlants.get(seed.getItem()).get(seed.getItemDamage()).setBlackListStatus(false);
        }
        else {
            removeFromBlackListArray(seed);
        }
        LogHelper.debug("Removed seed from blacklist: " + Item.itemRegistry.getNameForObject(seed.getItem()) + ":" + seed.getItemDamage());
    }

    /** Removes a seed from the blacklist array */
    private static void removeFromBlackListArray(ItemStack seed) {
        Iterator<ItemStack> it = blacklist.iterator();
        while(it.hasNext()) {
            ItemStack queued = it.next();
            if(seed.getItem() == queued.getItem() && seed.getItemDamage() == queued.getItemDamage()) {
                it.remove();
            }
        }
    }

    /**
     * Removes a collection of seeds from the blacklist
     * @param seeds collection containing all seeds to be removed from the blacklist
     */
    public static void removeAllFromSeedBlacklist(Collection<? extends ItemStack> seeds) {
        for(ItemStack seed : seeds) {
            removeFromSeedBlackList(seed);
        }
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

        //Set tier overrides
        IOHelper.initSeedTiers();

        //Initialize seed blacklist
        IOHelper.initSeedBlackList();

        //Set spread chance overrides
        IOHelper.initSpreadChancesOverrides();

        //Set vanilla planting rule overrides
        IOHelper.initVannilaPlantingOverrides();
    }
}
