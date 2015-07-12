package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public abstract class SeedHelper {
    private static List<ItemStack> seedBlackList = new ArrayList<ItemStack>();
    private static HashMap<Item, Integer[]> spreadChances;
    private static HashMap<Item, Integer[]> seedTiers;
    private static List<ItemStack> vanillaPlantingOverrides = new ArrayList<ItemStack>();

    public static void init() {
        initSeedBlackList();
        initSpreadChancesOverrides();
        initTiers();
        initVannilaPlantingOverrides();
    }

    private static void initSeedBlackList() {
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSeedBlackList());
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(String line:data) {
            LogHelper.debug(new StringBuffer("parsing ").append(line));
            ItemStack seedStack = IOHelper.getStack(line);
            Item seed = seedStack!=null?seedStack.getItem():null;
            boolean success = seed!=null;
            String errorMsg = "Invalid seed";
            if(success) {
                list.add(seedStack);
            }
            else {
                LogHelper.info(new StringBuffer("Error when adding seed to blacklist: ").append(errorMsg).append(" (line: ").append(line).append(")"));
            }
        }
        seedBlackList = list;
        LogHelper.info("Registered seeds blacklist:");
        for(ItemStack seed:seedBlackList) {
            LogHelper.info(new StringBuffer(" - ").append(Item.itemRegistry.getNameForObject(seed.getItem())).append(":").append(seed.getItemDamage()));
        }
    }

    private static void initSpreadChancesOverrides() {
        //read mutation chance overrides & initialize the arrays
        setMutationChances(IOHelper.getLinesArrayFromData(ConfigurationHandler.readSpreadChances()));
        LogHelper.info("Registered Mutations Chances overrides:");
        for(Map.Entry<Item, Integer[]> entry:spreadChances.entrySet()) {
            for(int i=0;i<entry.getValue().length;i++) {
                Integer chance = entry.getValue()[i];
                if(chance!=null) {
                    StringBuffer override = new StringBuffer(" - ").append(Item.itemRegistry.getNameForObject(entry.getKey())).append(':').append(i).append(" - ").append(chance).append(" percent");
                    LogHelper.info(override);
                }
            }
        }
    }

    private static void initTiers() {
        setSeedTiers(IOHelper.getLinesArrayFromData(ConfigurationHandler.readSeedTiers()));
        LogHelper.info("Registered seed tiers:");
        for(Map.Entry<Item, Integer[]> entry:seedTiers.entrySet()) {
            for(int i=0;i<entry.getValue().length;i++) {
                Integer tier = entry.getValue()[i];
                if(tier!=null) {
                    StringBuffer override = new StringBuffer(" - ").append(Item.itemRegistry.getNameForObject(entry.getKey())).append(':').append(i).append(" - tier:").append(tier);
                    LogHelper.info(override);
                }
            }
        }
    }

    private static void initVannilaPlantingOverrides() {
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readVanillaOverrides());
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(String line:data) {
            LogHelper.debug(new StringBuffer("parsing ").append(line));
            ItemStack seedStack = IOHelper.getStack(line);
            Item seed = seedStack!=null?seedStack.getItem():null;
            boolean success = seed!=null;
            String errorMsg = "Invalid seed";
            if(success) {
                list.add(seedStack);
            }
            else {
                LogHelper.info(new StringBuffer("Error when adding seed to vanilla overrides: ").append(errorMsg).append(" (line: ").append(line).append(")"));
            }
        }
        vanillaPlantingOverrides = list;
        LogHelper.info("Registered seeds ignoring vanilla planting rule:");
        for(ItemStack seed:seedBlackList) {
            LogHelper.info(new StringBuffer(" - ").append(Item.itemRegistry.getNameForObject(seed.getItem())).append(":").append(seed.getItemDamage()));
        }

    }

    //initializes the mutation chances overrides
    private static void setMutationChances(String[] input) {
        spreadChances = new HashMap<Item, Integer[]>();
        LogHelper.debug("reading mutation chance overrides");
        for(String line:input) {
            String[] data = IOHelper.getData(line);
            boolean success = data.length==2;
            String errorMsg = "Incorrect amount of arguments";
            LogHelper.debug("parsing "+line);
            if(success) {
                ItemStack seedStack = IOHelper.getStack(data[0]);
                Item seedItem = seedStack!=null?seedStack.getItem():null;
                success = seedItem!=null;
                errorMsg = "Invalid seed";
                if(success) {
                    int chance = Integer.parseInt(data[1]);
                    success = chance>=0 && chance<=100;
                    errorMsg = "Chance should be between 0 and 100";
                    if(success) {
                        Item seed = seedStack.getItem();
                        if(spreadChances.get(seed)==null) {
                            spreadChances.put(seed, new Integer[16]);
                        }
                        spreadChances.get(seed)[seedStack.getItemDamage()] = chance;
                    }
                }
            }
            if(!success) {
                LogHelper.info(new StringBuffer("Error when adding mutation chance override: ").append(errorMsg).append(" (line: ").append(line).append(")"));
            }
        }
    }

    //initializes the seed tier overrides
    private static void setSeedTiers(String[] input) {
        seedTiers = new HashMap<Item, Integer[]>();
        LogHelper.debug("reading seed tier overrides");
        for(String line:input) {
            String[] data = IOHelper.getData(line);
            boolean success = data.length==2;
            String errorMsg = "Incorrect amount of arguments";
            LogHelper.debug("parsing "+line);
            if(success) {
                ItemStack seedStack = IOHelper.getStack(data[0]);
                Item seedItem = seedStack!=null?seedStack.getItem():null;
                success = seedItem!=null;
                errorMsg = "Invalid seed";
                if(success) {
                    int tier = Integer.parseInt(data[1]);
                    success = tier>=1 && tier<=5;
                    errorMsg = "Chance should be between 1 and 5";
                    if(success) {
                        Item seed = seedStack.getItem();
                        if(seedTiers.get(seed)==null) {
                            seedTiers.put(seed, new Integer[16]);
                        }
                        seedTiers.get(seed)[seedStack.getItemDamage()] = tier;
                    }
                }
            }
            if(!success) {
                LogHelper.info(new StringBuffer("Error when adding seed tier override: ").append(errorMsg).append(" (line: ").append(line).append(")"));
            }
        }
    }

    public static double getSpreadChance(Item seed, int meta) {
        Integer[] value = spreadChances.get(seed);
        if(value!=null && value.length>meta && value[meta]!=null) {
            return ((double) value[meta]) / 100;
        }
        CropPlant plant = CropPlantHandler.getPlantFromStack(new ItemStack(seed, 1, meta));
        if(plant==null) {
            return 0;
        }
        return 1.00/plant.getTier();
    }

    public static int getSeedTierOverride(ItemStack stack) {
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        Integer[] tierArray = seedTiers.get(seed);
        if(tierArray!=null && tierArray.length>meta) {
            Integer tier = seedTiers.get(seed)[meta];
            if (tier != null) {
                return tier;
            }
        }
        return -1;
    }

    public static boolean isAnalyzedSeed(ItemStack seedStack) {
        return CropPlantHandler.isValidSeed(seedStack) && (seedStack.hasTagCompound()) && (seedStack.stackTagCompound.hasKey(Names.NBT.analyzed)) && (seedStack.stackTagCompound.getBoolean(Names.NBT.analyzed));
    }

    public static boolean isSeedBlackListed(ItemStack stack) {
        if(stack==null || stack.getItem()==null) {
            return true;
        }
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        for(ItemStack blacklistedSeed:seedBlackList) {
            if(blacklistedSeed.getItem()==seed && blacklistedSeed.getItemDamage()==meta) {
                return true;
            }
        }
        return false;
    }

    private static boolean ignoresVanillaPlantingSetting(ItemStack seed) {
        for(ItemStack stack:vanillaPlantingOverrides) {
            if(stack.getItem() == seed.getItem() && stack.getItemDamage() == seed.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    public static boolean allowVanillaPlanting(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return false;
        }
        if(ConfigurationHandler.disableVanillaFarming) {
            if(ignoresVanillaPlantingSetting(seed)) {
                return true;
            }
            if(CropPlantHandler.isValidSeed(seed)) {
                return false;
            }
            if(seed.getItem() == Items.potato) {
                return false;
            }
            if(seed.getItem() == Items.carrot) {
                return false;
            }
            if(seed.getItem() == Items.reeds) {
                return false;
            }
        }
        return true;
    }

    //define NBT tag
    public static NBTTagCompound setNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        tag.setShort(Names.NBT.growth, growth==0?Constants.defaultGrowth:growth>10?10:growth);
        tag.setShort(Names.NBT.gain, gain==0?Constants.defaultGain:gain>10?10:gain);
        tag.setShort(Names.NBT.strength, strength==0?Constants.defaultGain:strength>10?10:strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
        return tag;
    }

    //get a random seed
    public static ItemStack getRandomSeed(Random rand, boolean setTag) {
        return getRandomSeed(rand, setTag, 5);
    }

    public static ItemStack getRandomSeed(Random rand, boolean setTag, int maxTier) {
        ArrayList<CropPlant> plants = CropPlantHandler.getPlants();
        boolean flag = false;
        ItemStack seed = null;
        while(!flag) {
            CropPlant plant = plants.get(rand.nextInt(plants.size()));
            seed = plant.getSeed().copy();
            flag = (seed.getItem()!=null) && plant.getTier()<=maxTier;
        }
        if(setTag) {
            NBTTagCompound tag = new NBTTagCompound();
            setNBT(tag, (short) (rand.nextInt(ConfigurationHandler.cropStatCap)/2 + 1), (short) (rand.nextInt(ConfigurationHandler.cropStatCap)/2 + 1), (short) (rand.nextInt(ConfigurationHandler.cropStatCap)/2 + 1), false);
            seed.stackTagCompound = tag;
        }
        return seed;
    }

    public static void addAllToSeedBlacklist(Collection<? extends ItemStack> seeds) {
        seedBlackList.addAll(seeds);
    }

    public static void removeAllFromSeedBlacklist(Collection<? extends ItemStack> seeds) {
        seedBlackList.removeAll(seeds);
    }

    /** @return The previous spread chance of the given seed */
    public static int overrideSpreadChance(Item seed, int meta, int chance) {
        int oldChance = (int) (getSpreadChance(seed, meta) * 100);
        Integer[] chances = spreadChances.get(seed);
        if (chances == null) {
            chances = new Integer[16];
            spreadChances.put(seed, chances);
        }
        chances[meta] = chance;
        return oldChance;
    }
}
