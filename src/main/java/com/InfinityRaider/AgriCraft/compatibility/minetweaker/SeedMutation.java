package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.mutation.Mutation;
import com.InfinityRaider.AgriCraft.mutation.MutationHandler;
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
