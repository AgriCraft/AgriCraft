package com.InfinityRaider.AgriCraft.apiimpl.v1;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.ISoilContainer;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes all requirements a plant needs to mutate and grow
 * Uses the Builder class inside to construct instances.
 */
public class GrowthRequirement implements IGrowthRequirement{
    //static fields storing other requirements for seeds from other mods
    public static final IGrowthRequirement DEFAULT = new GrowthRequirement();

    public static final int NEARBY_DEFAULT_RANGE = 4;

    //brightness
    /** Maximum allowed brightness, exclusive **/
    private int maxBrightness = 16;
    /** Minimum allowed brightness, inclusive **/
    private int minBrightness = 8;

    private BlockWithMeta soil = null;

    //block requirement
    private BlockWithMeta requiredBlock = null;
    private boolean oreDict = false;
    private RequirementType requiredType = RequirementType.NONE;

    public List<BlockWithMeta> getSoilBlocks() {
        if(this.requiresSpecificSoil()) {
            List<BlockWithMeta> list = new ArrayList<BlockWithMeta>();
            list.add(soil);
            return list;
        }
        return GrowthRequirementHandler.defaultSoils;
    }

    public List<BlockWithMeta> getBelowBlocks() {
        List<BlockWithMeta> list = new ArrayList<BlockWithMeta>();
        if(this.requiredType==RequirementType.BELOW) {
            list.add(requiredBlock);
        }
        return list;
    }

    public List<BlockWithMeta> getNearBlocks() {
        List<BlockWithMeta> list = new ArrayList<BlockWithMeta>();
        if(this.requiredType==RequirementType.NEARBY) {
            list.add(requiredBlock);
        }
        return list;
    }

    //Methods to check if a seed can grow
    //-----------------------------------
	public boolean canGrow(World world, int x, int y, int z) {
        return this.isValidSoil(world, x, y-1, z) && this.isBrightnessGood(world, x, y, z) && this.isBaseBlockPresent(world, x, y, z);
    }

    @Override
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
    private boolean isBaseBlockBelow(World world, int x, int y, int z) {
        if(this.requiresBaseBlock() && this.requiredType==RequirementType.BELOW) {
            return this.isBlockAdequate(world.getBlock(x, y - 2, z), world.getBlockMetadata(x, y - 2, z));
        }
        return true;
    }

    /** @return true, if the correct base block is below **/
    private boolean isBaseBlockNear(World world, int x, int y, int z) {
        if(this.requiresBaseBlock() && this.requiredType==RequirementType.NEARBY) {
            int range = NEARBY_DEFAULT_RANGE;
            for (int xPos = x - range; xPos <= x + range; xPos++) {
                for (int yPos = y - range; yPos <= y + range; yPos++) {
                    for (int zPos = z - range; zPos <= z + range; zPos++) {
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
    private boolean isBlockAdequate(Block block, int meta) {
        if(this.oreDict) {
            return OreDictHelper.isSameOre(block, meta, this.requiredBlock.getBlock(), this.requiredBlock.getMeta());
        }
        else {
            return block==this.requiredBlock.getBlock() && meta==this.requiredBlock.getMeta();
        }
    }

    public boolean isBrightnessGood(World world, int x, int y, int z) {
        int lvl = world.getFullBlockLightValue(x, y+1, z);
        return lvl<this.maxBrightness && lvl>=this.minBrightness;
    }

    @Override
    public boolean isValidSoil(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        BlockWithMeta soil = new BlockWithMeta(block, meta);
        if (block instanceof ISoilContainer) {
            soil = new BlockWithMeta(((ISoilContainer) block).getSoil(world, x, y, z), ((ISoilContainer) block).getSoilMeta(world, x, y, z));
        }
        return isValidSoil(soil);
    }

    @Override
    public boolean isValidSoil(BlockWithMeta soil) {
        if(this.requiresSpecificSoil()) {
            return this.soil.equals(soil);
        } else {
            return GrowthRequirementHandler.defaultSoils.contains(soil);
        }
    }

    public boolean requiresSpecificSoil() {
        return this.soil!=null;
    }

    public boolean requiresBaseBlock() {
        return requiredType != RequirementType.NONE;
    }

    @Override
    public ItemStack requiredBlockAsItemStack() {
        return new ItemStack(requiredBlock.getBlock(), 1, requiredBlock.getMeta());
    }

    @Override
    public RequirementType getRequiredType() {
        return requiredType;
    }

    //Methods to change specific requirements
    //--------------------------------------
    @Override
    public BlockWithMeta getSoil() {return this.soil;}

    @Override
    public void setSoil(BlockWithMeta soil) {
        this.soil = soil;
        GrowthRequirementHandler.addSoil(soil);
    }

    @Override
    public int[] getBrightnessRange() {return new int[] {minBrightness, maxBrightness};}

    @Override
    public void setBrightnessRange(int min, int max) {
        this.minBrightness = min;
        this.maxBrightness = max;
    }

    @Override
    public void setRequiredBlock(BlockWithMeta requiredBlock, RequirementType requirementType, boolean oreDict) {
        this.requiredBlock = requiredBlock;
        this.requiredType = requirementType;
        this.oreDict = oreDict;
    }

    @Override
    public BlockWithMeta getRequiredBlock() {
        return requiredBlock;
    }

    @Override
    public boolean isOreDict() {
        return oreDict;
    }

    //Builder class
    //-------------
    private GrowthRequirement() {}

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
            GrowthRequirementHandler.addSoil(block);
            growthRequirement.soil = block;
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
}
