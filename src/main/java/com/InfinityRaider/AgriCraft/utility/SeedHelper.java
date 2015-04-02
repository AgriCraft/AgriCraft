package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

public abstract class SeedHelper {
    private static List<ItemStack> seedBlackList;
    private static HashMap<ItemSeeds, Integer[]> spreadChances;
    private static HashMap<ItemSeeds, Integer[]> seedTiers;

    public static void init() {
        initSeedBlackList();
        initSpreadChancesOverrides();
        initTiers();
    }

    private static void initSeedBlackList() {
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSeedBlackList());
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(String line:data) {
            LogHelper.debug(new StringBuffer("parsing ").append(line));
            ItemStack seedStack = IOHelper.getStack(line);
            Item seed = seedStack!=null?seedStack.getItem():null;
            boolean success = seed!=null && seed instanceof ItemSeeds;
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
        for(Map.Entry<ItemSeeds, Integer[]> entry:spreadChances.entrySet()) {
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
        for(Map.Entry<ItemSeeds, Integer[]> entry:seedTiers.entrySet()) {
            for(int i=0;i<entry.getValue().length;i++) {
                Integer tier = entry.getValue()[i];
                if(tier!=null) {
                    StringBuffer override = new StringBuffer(" - ").append(Item.itemRegistry.getNameForObject(entry.getKey())).append(':').append(i).append(" - tier:").append(tier);
                    LogHelper.info(override);
                }
            }
        }
    }

    //initializes the mutation chances overrides
    private static void setMutationChances(String[] input) {
        spreadChances = new HashMap<ItemSeeds, Integer[]>();
        LogHelper.debug("reading mutation chance overrides");
        for(String line:input) {
            String[] data = IOHelper.getData(line);
            boolean success = data.length==2;
            String errorMsg = "Incorrect amount of arguments";
            LogHelper.debug("parsing "+line);
            if(success) {
                ItemStack seedStack = IOHelper.getStack(data[0]);
                Item seedItem = seedStack!=null?seedStack.getItem():null;
                success = seedItem!=null && seedItem instanceof ItemSeeds;
                errorMsg = "Invalid seed";
                if(success) {
                    int chance = Integer.parseInt(data[1]);
                    success = chance>=0 && chance<=100;
                    errorMsg = "Chance should be between 0 and 100";
                    if(success) {
                        ItemSeeds seed = (ItemSeeds) seedStack.getItem();
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
        seedTiers = new HashMap<ItemSeeds, Integer[]>();
        LogHelper.debug("reading seed tier overrides");
        for(String line:input) {
            String[] data = IOHelper.getData(line);
            boolean success = data.length==2;
            String errorMsg = "Incorrect amount of arguments";
            LogHelper.debug("parsing "+line);
            if(success) {
                ItemStack seedStack = IOHelper.getStack(data[0]);
                Item seedItem = seedStack!=null?seedStack.getItem():null;
                success = seedItem!=null && seedItem instanceof ItemSeeds;
                errorMsg = "Invalid seed";
                if(success) {
                    int tier = Integer.parseInt(data[1]);
                    success = tier>=1 && tier<=5;
                    errorMsg = "Chance should be between 1 and 5";
                    if(success) {
                        ItemSeeds seed = (ItemSeeds) seedStack.getItem();
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

    public static double getSpreadChance(ItemSeeds seed, int meta) {
        Integer[] value = spreadChances.get(seed);
        if(value!=null && value.length>meta && value[meta]!=null) {
            return ((double) value[meta]) / 100;
        }
        return 1.00/ CropPlantHandler.getPlantFromStack(new ItemStack(seed, 1, meta)).getTier();
    }

    public static int getSeedTierOverride(ItemStack stack) {
        ItemSeeds seed = (ItemSeeds) stack.getItem();
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
        return (seedStack!=null) && (seedStack.getItem()!=null) && (seedStack.getItem() instanceof ItemSeeds) && (seedStack.hasTagCompound()) && (seedStack.stackTagCompound.hasKey(Names.NBT.analyzed)) && (seedStack.stackTagCompound.getBoolean(Names.NBT.analyzed));
    }

    public static boolean isSeedBlackListed(ItemStack stack) {
        Item seed = stack.getItem();
        int meta = stack.getItemDamage();
        for(ItemStack blacklistedSeed:seedBlackList) {
            if(blacklistedSeed.getItem()==seed && blacklistedSeed.getItemDamage()==meta) {
                return true;
            }
        }
        return false;
    }

    //define NBT tag
    public static void setNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        tag.setShort(Names.NBT.growth, growth==0?Constants.defaultGrowth:growth>10?10:growth);
        tag.setShort(Names.NBT.gain, gain==0?Constants.defaultGain:gain>10?10:gain);
        tag.setShort(Names.NBT.strength, strength==0?Constants.defaultGain:strength>10?10:strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
    }

    //get a random seed
    public static ItemStack getRandomSeed(boolean setTag) {
        ArrayList<ItemStack> seeds = OreDictionary.getOres(Names.OreDict.listAllseed);
        ItemStack seed = null;
        while(seed==null || !(seed.getItem() instanceof ItemSeeds) || !CropPlantHandler.isValidSeed(seed)) {
            seed = seeds.get((int) Math.floor(Math.random()*seeds.size()));
        }
        if(setTag) {
            int gain = (int) Math.ceil(Math.random()*7);
            int growth = (int) Math.ceil(Math.random()*7);
            int strength = (int) Math.ceil(Math.random()*7);
            NBTTagCompound tag = new NBTTagCompound();
            setNBT(tag, (short) growth, (short) gain, (short) strength, false);
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
    public static int overrideSpreadChance(ItemSeeds seed, int meta, int chance) {
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
