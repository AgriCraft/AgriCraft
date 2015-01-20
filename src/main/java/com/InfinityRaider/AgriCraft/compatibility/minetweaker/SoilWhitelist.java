package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.List;

@ZenClass("mods.agricraft.SoilWhitelist")
public class SoilWhitelist {

    @ZenMethod
    public static void add(IItemStack soil) {
        add(new IItemStack[] { soil });
    }


    @ZenMethod
    public static void add(IItemStack[] soils) {
        ItemStack[] soilsToAdd = MineTweakerMC.getItemStacks(soils);
        if (areValidSoils(soilsToAdd)) {
            MineTweakerAPI.apply(new AddAction(soilsToAdd));
        } else {
            MineTweakerAPI.logError("Error adding soils to the whitelist. All provided items must be of type ItemBlock.");
        }
    }


    @ZenMethod
    public static void remove(IItemStack soil) {
        remove(new IItemStack[] { soil });
    }


    @ZenMethod
    public static void remove(IItemStack[] soils) {
        ItemStack[] soilsToRemove = MineTweakerMC.getItemStacks(soils);
        if (areValidSoils(soilsToRemove)) {
            MineTweakerAPI.apply(new RemoveAction(soilsToRemove));
        } else {
            MineTweakerAPI.logError("Error removing soils from the whitelist. All provided items must be of type ItemBlock.");
        }
    }


    /** @return False, if one of the provided ItemStacks is not of type ItemBlock, true otherwise */
    private static boolean areValidSoils(ItemStack[] soils) {
        for (ItemStack stack : soils) {
            if (!(stack.getItem() instanceof ItemBlock)) {
                return false;
            }
        }
        return true;
    }


    private static class AddAction implements IUndoableAction {

        private final List<ItemStack> soils;

        public AddAction(ItemStack[] soils) {
            this.soils = Arrays.asList(soils);
        }

        @Override
        public void apply() {
            com.InfinityRaider.AgriCraft.farming.SoilWhitelist.addAllToSoilWhitelist(soils);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            com.InfinityRaider.AgriCraft.farming.SoilWhitelist.removeAllFromSoilWhitelist(soils);
        }

        @Override
        public String describe() {
            return "Adding soils to whitelist.";
        }

        @Override
        public String describeUndo() {
            return "Removing previously added soils from the whitelist.";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    private static class RemoveAction implements IUndoableAction {

        private final List<ItemStack> soils;

        public RemoveAction(ItemStack[] soils) {
            this.soils = Arrays.asList(soils);
        }

        @Override
        public void apply() {
            com.InfinityRaider.AgriCraft.farming.SoilWhitelist.removeAllFromSoilWhitelist(soils);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            com.InfinityRaider.AgriCraft.farming.SoilWhitelist.addAllToSoilWhitelist(soils);
        }

        @Override
        public String describe() {
            return "Removing soils from the whitelist.";
        }

        @Override
        public String describeUndo() {
            return "Adding previously removed soils to the whitelist.";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
