package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class MutationHandler {

    private static List<Mutation> mutations;

    public static void init() {
        //Read mutations & initialize the mutation arrays
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readMutationData());
        List<Mutation> list = new ArrayList<Mutation>();
        for(String line:data) {
            Mutation mutation = readMutation(line);
            if(mutation!=null) {
                list.add(mutation);
            }
        }
        mutations = list;

        //print registered mutations to the log
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
                LogHelper.info(new StringBuffer(info).append(" (Requires ").append(block).append(" ").append(location).append(")"));
            }
        }
    }

    private static Mutation readMutation(String input) {
        Mutation mutation = null;
        String[] data = IOHelper.getData(input);
        boolean success = data.length==1 || data.length==3;
        String errorMsg = "invalid number of arguments";
        if(success) {
            String mutationData = data[0];
            int indexEquals = mutationData.indexOf('=');
            int indexPlus = mutationData.indexOf('+');
            success = (indexEquals>0 && indexPlus>indexEquals);
            errorMsg = "Mutation is not defined correctly";
            if(success) {
                //read the stacks
                ItemStack resultStack = IOHelper.getStack(mutationData.substring(0,indexEquals));
                ItemStack parentStack1 = IOHelper.getStack(mutationData.substring(indexEquals + 1, indexPlus));
                ItemStack parentStack2 = IOHelper.getStack(mutationData.substring(indexPlus+1));
                Item result = resultStack!=null?resultStack.getItem():null;
                Item parent1 = parentStack1!=null?parentStack1.getItem():null;
                Item parent2 = parentStack2!=null?parentStack2.getItem():null;
                success = result!=null && result instanceof ItemSeeds;
                errorMsg = "resulting stack is not correct";
                if(success) {
                    success =  parent1!=null &&  parent1 instanceof ItemSeeds;
                    errorMsg = "first parent stack is not correct";
                    if(success) {
                        success =  parent2!=null &&  parent2 instanceof ItemSeeds;
                        errorMsg = "second parent stack is not correct";
                        if(success) {
                            if(data.length==1) {
                                /*
                                if(result instanceof ItemModSeed && ((BlockModPlant) SeedHelper.getPlant( (ItemSeeds) result)).base!=null) {
                                    BlockModPlant plant = (BlockModPlant) SeedHelper.getPlant((ItemSeeds) result);
                                    mutation = new Mutation(resultStack, parentStack1, parentStack2, 1, plant.base, plant.baseMeta);
                                }
                                */
                                mutation = new Mutation(resultStack, parentStack1, parentStack2);
                            }
                            else {
                                ItemStack reqBlockStack = IOHelper.getStack(data[2]);
                                Block reqBlock = (reqBlockStack!=null && reqBlockStack.getItem() instanceof ItemBlock)?((ItemBlock) reqBlockStack.getItem()).field_150939_a:null;
                                success = reqBlock!=null;
                                errorMsg = "invalid required block";
                                if(success) {
                                    int id = Integer.parseInt(data[1]);
                                    int reqMeta = reqBlockStack.getItemDamage();
                                    mutation = new Mutation(resultStack, parentStack1, parentStack2, id, reqBlock, reqMeta);
                                }
                            }
                        }
                    }
                }
            }
        }
        if(!success) {
            LogHelper.info(new StringBuffer("Error when reading mutation: ").append(errorMsg).append(" (line: ").append(input).append(")"));
        }
        return mutation;
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
        return mutations.toArray(new Mutation[mutations.size()]);
    }

    //gets all the parents
    public static ItemStack[][] getParents() {
        ItemStack[][] parents = new ItemStack[mutations.size()][2];
        for(int i=0;i< mutations.size();i++) {
            parents[i][0] = mutations.get(i).parent1.copy();
            parents[i][1] = mutations.get(i).parent2.copy();
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
                if (!(mutation.parent2.getItem() == mutation.parent1.getItem() && mutation.parent2.getItemDamage() == mutation.parent1.getItemDamage()) && (mutation.parent1.getItem() == stack.getItem() && mutation.parent1.getItemDamage() == stack.getItemDamage())) {
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

    /**
     * Removes all mutations where the given parameter is the result of a mutation
     * @return Removed mutations
     */
    public static List<Mutation> removeMutationsByResult(ItemStack result) {
        List<Mutation> removedMutations = new ArrayList<Mutation>();
        for (Iterator<Mutation> iter = mutations.iterator(); iter.hasNext();) {
            Mutation mutation = iter.next();
            if (mutation.result.isItemEqual(result)) {
                iter.remove();
                removedMutations.add(mutation);
            }
        }
        return removedMutations;
    }

    /** Adds the given mutation to the mutations list */
    public static void add(Mutation mutation) {
        mutations.add(mutation);
    }

    /** Removes the given mutation from the mutations list */
    public static void remove(Mutation mutation) {
        mutations.remove(mutation);
    }

    /**
     * Adds all mutations back into the mutation list. Does not perform and validation.
     */
    public static void addAll(Collection<? extends Mutation> mutationsToAdd) {
        mutations.addAll(mutationsToAdd);
    }
}
