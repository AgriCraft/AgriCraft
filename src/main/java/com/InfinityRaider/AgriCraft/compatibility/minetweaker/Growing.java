package com.InfinityRaider.AgriCraft.compatibility.minetweaker;


import com.InfinityRaider.AgriCraft.farming.GrowthRequirement;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.google.common.base.Joiner;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("mods.agricraft.Growing")
public class Growing {
    /**Provides functionality to add and remove fertile soils*/
    @ZenClass("mods.agricraft.Growing.FertileSoils")
    public static class FertileSoils {
        @ZenMethod
        public static void add(IItemStack soil) {
            add(new IItemStack[]{soil});
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
            remove(new IItemStack[]{soil});
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

        /**
         * @return False, if one of the provided ItemStacks is not of type ItemBlock, true otherwise
         */
        private static boolean areValidSoils(ItemStack[] soils) {
            for (ItemStack stack : soils) {
                if (!(stack.getItem() instanceof ItemBlock)) {
                    return false;
                }
            }
            return true;
        }

        private static class AddAction implements IUndoableAction {

            private final List<BlockWithMeta> soils;

            public AddAction(ItemStack[] soils) {
                this.soils = new ArrayList<BlockWithMeta>();
                for(ItemStack stack:soils) {
                    this.soils.add(new BlockWithMeta(((ItemBlock) stack.getItem()).field_150939_a, stack.getItemDamage()));
                }
            }

            @Override
            public void apply() {
                GrowthRequirements.addAllToSoilWhitelist(soils);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                GrowthRequirements.removeAllFromSoilWhitelist(soils);
            }

            @Override
            public String describe() {
                return "Adding soils [" + Joiner.on(", ").join(soils) + "] to whitelist.";
            }

            @Override
            public String describeUndo() {
                return "Removing previously added soils [" + Joiner.on(", ").join(soils) + "] from the whitelist.";
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }


        private static class RemoveAction implements IUndoableAction {

            private final List<BlockWithMeta> soils;

            public RemoveAction(ItemStack[] soils) {
                this.soils = new ArrayList<BlockWithMeta>();
                for(ItemStack stack:soils) {
                    this.soils.add(new BlockWithMeta(((ItemBlock) stack.getItem()).field_150939_a, stack.getItemDamage()));
                }
            }

            @Override
            public void apply() {
                GrowthRequirements.removeAllFromSoilWhitelist(soils);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                GrowthRequirements.addAllToSoilWhitelist(soils);
            }

            @Override
            public String describe() {
                return "Removing soils [" + Joiner.on(", ").join(soils) + "] from the whitelist.";
            }

            @Override
            public String describeUndo() {
                return "Adding previously removed soils [" + Joiner.on(", ").join(soils) + "] to the whitelist.";
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }
    }

    /**Provides functionality to set or clear a specific soil for a plant*/
    @ZenClass("mods.agricraft.Growing.Soil")
    public static class Soil {
        @ZenMethod
        public static void set(ItemStack seed, ItemStack soil) {
            String error = "Invalid first argument: has to be a seed";
            boolean success = seed.getItem()!=null && seed.getItem() instanceof ItemSeeds;
            if(success) {
                error = "Invalid second argument: has to be a block";
                success = soil.getItem()!=null && soil.getItem() instanceof ItemBlock;
                if(success) {
                    MineTweakerAPI.apply(new SetAction(seed, new BlockWithMeta(((ItemBlock) soil.getItem()).field_150939_a, soil.getItemDamage())));
                }
            }
            if(!success) {
                MineTweakerAPI.logError("Error when trying to set soil: "+error);
            }
        }

        @ZenMethod
        public static void clear(ItemStack seed) {
            if(seed.getItem()!=null && seed.getItem() instanceof ItemSeeds) {
                MineTweakerAPI.apply(new ClearAction(seed));
            }
            else {
                MineTweakerAPI.logError("Error when trying to set soil: Invalid argument: has to be a seed");
            }
        }

        private static class SetAction implements IUndoableAction {
            private final ItemSeeds seed;
            private final int meta;
            private final BlockWithMeta soil;

            public SetAction(ItemStack seed, BlockWithMeta block) {
                this.seed = (ItemSeeds) seed.getItem();
                this.meta = seed.getItemDamage();
                this.soil = block;
            }

            @Override
            public void apply() {
                if(GrowthRequirements.hasDefault(seed, meta)) {
                    GrowthRequirement req = (new GrowthRequirement.Builder()).soil(this.soil).build();
                    GrowthRequirements.setRequirement(this.seed, this.meta, req);
                }
                else {
                    GrowthRequirement req = GrowthRequirements.getGrowthRequirement(this.seed, this.meta);
                    req.setSoil(soil);
                }
            }

            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public void undo() {
            }

            @Override
            public String describe() {
                return "Setting soil for "+(new ItemStack(seed, 1, meta)).getDisplayName() + " to "+soil.toStack().getDisplayName();
            }

            @Override
            public String describeUndo() {
                return "Undoing set soil for "+(new ItemStack(seed, 1, meta)).getDisplayName() + " to "+soil.toStack().getDisplayName();
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }

        private static class ClearAction implements IUndoableAction {
            private final ItemSeeds seed;
            private final int meta;
            private final boolean hadReq;
            private final BlockWithMeta oldSoil;

            public ClearAction(ItemStack stack) {
                this.seed = (ItemSeeds) stack.getItem();
                this.meta = stack.getItemDamage();
                this.hadReq = !GrowthRequirements.hasDefault(seed, meta);
                this.oldSoil = GrowthRequirements.getGrowthRequirement(seed, meta).getSoil();
            }

            @Override
            public void apply() {
                if(hadReq) {
                    GrowthRequirement req = GrowthRequirements.getGrowthRequirement(seed, meta);
                    req.setSoil(null);
                }
                //if it didn't have a requirement, there is no need to add one because the default has no specific soil anyway
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                if(hadReq) {
                    GrowthRequirement req = GrowthRequirements.getGrowthRequirement(seed, meta);
                    req.setSoil(oldSoil);
                } else {
                    GrowthRequirements.resetGrowthRequirement(seed, meta);
                }
            }

            @Override
            public String describe() {
                return "Clearing soil for "+(new ItemStack(seed, 1, meta)).getDisplayName();
            }

            @Override
            public String describeUndo() {
                return "Resetting cleared soil for "+(new ItemStack(seed, 1, meta)).getDisplayName();
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }
    }

    /**Provides functionality to set the light level requirement for a plant*/
    @ZenClass("mods.agricraft.Growing.Brightness")
    public static class Brightness {
        @ZenMethod public static void set(ItemStack seed, int min, int max) {
            String error = "Invalid first argument: has to be a seed";
            boolean success = seed.getItem()!=null && seed.getItem() instanceof ItemSeeds;
            if(success) {
                error = "Invalid second argument: has to be larger than or equal to 0";
                success = min>=0;
                if(success) {
                    error = "Invalid third argument: has to be smaller than 16";
                    success = max>min;
                    if(success) {
                        error = "maximum should be higher than the minimum";
                        success = max<16;
                        if(success) {
                            MineTweakerAPI.apply(new SetAction(seed, min, max));
                        }
                    }
                }
            }
            if(!success) {
                MineTweakerAPI.logError("Error when trying to set soil: "+error);
            }
        }

        private static class SetAction implements IUndoableAction {
            private final ItemSeeds seed;
            private final int meta;
            private final int min;
            private final int max;
            private final boolean hadReq;
            private final int oldMin;
            private final int oldMax;

            public SetAction(ItemStack stack, int min, int max) {
                this.seed = (ItemSeeds) stack.getItem();
                this.meta = stack.getItemDamage();
                this.min = min;
                this.max = max;
                this.hadReq = !GrowthRequirements.hasDefault(seed, meta);
                int[] old = GrowthRequirements.getGrowthRequirement(seed, meta).getBrightnessRange();
                oldMin = old[0];
                oldMax = old[1];
            }

            @Override
            public void apply() {
                if(hadReq) {
                    GrowthRequirement req = GrowthRequirements.getGrowthRequirement(seed, meta);
                    req.setBrightnessRange(min, max);
                }
                else {
                    GrowthRequirement req = (new GrowthRequirement.Builder()).brightnessRange(min, max).build();
                    GrowthRequirements.setRequirement(seed, meta, req);
                }
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                if(hadReq) {
                    GrowthRequirement req = GrowthRequirements.getGrowthRequirement(seed, meta);
                    req.setBrightnessRange(oldMin, oldMax);
                }
                else {
                    GrowthRequirements.resetGrowthRequirement(seed, meta);
                }
            }

            @Override
            public String describe() {
                return "Setting brightness range of "+(new ItemStack(seed, 1, meta)).getDisplayName() + " to ["+min+", "+max+"[";
            }

            @Override
            public String describeUndo() {
                return "Resetting brightness range of "+(new ItemStack(seed, 1, meta)).getDisplayName() + " to ["+oldMin+", "+oldMax+"[";
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }
    }

    /**Provides functionality to set or clear a base block requirement for a plant*/
    @ZenClass("mods.agricraft.Growing.BaseBlock")
    public static class BaseBlock {

    }
}
