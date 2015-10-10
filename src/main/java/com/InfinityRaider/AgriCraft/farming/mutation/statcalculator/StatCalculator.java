package com.InfinityRaider.AgriCraft.farming.mutation.statcalculator;

import com.InfinityRaider.AgriCraft.farming.mutation.CrossOverResult;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class StatCalculator {
    private static StatCalculator instance;

    protected StatCalculator() {}

    public static StatCalculator getInstance() {
        if(instance == null) {
            if(ConfigurationHandler.hardCoreStats) {
                instance = new StatCalculatorHardcore();
            } else {
                instance = new StatCalculatorNormal();
            }
        }
        return instance;
    }

    /**
     * Applies the stats to the resulting crop after a spread or mutation
     * @param result The result from the spread/mutation
     * @param input A list with all the neighbouring crops, any neighbouring crop is in this list (with or without plant, mature or not, with weeds or not, ...)
     * @param mutation if this result comes from a mutation or from a spread
     */
    public abstract void setResultStats(CrossOverResult result, List<TileEntityCrop> input, boolean mutation);

    //gets an array of all the possible parents from the array containing all the neighbouring crops
    protected TileEntityCrop[] filterParents(List<TileEntityCrop> input) {
        ArrayList<TileEntityCrop> list = new ArrayList<TileEntityCrop>();
        for(TileEntityCrop crop:input) {
            if (crop != null && crop.isMature()) {
                list.add(crop);
            }
        }
        return list.toArray(new TileEntityCrop[list.size()]);
    }

    protected boolean canInheritStats(Item child, int childMeta, Item seed, int seedMeta) {
        int validParentId = ConfigurationHandler.validParents;
        //0: any crop
        //1: only parent crops and identical crops
        //2: only identical crops
        if(validParentId == 0) {
            return true;
        }
        if(validParentId == 2) {
            return child==seed && childMeta==seedMeta;
        }
        boolean b = child==seed && childMeta==seedMeta;
        if(!b) {
            for(Mutation mutation: MutationHandler.getMutationsFromChild(child, childMeta)) {
                if(mutation!=null) {
                    ItemStack parent1Stack = mutation.getParents()[0];
                    ItemStack parent2Stack = mutation.getParents()[1];
                    if(parent1Stack.getItem()==seed && parent1Stack.getItemDamage()==seedMeta) {
                        b = true;
                        break;
                    }
                    else if(parent2Stack.getItem()==seed && parent2Stack.getItemDamage()==seedMeta) {
                        b = true;
                        break;
                    }
                }
            }
        }
        return b;
    }
}
