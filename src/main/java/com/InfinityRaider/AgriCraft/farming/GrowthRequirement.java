package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Encodes all requirements a plant needs to mutate and grow
 * Uses the Builder class inside to construct instances.
 */
public class GrowthRequirement {

    public static final GrowthRequirement DEFAULT = new GrowthRequirement();
    public static HashMap<ItemSeeds, HashMap<Integer, GrowthRequirement>> overrides = new HashMap<ItemSeeds, HashMap<Integer, GrowthRequirement>>();

    /** Maximum allowed brightness, exclusive **/
    private int maxBrightness = 16;
    /** Minimum allowed brightness, inclusive **/
    private int minBrightness = 8;

    private static List<BlockWithMeta> defaultSoils = new ArrayList<BlockWithMeta>();
    private static List<BlockWithMeta> soils = new ArrayList<BlockWithMeta>();
    private BlockWithMeta soil = null;

    public static enum RequirementType {
        NONE, BELOW, NEARBY
    }

    private BlockWithMeta requiredBlock = null;
    private boolean oreDict = false;
    private RequirementType requiredType = RequirementType.NONE;

    private GrowthRequirement() {
    }

    /** @Checks if all the requirements are met */
    public boolean canGrow(World world, int x, int y, int z) {
        return this.isValidSoil(world.getBlock(x, y-1, z), world.getBlockMetadata(x, y-1, z)) && this.isBrightnessGood(world.getBlockLightValue(x, y, z)) && this.isBaseBlockPresent(world, x, y, z);
    }

    /** @return true, if the correct base block is present **/
    public boolean isBaseBlockPresent(World world, int x, int y, int z) {
        if(this.requiresBaseBlock()) {
            switch(this.requiredType) {
                case BELOW: return this.isBaseBlockBelow(world, x, y, z);
                case NEARBY: return this.isBaseBlockNear(world, x, y, z);
            }
        }
        return true;
    }

    /** @return true, if the correct base block is below **/
    public boolean isBaseBlockBelow(World world, int x, int y, int z) {
        if(this.requiresBaseBlock() && this.requiredType==RequirementType.BELOW) {
            return this.isBlockAdequate(world.getBlock(x, y - 2, z), world.getBlockMetadata(x, y - 2, z));
        }
        return true;
    }

    /** @return true, if the correct base block is below **/
    public boolean isBaseBlockNear(World world, int x, int y, int z) {
        if(this.requiresBaseBlock() && this.requiredType==RequirementType.NEARBY) {
            int range = 4;
            for (int xPos = x - range; xPos <= x + range; x++) {
                for (int yPos = y - range; yPos <= y + range; y++) {
                    for (int zPos = z - range; zPos <= z + range; z++) {
                        if(this.isBlockAdequate(world.getBlock(xPos, yPos, zPos), world.getBlockMetadata(xPos, yPos, zPos))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    /** @return true, if this block corresponds to the required block **/
    public boolean isBlockAdequate(Block block, int meta) {
        if(this.oreDict) {
            return OreDictHelper.isSameOre(block, meta, this.requiredBlock.getBlock(), this.requiredBlock.getMeta());
        }
        else {
            return block==this.requiredBlock.getBlock() && meta==this.requiredBlock.getMeta();
        }
    }

    /** @return true, if the light level is between the allowed values */
    public boolean isBrightnessGood(int lvl) {
        return lvl<this.maxBrightness && lvl>=this.minBrightness;
    }

    /** @return true, if the given block is a valid soil */
    public boolean isValidSoil(Block block, int meta) {
       if(this.requiresSpecificSoil()) {
           return this.soil.equals(new BlockWithMeta(block, meta));
       } else {
           return defaultSoils.contains(new BlockWithMeta(block, meta));
       }
    }

    /** @return true, if the given block requires a specific soil */
    public boolean requiresSpecificSoil() {
        return this.soil!=null;
    }

    /** @return true, if the plant requires a base block beneath or nearby it */
    public boolean requiresBaseBlock() {
        return requiredType != RequirementType.NONE;
    }

    /** @return the required block as ItemStack of size 1 */
    public ItemStack requiredBlockAsItemStack() {
        return new ItemStack(requiredBlock.getBlock(), 1, requiredBlock.getMeta());
    }

    public BlockWithMeta getRequiredBlock() {
        return requiredBlock;
    }

    public RequirementType getRequiredType() {
        return requiredType;
    }

    public static List<BlockWithMeta> getSoils() {
        return soils;
    }

    public static boolean isSoilValid(Block block, int meta) {
        return soils.contains(new BlockWithMeta(block, meta));
    }

    public static class Builder {

        private final GrowthRequirement growthRequirement;

        public Builder() {
            this.growthRequirement = new GrowthRequirement();
        }

        /** Adds a required block to this GrowthRequirement instance */
        public Builder requiredBlock(BlockWithMeta requiredBlock, RequirementType requiredType, boolean oreDict) {
            if (requiredBlock == null || requiredType == RequirementType.NONE) {
                throw new IllegalArgumentException("Required block must be not null and required type must be other than NONE.");
            }
            growthRequirement.requiredBlock = requiredBlock;
            growthRequirement.requiredType = requiredType;
            growthRequirement.oreDict = oreDict;
            return this;
        }

        /** Sets the required soil */
        public Builder soil(BlockWithMeta block) {
            if(!soils.contains(block)) {
                soils.add(block);
            }
            growthRequirement.soil= block;
            return this;
        }

        public Builder brightnessRange(int min, int max) {
            this.growthRequirement.minBrightness = min;
            this.growthRequirement.maxBrightness = max;
            return this;
        }

        public GrowthRequirement build() {
            return growthRequirement;
        }
    }

    /** Finds the growth requirement for a seed */
    public static GrowthRequirement getGrowthRequirement(ItemSeeds seed, int meta) {
        if(SeedHelper.getPlant(seed) instanceof BlockModPlant) {
            return ((BlockModPlant) SeedHelper.getPlant(seed)).getGrowthRequirement();
        }
        else if (overrides.get(seed)!=null && overrides.get(seed).get(meta)!=null) {
            return overrides.get(seed).get(meta);
        }
        return DEFAULT;
    }
}
