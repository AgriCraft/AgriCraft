package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public abstract class MutationHandler {
    private static Mutation[] mutations;

    public static class Mutation {
        public ItemStack result;
        public ItemStack parent1;
        public ItemStack parent2;
        public int id;
        public Block requirement;
        public int requirementMeta;
        public double chance;

        public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int id, Block requirement, int requirementMeta, int chance) {
            this.result = result;
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.id = id;
            this.requirement = requirement;
            this.requirementMeta = requirementMeta;
            this.chance = ((double) chance)/100;
        }

        public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2) {
            this(result, parent1, parent2, 0, null, 0, 100);
            this.chance = 1.00/ SeedHelper.getSeedTier((ItemSeeds) result.getItem());
        }

        public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int chance) {
            this(result, parent1, parent2, 0, null, 0, chance);
        }

        public Mutation(ItemStack result, ItemStack parent1, ItemStack parent2, int id, Block requirement, int requirementMeta) {
            this(result, parent1, parent2, id, requirement, requirementMeta, 100);
            this.chance = 1.00/ SeedHelper.getSeedTier((ItemSeeds) result.getItem());
        }

        //copy constructor
        public Mutation(Mutation mutation) {
            this.result = mutation.result;
            this.parent1 = mutation.parent1;
            this.parent2 = mutation.parent2;
            this.id = mutation.id;
            this.requirement = mutation.requirement;
            this.requirementMeta = mutation.requirementMeta;
        }

        public void setChanceOverride(int chance) {
            this.chance = chance;
        }
    }

    public static void init() {
        //Read mutations & initialize the mutation arrays
        setMutations(IOHelper.getLinesArrayFromData(ConfigurationHandler.readMutationData()));

        LogHelper.info("Registered Mutations:");
        for (Mutation mutation:mutations) {
            String result = mutation.result.getItem() != null ? (Item.itemRegistry.getNameForObject(mutation.result.getItem()) + ':' + mutation.result.getItemDamage()) : "null";
            String parent1 = mutation.parent1.getItem() != null ? (Item.itemRegistry.getNameForObject(mutation.parent1.getItem())) + ':' + mutation.parent1.getItemDamage() : "null";
            String parent2 = mutation.parent2.getItem() != null ? (Item.itemRegistry.getNameForObject(mutation.parent2.getItem())) + ':' + mutation.parent2.getItemDamage() : "null";
            String info = " - " + result + " = " + parent1 + " + " + parent2;
            if (mutation.id == 0) {
                LogHelper.info(info);
            } else {
                String block = mutation.requirement != null ? (Block.blockRegistry.getNameForObject(mutation.requirement) + ':' + mutation.requirementMeta) : "null";
                String location = "";
                if (mutation.id == 1) {
                    location = "below";
                } else if (mutation.id == 2) {
                    location = "nearby";
                }
                LogHelper.info(info + " (Requires " + block + " " + location + ")");
            }
        }
    }

    //initializes the mutations arrays
    private static void setMutations(String[] data) {
        mutations = new Mutation[data.length];
        for(int i=0;i<data.length;i++) {
            //read the stacks
            ItemStack result = IOHelper.getSeedStack(IOHelper.correctSeedName(data[i].substring(0,data[i].indexOf('='))));
            ItemStack parent1 = IOHelper.getSeedStack(IOHelper.correctSeedName(data[i].substring(data[i].indexOf('=') + 1, data[i].indexOf('+'))));
            ItemStack parent2;
            //parse the end of the string
            int id = 0;
            Block req = null;
            int reqMeta = 0;
            String nextPart = data[i].substring(data[i].indexOf('+')+1);
            //check if a requirement is specified
            if(nextPart.indexOf(',')>0) {
                parent2 = IOHelper.getSeedStack(IOHelper.correctSeedName(nextPart.substring(0, nextPart.indexOf(','))));
                nextPart = nextPart.substring(nextPart.indexOf(',')+1);
                id = Integer.parseInt(nextPart.substring(0, nextPart.indexOf(',')));
                //get the requirement
                ItemStack blockStack = IOHelper.getBlock(nextPart.substring(nextPart.indexOf(',')+1));
                req = ((ItemBlock) blockStack.getItem()).field_150939_a;
                reqMeta = blockStack.getItemDamage();
            }
            //no requirement specified
            else {
                parent2 = IOHelper.getSeedStack(IOHelper.correctSeedName(nextPart));
            }
            mutations[i] = new Mutation(result, parent1, parent2, id, req, reqMeta, 100);
        }
    }

    //gets all the possible crossovers
    public static Mutation[] getCrossOvers(TileEntityCrop[] crops) {
        TileEntityCrop[] parents = MutationHandler.getParents(crops);
        ArrayList<Mutation> list = new ArrayList<Mutation>();
        switch (parents.length) {
            case 2:
                list.addAll(MutationHandler.getMutations(parents[0], parents[1]));
                break;
            case 3:
                list.addAll(MutationHandler.getMutations(parents[0], parents[1]));
                list.addAll(MutationHandler.getMutations(parents[0], parents[2]));
                list.addAll(MutationHandler.getMutations(parents[1], parents[2]));
                break;
            case 4:
                list.addAll(MutationHandler.getMutations(parents[0], parents[1]));
                list.addAll(MutationHandler.getMutations(parents[0], parents[2]));
                list.addAll(MutationHandler.getMutations(parents[0], parents[3]));
                list.addAll(MutationHandler.getMutations(parents[1], parents[2]));
                list.addAll(MutationHandler.getMutations(parents[1], parents[3]));
                list.addAll(MutationHandler.getMutations(parents[2], parents[3]));
                break;
        }
        return cleanMutationArray(list.toArray(new Mutation[list.size()]));
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
    private static ArrayList<Mutation> getMutations(TileEntityCrop parent1, TileEntityCrop parent2) {
        ItemSeeds seed1 = (ItemSeeds) parent1.seed;
        ItemSeeds seed2 = (ItemSeeds) parent2.seed;
        int meta1 = parent1.seedMeta;
        int meta2 = parent2.seedMeta;
        ArrayList<Mutation> list = new ArrayList<Mutation>();
        for (Mutation mutation:mutations) {
            if ((seed1==mutation.parent1.getItem() && seed2==mutation.parent2.getItem()) && (meta1==mutation.parent1.getItemDamage() && meta2==mutation.parent2.getItemDamage())) {
                list.add(mutation);
            }
            if ((seed1==mutation.parent2.getItem() && seed2==mutation.parent1.getItem()) && (meta1==mutation.parent2.getItemDamage() && meta2==mutation.parent1.getItemDamage())) {
                list.add(mutation);
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

    //removes null instance from a mutations array
    private static Mutation[] cleanMutationArray(Mutation[] input) {
        ArrayList<Mutation> list = new ArrayList<Mutation>();
        for(Mutation mutation:input) {
            if (mutation.result != null) {
                list.add(mutation);
            }
        }
        return list.toArray(new Mutation[list.size()]);
    }


    //public methods to read data from the mutations and parents arrays

    //gets all the mutations
    public static Mutation[] getMutations() {
        return mutations.clone();
    }

    //gets all the parents
    public static ItemStack[][] getParents() {
        ItemStack[][] parents = new ItemStack[mutations.length][2];
        for(int i=0;i< mutations.length;i++) {
            parents[i][0] = mutations[i].parent1.copy();
            parents[i][1] = mutations[i].parent2.copy();
        }
        return parents;
    }

    //gets all the mutations this crop can mutate to
    public static Mutation[] getMutations(ItemStack stack) {
        ArrayList<Mutation> list = new ArrayList<Mutation>();
        if(stack.getItem() instanceof ItemSeeds) {
            for (Mutation mutation:mutations) {
                if (mutation.parent2.getItem() == stack.getItem() && mutation.parent2.getItemDamage() == stack.getItemDamage()) {
                    list.add(new Mutation(mutation));
                }
                if (mutation.parent1.getItem() == stack.getItem() && mutation.parent1.getItemDamage() == stack.getItemDamage()) {
                    list.add(new Mutation(mutation));
                }
            }
        }
        return list.toArray(new Mutation[list.size()]);
    }

    //gets the parents this crop mutates from
    public static Mutation[] getParentMutations(ItemStack stack) {
        ArrayList<Mutation> list = new ArrayList<Mutation>();
        if(stack.getItem() instanceof ItemSeeds) {
            for (Mutation mutation:mutations) {
                if (mutation.result.getItem() == stack.getItem() && mutation.result.getItemDamage() == stack.getItemDamage()) {
                    list.add(new Mutation(mutation));
                }
            }
        }
        return list.toArray(new Mutation[list.size()]);
    }
}
