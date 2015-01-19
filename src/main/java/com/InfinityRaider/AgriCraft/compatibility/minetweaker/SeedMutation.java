package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.mutation.Mutation;
import com.InfinityRaider.AgriCraft.mutation.MutationHandler;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
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
        add(result, parent1, parent2, Mutation.DEFAULT_ID, null);
    }

    @ZenMethod
    public static void add(IItemStack result, IItemStack parent1, IItemStack parent2, int id, IItemStack block) {
        ItemStack resultToAdd = MineTweakerMC.getItemStack(result);
        ItemStack parent1ToAdd = MineTweakerMC.getItemStack(parent1);
        ItemStack parent2ToAdd = MineTweakerMC.getItemStack(parent2);
        ItemStack blockRequired = MineTweakerMC.getItemStack(block);

        if (resultToAdd.getItem() instanceof ItemSeeds && parent1ToAdd.getItem() instanceof ItemSeeds
                && parent2ToAdd.getItem() instanceof ItemSeeds && blockRequired.getItem() instanceof ItemBlock) {
            Block blockRequiredAsBlock = Block.getBlockFromItem(blockRequired.getItem());
            MineTweakerAPI.apply(new AddAction(resultToAdd, parent1ToAdd, parent2ToAdd, id, blockRequiredAsBlock));
        } else {
            MineTweakerAPI.logError("Adding mutation with result '" + resultToAdd.getDisplayName()
                    + "' failed. All 3 have to be of type ItemSeeds.");
        }
    }

    @ZenMethod
    public static void remove(IItemStack seed) {
        ItemStack resultToRemove = MineTweakerMC.getItemStack(seed);
        if (resultToRemove.getItem() instanceof ItemSeeds) {
            MineTweakerAPI.apply(new RemoveAction(resultToRemove));
        } else {
            MineTweakerAPI.logError(resultToRemove.getDisplayName() + " is not of type ItemSeeds.");
        }
    }

    private static class AddAction implements IUndoableAction {

        private final Mutation mutation;

        public AddAction(ItemStack resultToAdd, ItemStack parent1ToAdd, ItemStack parent2ToAdd, int id, Block block) {
            mutation = new Mutation(resultToAdd, parent1ToAdd, parent2ToAdd, id, block, Mutation.DEFAULT_REQUIREMENT_META);
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
            return mutation.result.getDisplayName() + " = " + mutation.parent1.getDisplayName() + " + " + mutation.parent2.getDisplayName();
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
