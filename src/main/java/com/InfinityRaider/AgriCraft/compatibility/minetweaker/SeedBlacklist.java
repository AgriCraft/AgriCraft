package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.google.common.base.Joiner;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.List;

@ZenClass("mods.agricraft.SeedBlacklist")
public class SeedBlacklist {

    @ZenMethod
    public static void add(IItemStack seed) {
        add(new IItemStack[] { seed });
    }


    @ZenMethod
    public static void add(IItemStack[] seeds) {
        ItemStack[] seedsToAdd = MineTweakerMC.getItemStacks(seeds);
        if (areValidSeeds(seedsToAdd)) {
            MineTweakerAPI.apply(new AddAction(seedsToAdd));
        } else {
            MineTweakerAPI.logError("Error adding seeds to the blacklist. All provided items must be of type ItemSeeds.");
        }
    }


    @ZenMethod
    public static void remove(IItemStack seed) {
        remove(new IItemStack[] { seed });
    }


    @ZenMethod
    public static void remove(IItemStack[] seeds) {
        ItemStack[] seedsToRemove = MineTweakerMC.getItemStacks(seeds);
        if (areValidSeeds(seedsToRemove)) {
            MineTweakerAPI.apply(new RemoveAction(seedsToRemove));
        } else {
            MineTweakerAPI.logError("Error removing seeds from the blacklist. All provided items must be of type ItemSeeds.");
        }
    }


    /** @return False, if one of the provided ItemStacks is not of type ItemSeeds, true otherwise */
    private static boolean areValidSeeds(ItemStack[] seeds) {
        for (ItemStack stack : seeds) {
            if (!(stack.getItem() instanceof ItemSeeds)) {
                return false;
            }
        }
        return true;
    }


    private static class AddAction implements IUndoableAction {

        private final List<ItemStack> seeds;

        public AddAction(ItemStack[] seeds) {
            this.seeds = Arrays.asList(seeds);
        }

        @Override
        public void apply() {
            CropPlantHandler.addAllToSeedBlacklist(seeds);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            CropPlantHandler.removeAllFromSeedBlacklist(seeds);
        }

        @Override
        public String describe() {
            return "Adding seeds [" + Joiner.on(", ").join(seeds) + "] to the blacklist.";
        }

        @Override
        public String describeUndo() {
            return "Removing previously added seeds [" + Joiner.on(", ").join(seeds) + "] from the blacklist.";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    private static class RemoveAction implements IUndoableAction {

        private final List<ItemStack> seeds;

        public RemoveAction(ItemStack[] seeds) {
            this.seeds = Arrays.asList(seeds);
        }

        @Override
        public void apply() {
            CropPlantHandler.removeAllFromSeedBlacklist(seeds);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            CropPlantHandler.addAllToSeedBlacklist(seeds);
        }

        @Override
        public String describe() {
            return "Removing seeds [" + Joiner.on(", ").join(seeds) + "] from the blacklist.";
        }

        @Override
        public String describeUndo() {
            return "Adding previously removed seeds [" + Joiner.on(", ").join(seeds) + "] to the blacklist.";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
