package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.v3.util.BlockWithMeta;
import com.infinityraider.agricraft.api.v3.IGrowthRequirement;
import com.infinityraider.agricraft.api.v3.RequirementType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An IGrowthRequirement implementation to prevent NPE's, for instance when the IGrowthRequirement for a null stack is querried, this is returned
 */
public final class GrowthRequirementNull implements IGrowthRequirement {
    GrowthRequirementNull() {}

    @Override
    public boolean canGrow(World world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isBaseBlockPresent(World world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isValidSoil(World world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isValidSoil(BlockWithMeta soil) {
        return false;
    }

    @Override
    public ItemStack requiredBlockAsItemStack() {
        return null;
    }

    @Override
    public RequirementType getRequiredType() {
        return null;
    }

    @Override
    public BlockWithMeta getSoil() {
        return null;
    }

    @Override
    public void setSoil(BlockWithMeta soil) {

    }

    @Override
    public int[] getBrightnessRange() {
        return new int[2];
    }

    @Override
    public void setBrightnessRange(int min, int max) {

    }

    @Override
    public void setRequiredBlock(BlockWithMeta requiredBlock, RequirementType requirementType, boolean oreDict) {

    }

    @Override
    public BlockWithMeta getRequiredBlock() {
        return null;
    }

    @Override
    public boolean isOreDict() {
        return false;
    }
}
