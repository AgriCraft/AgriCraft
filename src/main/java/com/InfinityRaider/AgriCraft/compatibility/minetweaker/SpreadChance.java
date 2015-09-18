package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import com.InfinityRaider.AgriCraft.utility.SeedHelper;

@ZenClass("mods.agricraft.SpreadChance")
public class SpreadChance {

    @ZenMethod
    public static void override(IItemStack seed, int chance) {
        ItemStack seedToOverride = MineTweakerMC.getItemStack(seed);
        if (seedToOverride.getItem() instanceof ItemSeeds) {
            if (chance >= 0 && chance <= 100) {
                MineTweakerAPI.apply(new OverrideAction(seedToOverride, chance));
            } else {
                MineTweakerAPI.logError("Spread chance must be between 0 and 100 inclusive.");
            }
        } else {
            MineTweakerAPI.logError("Spread chance can only be overwritten for items of type ItemSeeds.");
        }
    }


    private static class OverrideAction implements IUndoableAction {

        private final ItemStack seed;
        private final int chance;
        private int oldChance;

        public OverrideAction(ItemStack seed, int chance) {
            this.seed = seed;
            this.chance = chance;
        }

        @Override
        public void apply() {
            oldChance = SeedHelper.overrideSpreadChance((ItemSeeds) seed.getItem(), seed.getItemDamage(), chance);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            SeedHelper.overrideSpreadChance((ItemSeeds) seed.getItem(), seed.getItemDamage(), oldChance);
        }

        @Override
        public String describe() {
            return "Overriding spread chance of " + seed.getDisplayName() + " to " + chance;
        }

        @Override
        public String describeUndo() {
            return "Resetting spread chance of " + seed.getDisplayName() + " to " + oldChance;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
