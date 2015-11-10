package com.InfinityRaider.AgriCraft.compatibility.minetweaker;

import com.InfinityRaider.AgriCraft.items.ItemHandRake;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.agricraft.WeedRaking")
public class WeedRaking {
    @ZenMethod
    public static void add(IItemStack drop, int weight) {
        MineTweakerAPI.apply(new AddAction(MineTweakerMC.getItemStack(drop), weight));
    }

    @ZenMethod
    public static void remove(IItemStack drop) {
        MineTweakerAPI.apply(new RemoveAction(MineTweakerMC.getItemStack(drop)));
    }

    private static class AddAction implements IUndoableAction {
        private final ItemStack entry;
        private final int weight;

        public AddAction(ItemStack drop, int weight) {
            this.entry = drop;
            this.weight = weight;
        }

        @Override
        public void apply() {
            ItemHandRake.ItemDropRegistry.instance().registerDrop(entry, weight);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            ItemHandRake.ItemDropRegistry.instance().removeDrop(entry);
        }

        @Override
        public String describe() {
            return "Adding drop for raking '" + entry.getDisplayName() + "', with weight " + weight;
        }

        @Override
        public String describeUndo() {
            return "Removing previously added rake drop '" + entry.getDisplayName() + "'";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveAction implements IUndoableAction {
        private final ItemStack drop;
        private final int weight;

        public RemoveAction(ItemStack drop) {
            this.drop = drop;
            this.weight = ItemHandRake.ItemDropRegistry.instance().getWeight(drop);
        }

        @Override
        public void apply() {
            if(weight > 0) {
                ItemHandRake.ItemDropRegistry.instance().removeDrop(drop);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if(weight > 0) {
                ItemHandRake.ItemDropRegistry.instance().registerDrop(drop, weight);
            }
        }

        @Override
        public String describe() {
            return "Removing drop for raking '" + drop.getDisplayName() + "',";
        }

        @Override
        public String describeUndo() {
            return "Restoring previously removed rake drop '" + drop.getDisplayName() + "', with weight "+ weight;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
