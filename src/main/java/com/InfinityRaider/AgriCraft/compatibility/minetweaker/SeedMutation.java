package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;


@ZenClass("mods.agricraft.SeedMutation")
public class SeedMutation {

    @ZenMethod
    public static void add(IItemStack result, IItemStack parent1, IItemStack parent2) {
        ItemStack resultToAdd = MineTweakerMC.getItemStack(result);
        ItemStack parent1ToAdd = MineTweakerMC.getItemStack(parent1);
        ItemStack parent2ToAdd = MineTweakerMC.getItemStack(parent2);

        if (CropPlantHandler.isValidSeed(resultToAdd) && CropPlantHandler.isValidSeed(parent1ToAdd) && CropPlantHandler.isValidSeed(parent2ToAdd)) {
            MineTweakerAPI.apply(new AddAction(resultToAdd, parent1ToAdd, parent2ToAdd));
        } else {
            MineTweakerAPI.logError("Adding mutation with result '" + resultToAdd.getDisplayName()
                    + "' failed. All 3 have to be of type ItemSeeds.");
        }
    }

    @ZenMethod
    public static void remove(IItemStack result) {
        ItemStack resultToRemove = MineTweakerMC.getItemStack(result);
        if (CropPlantHandler.isValidSeed(resultToRemove)) {
            MineTweakerAPI.apply(new RemoveAction(resultToRemove));
        } else {
            MineTweakerAPI.logError(resultToRemove.getDisplayName() + " is not of type ItemSeeds.");
        }
    }

    private static class AddAction implements IUndoableAction {

        private final Mutation mutation;

        public AddAction(ItemStack resultToAdd, ItemStack parent1ToAdd, ItemStack parent2ToAdd) {
            mutation = new Mutation(resultToAdd, parent1ToAdd, parent2ToAdd);
        }

        @Override
        public void apply() {
            MutationHandler.add(mutation);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            MutationHandler.remove(mutation);
        }

        @Override
        public String describe() {
            return "Adding mutation '" + getEquationString() + "'";
        }

        @Override
        public String describeUndo() {
            return "Removing previously added mutation '" + getEquationString() + "'";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

        private String getEquationString() {
            ItemStack result = mutation.getResult();
            ItemStack[] parents = mutation.getParents();
            return result.getDisplayName() + " = " + parents[0].getDisplayName() + " + " + parents[1].getDisplayName();
        }
    }

    private static class RemoveAction implements IUndoableAction {

        private final ItemStack result;
        private List<Mutation> removedMutations = new ArrayList<Mutation>();

        public RemoveAction(ItemStack result) {
            this.result = result;
        }

        @Override
        public void apply() {
            removedMutations = MutationHandler.removeMutationsByResult(result);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            MutationHandler.addAll(removedMutations);
        }

        @Override
        public String describe() {
            return "Removing all mutations where '" + result.getDisplayName() + "' is the result.";
        }

        @Override
        public String describeUndo() {
            return "Adding back in all mutations where '" + result.getDisplayName() + "' is the result.";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
