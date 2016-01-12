package com.InfinityRaider.AgriCraft.compatibility.minetweaker;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.google.common.base.Joiner;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

public class Growing {
    /**Provides functionality to add and remove fertile soils*/
    @ZenClass("mods.agricraft.growing.FertileSoils")
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
                GrowthRequirementHandler.addAllToSoilWhitelist(soils);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                GrowthRequirementHandler.removeAllFromSoilWhitelist(soils);
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
                GrowthRequirementHandler.removeAllFromSoilWhitelist(soils);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                GrowthRequirementHandler.addAllToSoilWhitelist(soils);
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
    @ZenClass("mods.agricraft.growing.Soil")
    public static class Soil {
        @ZenMethod
        public static void set(IItemStack seed, IItemStack soil) {
            ItemStack seedStack = MineTweakerMC.getItemStack(seed);
            ItemStack soilStack = MineTweakerMC.getItemStack(soil);
            String error = "Invalid first argument: has to be a seed";
            boolean success = CropPlantHandler.isValidSeed(seedStack);
            if(success) {
                error = "Invalid second argument: has to be a block";
                success = soilStack.getItem()!=null && soilStack.getItem() instanceof ItemBlock;
                if(success) {
                    MineTweakerAPI.apply(new SetAction(seedStack, new BlockWithMeta(((ItemBlock) soilStack.getItem()).field_150939_a, soilStack.getItemDamage())));
                }
            }
            if(!success) {
                MineTweakerAPI.logError("Error when trying to set soil: "+error);
            }
        }

        @ZenMethod
        public static void clear(IItemStack seed) {
            ItemStack seedStack = MineTweakerMC.getItemStack(seed);
            if(seedStack.getItem()!=null && seedStack.getItem() instanceof ItemSeeds) {
                MineTweakerAPI.apply(new SetAction(seedStack, null));
            }
            else {
                MineTweakerAPI.logError("Error when trying to set soil: Invalid argument: has to be a seed");
            }
        }

        private static class SetAction implements IUndoableAction {

            private final ItemStack seedStack;
            private final ItemSeeds seed;
            private final int meta;
            private final BlockWithMeta soil;

            private BlockWithMeta oldSoil;

            public SetAction(ItemStack seed, BlockWithMeta block) {
                this.seedStack = seed;
                this.seed = (ItemSeeds) seed.getItem();
                this.meta = seed.getItemDamage();
                this.soil = block;
            }

            @Override
            public void apply() {
                IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(seed, meta);
                oldSoil = growthReq.getSoil();
                growthReq.setSoil(soil);
                GrowthRequirementHandler.addSoil(soil);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(seed, meta);
                growthReq.setSoil(oldSoil);
            }

            @Override
            public String describe() {
                String soilText = soil != null ? soil.toStack().getDisplayName() : "DEFAULT";
                return "Setting soil for " + seedStack.getDisplayName() + " to " + soilText;
            }

            @Override
            public String describeUndo() {
                String soilText = oldSoil != null ? oldSoil.toStack().getDisplayName() : "DEFAULT";
                return "Reverting soil for " + seedStack.getDisplayName() + " to " + soilText;
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }


    }

    /**Provides functionality to set the light level requirement for a plant*/
    @ZenClass("mods.agricraft.growing.Brightness")
    public static class Brightness {
        @ZenMethod public static void set(IItemStack seed, int min, int max) {
            ItemStack seedStack = MineTweakerMC.getItemStack(seed);
            String error = "Invalid first argument: has to be a seed";
            boolean success = CropPlantHandler.isValidSeed(seedStack);
            if(success) {
                error = "Invalid second argument: has to be larger than or equal to 0";
                success = min>=0;
                if(success) {
                    error = "maximum should be higher than the minimum";
                    success = max>min;
                    if(success) {
                        error = "Invalid third argument: has to be smaller than or equal to 16";
                        success = max<=16;
                        if(success) {
                            MineTweakerAPI.apply(new SetAction(seedStack, min, max));
                        }
                    }
                }
            }
            if(!success) {
                MineTweakerAPI.logError("Error when trying to set brightness: "+error);
            }
        }

        private static class SetAction implements IUndoableAction {

            private final ItemSeeds seed;
            private final int meta;
            private final int min;
            private final int max;

            private int[] old;

            public SetAction(ItemStack stack, int min, int max) {
                this.seed = (ItemSeeds) stack.getItem();
                this.meta = stack.getItemDamage();
                this.min = min;
                this.max = max;
            }

            @Override
            public void apply() {
                IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(seed, meta);
                old = growthReq.getBrightnessRange();
                growthReq.setBrightnessRange(min, max);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(seed, meta);
                growthReq.setBrightnessRange(old[0], old[1]);
            }

