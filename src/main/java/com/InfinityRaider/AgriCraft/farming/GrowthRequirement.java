package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encodes all requirements a plant needs to mutate and grow
 * Uses the Builder class inside to construct instances.
 */
public class GrowthRequirement {

    public static enum RequirementType {
        NONE, BELOW, NEARBY
    }

    private final List<BlockWithMeta> soils = new ArrayList<BlockWithMeta>();

    private BlockWithMeta requiredBlock;
    private RequirementType requiredType = RequirementType.NONE;

    private GrowthRequirement() {
    }

    /** @return true, if the given block is a valid soil */
    public boolean isValidSoil(Block block) {
        return soils.contains(new BlockWithMeta(block));
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

    public List<BlockWithMeta> getSoils() {
        return soils;
    }

    public static class Builder {

        private final GrowthRequirement growthRequirement;

        public Builder() {
            this.growthRequirement = new GrowthRequirement();
        }

        /** Adds all soils found in the SoilWhitelist as a soil requirement */
        public Builder defaultSoils() {
            // TODO: SoilWhitelist should also be of type BlockWithMeta
            List<ItemStack> whiteList = SoilWhitelist.getWhitelist();
            for (ItemStack stack : whiteList) {
                growthRequirement.soils.add(new BlockWithMeta(((ItemBlock) stack.getItem()).field_150939_a, stack.getItemDamage()));
            }
            return this;
        }

        /** Adds a required block to this GrowthRequirement instance */
        public Builder requiredBlock(BlockWithMeta requiredBlock, RequirementType requiredType) {
            if (requiredBlock == null || requiredType == RequirementType.NONE)
                throw new IllegalArgumentException("Required block must be not null and required type must be other than NONE.");

            growthRequirement.requiredBlock = requiredBlock;
            growthRequirement.requiredType = requiredType;
            return this;
        }

        /** Adds all the given soils to the soil requirements list */
        public Builder soils(BlockWithMeta... soils) {
            growthRequirement.soils.addAll(Arrays.asList(soils));
            return this;
        }

        public GrowthRequirement build() {
            return growthRequirement;
        }
    }
}
