package com.InfinityRaider.AgriCraft.farming.growthrequirement;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.ISoilContainer;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Encodes all requirements a plant needs to mutate and grow
 * Uses the Builder class inside to construct instances.
 */
public class GrowthRequirement implements IGrowthRequirement{
    GrowthRequirement() {}

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
            List<BlockWithMeta> list = new ArrayList<>();
            list.add(soil);
            return list;
        }
        return GrowthRequirementHandler.defaultSoils;
    }

    public List<BlockWithMeta> getBelowBlocks() {
        List<BlockWithMeta> list = new ArrayList<>();
        if(this.requiredType==RequirementType.BELOW) {
            list.add(requiredBlock);
        }
        return list;
    }

    public List<BlockWithMeta> getNearBlocks() {
        List<BlockWithMeta> list = new ArrayList<>();
        if(this.requiredType==RequirementType.NEARBY) {
            list.add(requiredBlock);
        }
        return list;
    }

    //Methods to check if a seed can grow
    //-----------------------------------
	public boolean canGrow(World world, BlockPos pos) {
        return this.isValidSoil(world, pos.add(0, -1, 0)) && this.isBrightnessGood(world, pos) && this.isBaseBlockPresent(world, pos);
    }

    @Override
    public boolean isBaseBlockPresent(World world, BlockPos pos) {
        if(this.requiresBaseBlock()) {
            switch(this.requiredType) {
                case BELOW: return this.isBaseBlockBelow(world, pos.add(0, -2, 0));
                case NEARBY: return this.isBaseBlockNear(world, pos);
            }
        }
        return true;
    }

    /** @return true, if the correct base block is below **/
    private boolean isBaseBlockBelow(World world, BlockPos pos) {
        if(this.requiresBaseBlock() && this.requiredType==RequirementType.BELOW) {
            return this.isBlockAdequate(world, pos);
        }
        return true;
    }

    /** @return true, if the correct base block is below **/
    private boolean isBaseBlockNear(World world, BlockPos pos) {
        if(this.requiresBaseBlock() && this.requiredType==RequirementType.NEARBY) {
            int range = NEARBY_DEFAULT_RANGE;
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            for (int xPos = x - range; xPos <= x + range; xPos++) {
                for (int yPos = y - range; yPos <= y + range; yPos++) {
                    for (int zPos = z - range; zPos <= z + range; zPos++) {
                        if(this.isBlockAdequate(world, pos.add(xPos, yPos, zPos))) {
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
    private boolean isBlockAdequate(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getDamageValue(world, pos);
        if(this.oreDict) {
            return OreDictHelper.isSameOre(block, meta, this.requiredBlock.getBlock(), this.requiredBlock.getMeta());
        }
        else {
            return block==this.requiredBlock.getBlock() && meta==this.requiredBlock.getMeta();
        }
    }

    public boolean isBrightnessGood(World world, BlockPos pos) {
        BlockPos above = pos.add(0, 1, 0);
        int lvl = Math.max(world.getLightFor(EnumSkyBlock.BLOCK, above), world.getLightFor(EnumSkyBlock.SKY, above));
        return lvl<this.maxBrightness && lvl>=this.minBrightness;
    }

    @Override
    public boolean isValidSoil(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        int meta = block.getDamageValue(world, pos);
        BlockWithMeta soil = new BlockWithMeta(block, meta);
        if (block instanceof ISoilContainer) {
            soil = new BlockWithMeta(((ISoilContainer) block).getSoil(world, pos), ((ISoilContainer) block).getSoilMeta(world, pos));
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

}
