package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.requirement.RequirementType;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.util.FuzzyStack;
import com.infinityraider.agricraft.utility.BlockRange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.Collection;
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

    private final Collection<IAgriSoil> soils;
    
    private final Optional<FuzzyStack> reqBlock;
    private final Optional<RequirementType> reqType;

    GrowthRequirement(int maxBrightness, int minBrightness, Collection<IAgriSoil> soils, FuzzyStack reqBlock, RequirementType reqType) {
        assert soils != null;
        
        this.maxBrightness = maxBrightness;
        this.minBrightness = minBrightness;
        this.soils = soils;
        this.reqBlock = Optional.ofNullable(reqBlock);
        this.reqType = Optional.ofNullable(reqType);
    }

    @Override
    public Optional<FuzzyStack> getRequiredBlock() {
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
        FuzzyStack stack = new FuzzyStack(world.getBlockState(pos));
        return (!this.reqBlock.isPresent()) || this.reqBlock.equals(stack);
    }

    public boolean isBrightnessGood(World world, BlockPos pos) {
        BlockPos above = pos.add(0, 1, 0);
        int lvl = Math.max(world.getLightFor(EnumSkyBlock.BLOCK, above), world.getLightFor(EnumSkyBlock.SKY, above));
        return lvl < this.maxBrightness && lvl >= this.minBrightness;
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
    public Collection<IAgriSoil> getSoils() {
        return this.soils;
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
