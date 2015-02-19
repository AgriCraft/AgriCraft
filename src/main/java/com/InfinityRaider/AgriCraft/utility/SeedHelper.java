package com.InfinityRaider.AgriCraft.utility;

import chococraft.common.items.seeds.ItemGysahlSeeds;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.chococraft.ChococraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.plantmegapack.PlantMegaPackHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.SeedInformation;
import mods.natura.common.NContent;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCrops;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
        return 1.00/ SeedHelper.getSeedTier(seed, meta);
    }

    public static int getSeedTier(ItemSeeds seed, int meta) {
        if(seed == null) {
            return 0;
        }
        Integer[] tierArray = seedTiers.get(seed);
        if(tierArray!=null && tierArray.length>meta) {
            Integer tier = seedTiers.get(seed)[meta];
            if (tier != null) {
                return tier;
            }
        }
        if(seed instanceof ItemModSeed) {
            return ((ItemModSeed) seed).getPlant().tier;
        }
        String domain = Item.itemRegistry.getNameForObject(seed).substring(0, Item.itemRegistry.getNameForObject(seed).indexOf(':'));
        if(domain.equalsIgnoreCase("harvestcraft")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("natura")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("magicalcrops")) {
            return 4;
        }
        if(domain.equalsIgnoreCase("plantmegapack")) {
            return 2;
        }
        if(domain.equalsIgnoreCase("weeeflowers")) {
            return 2;
        }
        return 1;
    }

    public static int getBaseGrowth(int tier) {
        switch(tier) {
            case 1: return Constants.growthTier1;
            case 2: return Constants.growthTier2;
            case 3: return Constants.growthTier3;
            case 4: return Constants.growthTier4;
            case 5: return Constants.growthTier5;
            default: return 0;
        }
    }

    //find the crop for a seed
    public static BlockBush getPlant(ItemSeeds seed) {
        if(seed == null) {
            return null;
        }
        else if(seed == Items.melon_seeds) {
            return Crops.melon;
        }
        else if(seed == Items.pumpkin_seeds) {
            return Crops.pumpkin;
        }
        else {
            if(seed.getPlant(null, 0, 0, 0) instanceof BlockCrops) {
                return (BlockCrops) seed.getPlant(null, 0, 0, 0);
            }
            else {
                return (BlockBush) seed.getPlant(null, 0, 0, 0);
            }
        }
    }

    public static boolean isAnalyzedSeed(ItemStack seedStack) {
        return (seedStack!=null) && (seedStack.getItem()!=null) && (seedStack.getItem() instanceof ItemSeeds) && (seedStack.hasTagCompound()) && (seedStack.stackTagCompound.hasKey(Names.NBT.analyzed)) && (seedStack.stackTagCompound.getBoolean(Names.NBT.analyzed));
    }

    //gets the seed domain
    public static String getPlantDomain(ItemSeeds seed) {
        String name = Item.itemRegistry.getNameForObject(seed);
        return name.substring(0, name.indexOf(":")).toLowerCase();
    }

    //gets the fruits
    public static ArrayList<ItemStack> getPlantFruits(ItemSeeds seed, World world, int x, int y, int z, int gain, int meta) {
        int nr = (int) (Math.ceil((gain + 0.00) / 3));
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        //agricraft seed
        if (seed instanceof ItemModSeed) {
            BlockModPlant plant = (BlockModPlant) getPlant(seed);
            items.addAll(plant.getFruit(nr, world.rand));
        }
        //chococraft seed
        else if (ModIntegration.LoadedMods.chococraft && seed instanceof ItemGysahlSeeds) {
            items.add(ChococraftHelper.getFruit(gain, nr));
        }
        //use oredict to get the drops
        else {
            int[] oreIDs = OreDictionary.getOreIDs(new ItemStack(seed, 1, meta));
            ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
            for (int id : oreIDs) {
                String seedOreName = OreDictionary.getOreName(id);
                LogHelper.debug("Getting fruits for oredict entry " + seedOreName);
                if (seedOreName.substring(0, 4).equals(Names.Objects.seed)) {
                    String fruitOreName = Names.Objects.crop + seedOreName.substring(4);
                    LogHelper.debug(fruitOreName);
                    fruits = OreDictionary.getOres(fruitOreName);
                    break;
                }
            }
            while (fruits.size() > 0 && nr > 0) {
                int index = world.rand.nextInt(fruits.size());
                ItemStack fruit = fruits.get(index);
                fruit.stackSize = 1;
                items.add(fruit);
                nr--;
            }

        }
        //didn't succeed to find fruits trough the ore dictionary
        if (!(items.size() > 0)) {
            LogHelper.debug("didn't find fruit trough ore dictionary");
            BlockBush plant = getPlant(seed);
            //nether wart exception
            if (plant == Blocks.nether_wart) {
                items.add(new ItemStack(seed, 1, 0));
            }
            //natura crop exception
            else if (ModIntegration.LoadedMods.natura && getPlantDomain(seed).equalsIgnoreCase("natura")) {
                items.add(new ItemStack(NContent.plantItem, nr, meta * 3));
            }
            //other crop exception
            else {
                int harvestMeta = 7;
                //plant mega pack crop
                if (ModIntegration.LoadedMods.plantMegaPack && getPlantDomain(seed).equalsIgnoreCase("plantmegapack")) {
                    harvestMeta = PlantMegaPackHelper.getTextureIndex(seed, harvestMeta);
                }
                //other crop
                ArrayList<ItemStack> defaultDrops = plant.getDrops(world, x, y, z, harvestMeta, 0);
                for (ItemStack drop : defaultDrops) {
                    if (!(drop.getItem() instanceof ItemSeeds) && drop.getItem() != null) {
                        boolean add = true;
                        for (ItemStack item : items) {
                            if (item.getItem() == drop.getItem() && item.getItemDamage() == drop.getItemDamage()) {
                                add = false;
                            }
                        }
                        if (add) {
                            items.add(new ItemStack(drop.getItem(), nr, drop.getItemDamage()));
                        }
                    }
                }
            }
        }
        return items;
    }

    //get all plant fruits
    public static ArrayList<ItemStack> getAllPlantFruits(ItemSeeds seed, World world, int x, int y, int z, int gain, int meta) {
        BlockBush plant = getPlant(seed);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if(plant instanceof BlockModPlant) {
            items.addAll(((BlockModPlant) plant).getFruits());
        }
        //chococraft crop
        else if(ModIntegration.LoadedMods.chococraft && seed instanceof ItemGysahlSeeds) {
            items.addAll(ChococraftHelper.getFruits());
        }
        //other crop
        else {
            int[] oreIDs = OreDictionary.getOreIDs(new ItemStack(seed, 1, meta));
            ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
            for(int id:oreIDs) {
                String seedOreName = OreDictionary.getOreName(id);
                if(seedOreName.substring(0,4).equals(Names.Objects.seed)) {
                    String fruitOreName = Names.Objects.crop + seedOreName.substring(4);
                    fruits = OreDictionary.getOres(fruitOreName);
                    break;
                }
            }
            if(fruits.size()>0) {
                items.addAll(fruits);
            }
        }
        if(!(items.size()>0)) {
            items = getPlantFruits(seed, world, x, y, z, gain, meta);
        }
        return items;
    }

    //check if the seed is valid
    public static boolean isValidSeed(ItemSeeds seed, int meta) {
        if(ModIntegration.LoadedMods.thaumicTinkerer && getPlantDomain(seed).equalsIgnoreCase(Names.Mods.thaumicTinkerer)) {
            LogHelper.debug("Thaumic Tinkerer infused seeds are not supported, sorry");
            return false;
        }
        for(ItemStack blacklistedSeed:seedBlackList) {
            if(blacklistedSeed.getItem()==seed && blacklistedSeed.getItemDamage()==meta) {
                return false;
            }
        }
        return true;
    }

    //get the base growth
    public static int getBaseGrowth(ItemSeeds seed, int meta) {
        return getBaseGrowth(getSeedTier(seed, meta));
    }

    //define NBT tag
    public static void setNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
        tag.setShort(Names.NBT.growth, growth==0?Constants.defaultGrowth:growth>10?10:growth);
        tag.setShort(Names.NBT.gain, gain==0?Constants.defaultGain:gain>10?10:gain);
        tag.setShort(Names.NBT.strength, strength==0?Constants.defaultGain:strength>10?10:strength);
        tag.setBoolean(Names.NBT.analyzed, analyzed);
    }

    //get a string of information about the seed for the journal
    public static String getSeedInformation(ItemStack seedStack) {
        if (!(seedStack.getItem() instanceof ItemSeeds)) {
            return null;
        }
        return SeedInformation.getSeedInformation(seedStack);
    }

    //get a random seed
    public static ItemStack getRandomSeed(boolean setTag) {
        ArrayList<ItemStack> seeds = OreDictionary.getOres(Names.OreDict.listAllseed);
        ItemStack seed = null;
        while(seed==null || !(seed.getItem() instanceof ItemSeeds) || !isValidSeed((ItemSeeds) seed.getItem(), seed.getItemDamage())) {
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