            @Override
            public String describe() {
                return "Setting brightness range of "+(new ItemStack(seed, 1, meta)).getDisplayName() + " to ["+min+", "+max+"[";
            }

            @Override
            public String describeUndo() {
                return "Resetting brightness range of "+(new ItemStack(seed, 1, meta)).getDisplayName() + " to ["+old[0]+", "+old[1]+"[";
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }
    }

    /**Provides functionality to set or clear a base block requirement for a plant*/
    @ZenClass("mods.agricraft.growing.BaseBlock")
    public static class BaseBlock {

        @ZenMethod
        public static void set(IItemStack seed, IItemStack base, int type, boolean oreDict) {
            set(MineTweakerMC.getItemStack(seed), MineTweakerMC.getItemStack(base), type, oreDict);
        }

        @ZenMethod
        public static void set(IItemStack seed, IItemStack base, int type) {
            set(seed, base, type, false);
        }

        @ZenMethod
        public static void set(IItemStack seed, IOreDictEntry base, int type) {
            set(MineTweakerMC.getItemStack(seed), MineTweakerMC.getItemStack(base), type, true);
        }

        private static void set(ItemStack seed, ItemStack base, int type, boolean oreDict) {
            if (type < 1 || type > 2) {
                MineTweakerAPI.logError("Type needs to be either 1 (below) or 2 (nearby)");
                return;
            }
            if (seed == null || !(CropPlantHandler.isValidSeed(seed))) {
                MineTweakerAPI.logError("Seeds has to be non-null and should be recognized by AgriCraft as a seed.");
                return;
            }
            if (base == null || !(base.getItem() instanceof ItemBlock)) {
                MineTweakerAPI.logError("Base has to be non-null and ot type ItemBlock.");
                return;
            }
            BlockWithMeta baseWM = new BlockWithMeta(((ItemBlock) base.getItem()).field_150939_a, base.getItemDamage());
            RequirementType reqType = type == 1 ? RequirementType.BELOW
                    : RequirementType.NEARBY;
            MineTweakerAPI.apply(new SetAction(seed, baseWM, reqType, oreDict));

        }

        @ZenMethod
        public static void clear(IItemStack seed) {
            ItemStack seedIS = MineTweakerMC.getItemStack(seed);
            if (seedIS == null || !(CropPlantHandler.isValidSeed(seedIS))) {
                MineTweakerAPI.logError("Seeds has to be non-null and should be recognized by AgriCraft as a seed.");
                return;
            }

            MineTweakerAPI.apply(new SetAction(seedIS, null, RequirementType.NONE, false));
        }

        private static class SetAction implements IUndoableAction {

            private final ItemStack seedStack;
            private final ItemSeeds seed;
            private final int seedMeta;
            private final BlockWithMeta base;
            private final RequirementType type;
            private final boolean oreDict;

            private BlockWithMeta oldReqBlock;
            private RequirementType oldRequiredType;
            private boolean oldReqBlockIsOreDict;

            public SetAction(ItemStack seed, BlockWithMeta base, RequirementType type, boolean oreDict) {
                this.seedStack = seed;
                this.seed = (ItemSeeds) seed.getItem();
                this.seedMeta = seed.getItemDamage();
                this.base = base;
                this.type = type;
                this.oreDict = oreDict;
            }

            @Override
            public void apply() {
                IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(seed, seedMeta);
                oldReqBlock = growthReq.getRequiredBlock();
                oldRequiredType = growthReq.getRequiredType();
                oldReqBlockIsOreDict = growthReq.isOreDict();
                growthReq.setRequiredBlock(base, type, oreDict);
            }

            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public void undo() {
                IGrowthRequirement growthReq = CropPlantHandler.getGrowthRequirement(seed, seedMeta);
                growthReq.setRequiredBlock(oldReqBlock, oldRequiredType, oldReqBlockIsOreDict);
            }

            @Override
            public String describe() {
                String blockString = base != null ? base.getBlock().getLocalizedName() : "DEFAULT";
                return "Setting base block requirement for seed " + seedStack.getDisplayName() + " to "
                        + blockString + " (" + type.toString() +  ")";
            }

            @Override
            public String describeUndo() {
                String blockString = (oldReqBlock!=null && oldReqBlock.getBlock()!=null) ? oldReqBlock.getBlock().getLocalizedName() : "DEFAULT";
                return "Resetting base block requirement for seed " + seedStack.getDisplayName() + " to "
                        + blockString + " (" + oldRequiredType.toString() + ")";
            }

            @Override
            public Object getOverrideKey() {
                return null;
            }
        }
    }
}
