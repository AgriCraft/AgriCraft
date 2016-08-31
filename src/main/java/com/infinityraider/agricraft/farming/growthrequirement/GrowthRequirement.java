package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.util.BlockWithMeta;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.misc.ISoilContainer;
import com.infinityraider.agricraft.api.requirement.RequirementType;
import com.infinityraider.agricraft.utility.BlockRange;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Encodes all requirements a plant needs to mutate and grow Uses the Builder
 * class inside to construct instances.
 */
public class GrowthRequirement implements IGrowthRequirement {

    public static final int NEARBY_DEFAULT_RANGE = 4;

    //brightness
    /**
     * Maximum allowed brightness, exclusive *
     */
    private final int maxBrightness;

    /**
     * Minimum allowed brightness, inclusive *
     */
    private final int minBrightness;

    private final BlockWithMeta soil;
    
    private final Optional<BlockWithMeta> reqBlock;
    private final Optional<RequirementType> reqType;

    GrowthRequirement(int maxBrightness, int minBrightness, BlockWithMeta soil, BlockWithMeta reqBlock, RequirementType reqType) {
        this.maxBrightness = maxBrightness;
        this.minBrightness = minBrightness;
        this.soil = soil;
        this.reqBlock = Optional.ofNullable(reqBlock);
        this.reqType = Optional.ofNullable(reqType);

        // Other stuff.
        GrowthRequirementHandler.addSoil(soil);
    }

    public List<BlockWithMeta> getSoilBlocks() {
        if (this.requiresSpecificSoil()) {
            List<BlockWithMeta> list = new ArrayList<>();
            list.add(soil);
            return list;
        }
        return GrowthRequirementHandler.defaultSoils;
    }

    @Override
    public Optional<BlockWithMeta> getRequiredBlock() {
        return this.reqBlock;
    }

    //Methods to check if a seed can grow
    //-----------------------------------
    @Override
    public boolean canGrow(World world, BlockPos pos) {
        return this.isValidSoil(world, pos.add(0, -1, 0)) && this.isBrightnessGood(world, pos) && this.isBaseBlockPresent(world, pos);
    }

    @Override
    public boolean isBaseBlockPresent(World world, BlockPos pos) {
        if (this.reqType.isPresent()) {
            switch (this.reqType.get()) {
                case BELOW:
                    return this.isBaseBlockBelow(world, pos.add(0, -2, 0));
                case NEARBY:
                    return this.isBaseBlockNear(world, pos);
            }
        }
        return true;
    }

    /**
     * @return true, if the correct base block is below *
     */
    private boolean isBaseBlockBelow(World world, BlockPos pos) {
        if (this.reqType.isPresent() && this.getRequiredType().get().equals(RequirementType.BELOW)) {
            return this.isBlockAdequate(world, pos);
        }
        return true;
    }

    /**
     * @return true, if the correct base block is below *
     */
    private boolean isBaseBlockNear(World world, BlockPos pos) {
        if (this.reqType.isPresent() && this.getRequiredType().get().equals(RequirementType.NEARBY)) {
            BlockRange range = new BlockRange(pos, NEARBY_DEFAULT_RANGE);
            return range.stream().anyMatch(p -> this.isBlockAdequate(world, p));
        }
        return true;
    }

    /**
     * @return true, if this block corresponds to the required block *
     */
    private boolean isBlockAdequate(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        BlockWithMeta block = new BlockWithMeta(state);
        return (!this.reqBlock.isPresent()) || this.reqBlock.get().equals(block);
    }

    public boolean isBrightnessGood(World world, BlockPos pos) {
        BlockPos above = pos.add(0, 1, 0);
        int lvl = Math.max(world.getLightFor(EnumSkyBlock.BLOCK, above), world.getLightFor(EnumSkyBlock.SKY, above));
        return lvl < this.maxBrightness && lvl >= this.minBrightness;
    }

    @Override
    public boolean isValidSoil(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        IBlockState state = world.getBlockState(pos);
        int meta = block.getMetaFromState(state);
        BlockWithMeta soil = new BlockWithMeta(block, meta);
        if (block instanceof ISoilContainer) {
            soil = new BlockWithMeta(((ISoilContainer) block).getSoil(world, pos), ((ISoilContainer) block).getSoilMeta(world, pos));
        }
        return isValidSoil(soil);
    }

    @Override
    public boolean isValidSoil(BlockWithMeta soil) {
        if (this.requiresSpecificSoil()) {
            return this.soil.equals(soil);
        } else {
            return GrowthRequirementHandler.defaultSoils.contains(soil);
        }
    }

    public boolean requiresSpecificSoil() {
        return this.soil != null;
    }

    public boolean requiresBaseBlock() {
        return this.reqType.isPresent() && !this.reqType.get().equals(RequirementType.NONE);
    }

    @Override
    public Optional<RequirementType> getRequiredType() {
        return this.reqType;
    }

    //Methods to change specific requirements
    //--------------------------------------
    @Override
    public BlockWithMeta getSoil() {
        return this.soil;
    }

    @Override
    public int getMinBrightness() {
        return minBrightness;
    }

    @Override
    public int getMaxBrightness() {
        return maxBrightness;
    }

}
