package com.InfinityRaider.AgriCraft.farming.growthrequirement;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * An IGrowthRequirement implementation to prevent NPE's, for instance when the IGrowthRequirement for a null stack is querried, this is returned
 */
public final class GrowthRequirementNull implements IGrowthRequirement {
    GrowthRequirementNull() {}

    @Override
    public boolean canGrow(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isBaseBlockPresent(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isValidSoil(World world, int x, int y, int z) {
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
