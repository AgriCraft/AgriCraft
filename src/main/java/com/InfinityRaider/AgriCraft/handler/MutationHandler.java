package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public abstract class MutationHandler {
    private static ItemStack[] mutations;
    private static ItemStack[] parents1;
    private static ItemStack[] parents2;
    private static ItemStack[] mutationChanceOverrides;
    private static int[] mutationChances;

    public static void init() {
        //Read mutations & initialize the mutation arrays
        setMutations(IOHelper.getLinesArrayFromData(ConfigurationHandler.readMutationData()));

        LogHelper.info("Registered Mutations:");
        for(int i=0;i< mutations.length;i++) {
            String mutation = mutations[i].getItem()!=null?(Item.itemRegistry.getNameForObject(mutations[i].getItem())+':'+mutations[i].getItemDamage()):"null";
            String parent1 = parents1[i].getItem()!=null?(Item.itemRegistry.getNameForObject(parents1[i].getItem()))+':'+parents1[i].getItemDamage():"null";
            String parent2 = parents2[i].getItem()!=null?(Item.itemRegistry.getNameForObject(parents2[i].getItem()))+':'+parents2[i].getItemDamage():"null";
            LogHelper.info(" - "+mutation + " = " + parent1 + " + " + parent2);
        }

        //read mutation chance overrides & initialize the arrays
        setMutationChances(IOHelper.getLinesArrayFromData(ConfigurationHandler.readMutationChances()));
        LogHelper.info("Registered Mutations Chances overrides:");
        for(int i=0;i< mutationChances.length;i++) {
            String mutation = mutationChanceOverrides[i].getItem()!=null?(Item.itemRegistry.getNameForObject(mutationChanceOverrides[i].getItem())+':'+mutationChanceOverrides[i].getItemDamage()):"null";
            String chance = mutationChances[i]+" percent";
            LogHelper.info(" - "+mutation + ": " + chance);
        }
    }

    //initializes the mutations arrays
    private static void setMutations(String[] data) {
        mutations = new ItemStack[data.length];
        parents1 = new ItemStack[data.length];
        parents2 = new ItemStack[data.length];
        for(int i=0;i<data.length;i++) {
            mutations[i] = IOHelper.getSeedStack(IOHelper.correctSeedName(data[i].substring(0,data[i].indexOf('='))));
            parents1[i] = IOHelper.getSeedStack(IOHelper.correctSeedName(data[i].substring(data[i].indexOf('=')+1,data[i].indexOf('+'))));
            parents2[i] = IOHelper.getSeedStack(IOHelper.correctSeedName(data[i].substring(data[i].indexOf('+')+1)));
        }
    }

    //initializes the mutation chances arrays
    private static void setMutationChances(String[] data) {
        mutationChanceOverrides = new ItemStack[data.length];
        mutationChances = new int[data.length];
        for(int i=0;i<data.length;i++) {
            mutationChanceOverrides[i] = IOHelper.getSeedStack(IOHelper.correctSeedName(data[i].substring(0,data[i].indexOf(','))));
            int chance = Integer.parseInt(data[i].substring(data[i].indexOf(',')+1));
            mutationChances[i] = chance<0?0:(chance>100?100:chance);
        }
    }

    //gets all the possible crossovers
    public static ItemStack[] getCrossOvers(TileEntityCrop[] crops) {
        TileEntityCrop[] parents = MutationHandler.getParents(crops);
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        switch (parents.length) {
            case 2:
                list.addAll(MutationHandler.getMutation(parents[0], parents[1]));
                break;
            case 3:
                list.addAll(MutationHandler.getMutation(parents[0], parents[1]));
                list.addAll(MutationHandler.getMutation(parents[0], parents[2]));
                list.addAll(MutationHandler.getMutation(parents[1], parents[2]));
                break;
            case 4:
                list.addAll(MutationHandler.getMutation(parents[0], parents[1]));
                list.addAll(MutationHandler.getMutation(parents[0], parents[2]));
                list.addAll(MutationHandler.getMutation(parents[0], parents[3]));
                list.addAll(MutationHandler.getMutation(parents[1], parents[2]));
                list.addAll(MutationHandler.getMutation(parents[1], parents[3]));
                list.addAll(MutationHandler.getMutation(parents[2], parents[3]));
                break;
        }
        return cleanItemStackArray(list.toArray(new ItemStack[list.size()]));
    }

    //gets an array of all the possible parents from the array containing all the neighbouring crops
    private static TileEntityCrop[] getParents(TileEntityCrop[] input) {
        ArrayList<TileEntityCrop> list = new ArrayList<TileEntityCrop>();
        for(TileEntityCrop crop:input) {
            if (crop != null && crop.isMature()) {
                list.add(crop);
            }
        }
        return list.toArray(new TileEntityCrop[list.size()]);
    }

    //finds the product of two parents
    private static ArrayList<ItemStack> getMutation(TileEntityCrop parent1, TileEntityCrop parent2) {
        ItemSeeds seed1 = (ItemSeeds) parent1.seed;
        ItemSeeds seed2 = (ItemSeeds) parent2.seed;
        int meta1 = parent1.seedMeta;
        int meta2 = parent2.seedMeta;
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(int i=0;i<mutations.length;i++) {
            if((seed1==parents1[i].getItem() && seed2==parents2[i].getItem()) && (meta1==parents1[i].getItemDamage() && meta2==parents2[i].getItemDamage())) {
                list.add(mutations[i]);
            }
            if((seed1==parents2[i].getItem() && seed2==parents1[i].getItem()) && (meta1==parents2[i].getItemDamage() && meta2==parents1[i].getItemDamage())) {
                list.add(mutations[i]);
            }
        }
        return list;
    }

    //logic for stat inheritance
    public static int[] getStats(TileEntityCrop[] input) {
        int[] output = new int[3];
        TileEntityCrop[] neighbors = getParents(input);
        int size = neighbors.length;
        int[] growth = new int[size];
        int[] gain = new int[size];
        int[] strength = new int[size];
        for(int i=0;i<size;i++) {
            growth[i] = neighbors[i].growth;
            gain[i] = neighbors[i].gain;
            strength[i] = neighbors[i].strength;
        }
        int meanGrowth = getMean(growth);
        int meanGain = getMean(gain);
        int meanStrength = getMean(strength);
        output[0] = getGain(meanGrowth, size);
        output[1] = getGain(meanGain, size);
        output[2] = getGain(meanStrength, size);
        for(int i=0;i<output.length;i++) {
            output[i] = output[i]>10?10:output[i];
        }
        return output;
    }

    //returns the mean value of an int array
    private static int getMean(int[] input) {
        int mean = 0;
        if(input.length>0) {
            for (int nr : input) {
                mean = mean + nr;
            }
            mean = Math.round(((float) mean) / ((float) input.length));
        }
        return mean;
    }

    //returns the added value for a statistic
    private static int getGain(int input, int neighbors) {
       return input+ (int) Math.round(Math.abs(neighbors-2)*Math.random());
    }

    //removes null instance from an ItemSeeds array
    private static ItemStack[] cleanItemStackArray(ItemStack[] input) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for(ItemStack seed:input) {
            if (seed != null) {
                list.add(seed);
            }
        }
        return list.toArray(new ItemStack[list.size()]);
    }


    //public methods to read data from the mutations and parents arrays

    //gets all the mutations
    public static ItemStack[] getMutations() {
        return mutations.clone();
    }

    //gets all the parents
    public static ItemStack[][] getParents() {
        ItemStack[][] parents = new ItemStack[mutations.length][2];
        for(int i=0;i<mutations.length;i++) {
            parents[i][0] = parents1[i];
            parents[i][1] = parents2[i];
        }
        return parents;
    }

    //gets all the crops this crop can mutate to
    //the mutation at a certain index corresponds to the co parent at the index of the array returned by getCoParents(ItemStack stack)
    public static ItemStack[] getMutations(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if(stack.getItem() instanceof ItemSeeds) {
            for (int i = 0; i < parents1.length; i++) {
                if (parents2[i].getItem() == stack.getItem() && parents2[i].getItemDamage() == stack.getItemDamage()) {
                    list.add(new ItemStack(mutations[i].getItem(), 1, mutations[i].getItemDamage()));
                }
                if (parents1[i].getItem() == stack.getItem() && parents1[i].getItemDamage() == stack.getItemDamage()) {
                    list.add(new ItemStack(mutations[i].getItem(), 1, mutations[i].getItemDamage()));
                }
            }
        }
        return list.toArray(new ItemStack[list.size()]);
    }

    //gets all the other parents this crop can mutate with
    //the parent at a certain index corresponds to the mutation at the index of the array returned by getMutations(ItemStack stack)
    public static ItemStack[] getCoParents(ItemStack stack) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if(stack.getItem() instanceof ItemSeeds) {
            for (int i = 0; i < parents1.length; i++) {
                if (parents2[i].getItem() == stack.getItem() && parents2[i].getItemDamage() == stack.getItemDamage()) {
                    list.add(new ItemStack(parents1[i].getItem(), 1, parents1[i].getItemDamage()));
                }
                if (parents1[i].getItem() == stack.getItem() && parents1[i].getItemDamage() == stack.getItemDamage()) {
                    list.add(new ItemStack(parents2[i].getItem(), 1, parents2[i].getItemDamage()));
                }
            }
        }
        return list.toArray(new ItemStack[list.size()]);
    }

    //gets the parents this crop mutates from
    public static ItemStack[][] getParents(ItemStack stack) {
        ArrayList<ItemStack[]> list = new ArrayList<ItemStack[]>();
        if(stack.getItem() instanceof ItemSeeds) {
            for(int i=0;i<mutations.length;i++) {
                if(mutations[i].getItem() == stack.getItem() && mutations[i].getItemDamage() == stack.getItemDamage()) {
                    ItemStack[] entry = {new ItemStack(parents1[i].getItem(), 1, parents1[i].getItemDamage()), new ItemStack(parents2[i].getItem(), 1, parents2[i].getItemDamage())};
                    list.add(entry);

                }
            }
        }
        ItemStack[][] result = new ItemStack[list.size()][2];
        for(int i=0;i<list.size();i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static double getMutationChance(ItemSeeds seed, int meta) {
        for(int i=0;i<mutationChances.length;i++) {
            if(seed==mutationChanceOverrides[i].getItem() && meta==mutationChanceOverrides[i].getItemDamage()) {
                return ((double)mutationChances[i])/100;
            }
        }
        return 1.00/ SeedHelper.getSeedTier(seed);
    }
}
