package com.infinityraider.agricraft.farming;

import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.ItemWithMeta;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.farming.cropplant.*;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.IOHelper;
import com.infinityraider.agricraft.utility.LogHelper;
import com.infinityraider.agricraft.utility.OreDictHelper;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.stream.Collectors;

public class CropPlantHandler {
    /** None object to avoid NPE's with block states */
    public static final CropPlant NONE = CropPlantNone.NONE;
    /** HashMap containing all plants known to AgriCraft */
    private static HashMap<Item, HashMap<Integer, CropPlant>> cropPlants = new HashMap<>();
    /** Queue to store plants registered via the API before the cropPlants HashMap has been initialized */
    private static ArrayList<CropPlant> plantsToRegister = new ArrayList<>();
    /** Queue to store BlackListed seeds which are not recognized as seeds by agricraft */
    private static ArrayList<ItemStack> blacklist = new ArrayList<>();

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
            entryForSeed = new HashMap<>();
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
    }

    /**
     * Sets the GROWTH requirement for a SEED, effectively overriding the previously registered GROWTH requirement
     * @param seed The SEED for which to set the GROWTH requirement
     * @param req The GROWTH requirement to be set
     * @return if the GROWTH requirement was successfully set
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
        return isRecognizedByAgriCraft(seed, meta) && !cropPlants.get(seed).get(meta).isBlackListed();
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
    public static NBTTagCompound writePlantToNBT(CropPlant plant) {
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
    public static CropPlant readPlantFromNBT(NBTTagCompound tag) {
        return getPlantFromStack(ItemStack.loadItemStackFromNBT(tag));
    }

    /**
     * Retrieves the {@link #cropPlants plant} from a stack.
     * 
     * @param stack the stack (possibly) containing the SEED to retrieve.
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
        ArrayList<CropPlant> plants = new ArrayList<>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            plants.addAll(subMap.values().stream().filter(plant -> !plant.isBlackListed()).collect(Collectors.toList()));
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
        ArrayList<CropPlant> plants = new ArrayList<>();
        for(HashMap<Integer, CropPlant> subMap:cropPlants.values()) {
            plants.addAll(subMap.values().stream().filter(plant -> plant.getTier() <= tier && !plant.isBlackListed()).collect(Collectors.toList()));
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

    /**
     * Checks if a SEED is BlackListed
     * @param seed the SEED to check
     * @return if the SEED is blacklisted and should not be plantable on crop sticks
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
     * Adds a SEED to the blacklist
     * @param seed the SEED to add to the blacklist
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
        seeds.forEach(CropPlantHandler::addSeedToBlackList);
    }

    /**
     * Removes a SEED from the blacklist
     * @param seed the SEED to be removed from the blacklist
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

    /** Removes a SEED from the blacklist array */
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
        seeds.forEach(CropPlantHandler::removeFromSeedBlackList);
    }

    public static List<Mutation> getDefaultMutations() {
        List<Mutation> list = new ArrayList<>();
        for(CropPlant plant : getPlants()) {
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
        OreDictionary.registerOre("seedMelon", Items.melon_seeds);
        OreDictionary.registerOre("cropMelon", Items.melon);
        OreDictionary.registerOre("seedPumpkin", Items.pumpkin_seeds);
        OreDictionary.registerOre("cropPumpkin", Blocks.pumpkin);
        
        suppressedRegisterPlant(new CropPlantVanilla((BlockCrops) net.minecraft.init.Blocks.wheat, (ItemSeeds) net.minecraft.init.Items.wheat_seeds, "wheat"));
        suppressedRegisterPlant(new CropPlantStem((ItemSeeds) Items.melon_seeds, Blocks.melon_block));
        suppressedRegisterPlant(new CropPlantStem((ItemSeeds) Items.pumpkin_seeds, Blocks.pumpkin));
        suppressedRegisterPlant(new CropPlantNetherWart());

        //Register crops specified through the API.
        plantsToRegister.forEach(CropPlantHandler::suppressedRegisterPlant);
        plantsToRegister = null;

        //Register mod crops.
        CompatibilityHandler.getInstance().getCropPlants().forEach(CropPlantHandler::suppressedRegisterPlant);
        
        //Register crops found in the ore dictionary.
        List<ItemStack> seeds = OreDictionary.getOres("listAllseed");
        seeds.stream().filter(seed -> !isValidSeed(seed) && (seed.getItem() instanceof ItemSeeds)).forEach(seed -> {
            ArrayList<ItemStack> fruits = OreDictHelper.getFruitsFromOreDict(seed);
            if (fruits != null && fruits.size() > 0) {
                suppressedRegisterPlant(new CropPlantOreDict((ItemSeeds) seed.getItem()));
            }
        });

        //Set tier overrides
        IOHelper.initSeedTiers();

        //Initialize SEED blacklist
        IOHelper.initSeedBlackList();

        //Set spread chance overrides
        IOHelper.initSpreadChancesOverrides();

        //Set vanilla planting rule overrides
        IOHelper.initVannilaPlantingOverrides();
    }
}
