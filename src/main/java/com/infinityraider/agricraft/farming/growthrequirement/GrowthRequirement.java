package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.util.BlockWithMeta;
import com.infinityraider.agricraft.api.requirment.IGrowthRequirement;
import com.infinityraider.agricraft.api.misc.ISoilContainer;
import com.infinityraider.agricraft.api.requirment.RequirementType;
import com.infinityraider.agricraft.utility.OreDictHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

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

    private final List<Pair<BlockWithMeta, RequirementType>> requiredBlocks;

    public GrowthRequirement(int maxBrightness, int minBrightness, BlockWithMeta soil, List<Pair<BlockWithMeta, RequirementType>> requiredBlocks) {
        this.maxBrightness = maxBrightness;
        this.minBrightness = minBrightness;
        this.soil = soil;
        this.requiredBlocks = requiredBlocks;

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
    public BlockWithMeta getRequiredBlock() {
        return this.requiredBlocks.isEmpty() ? null : this.requiredBlocks.get(0).getKey();
    }

    @Override
    public List<BlockWithMeta> getRequiredBlocks() {
        return this.requiredBlocks.stream()
                .map(p -> p.getKey())
                .collect(Collectors.toList());
    }
    
    public List<BlockWithMeta> getRequiredBlocks(RequirementType reqType) {
        return this.requiredBlocks.stream()
                .filter(p -> p.getValue().equals(RequirementType.NEARBY))
                .map(p -> p.getKey())
                .collect(Collectors.toList());
    }

    //Methods to check if a seed can grow
    //-----------------------------------
    @Override
    public boolean canGrow(World world, BlockPos pos) {
        return this.isValidSoil(world, pos.add(0, -1, 0)) && this.isBrightnessGood(world, pos) && this.isBaseBlockPresent(world, pos);
    }

    @Override
    public boolean isBaseBlockPresent(World world, BlockPos pos) {
        if (this.requiresBaseBlock()) {
            switch (this.getRequiredType()) {
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
        if (this.requiresBaseBlock() && this.getRequiredType() == RequirementType.BELOW) {
            return this.isBlockAdequate(world, pos);
        }
        return true;
    }

    /**
     * @return true, if the correct base block is below *
     */
    private boolean isBaseBlockNear(World world, BlockPos pos) {
        if (this.requiresBaseBlock() && this.getRequiredType() == RequirementType.NEARBY) {
            int range = NEARBY_DEFAULT_RANGE;
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            for (int xPos = x - range; xPos <= x + range; xPos++) {
                for (int yPos = y - range; yPos <= y + range; yPos++) {
                    for (int zPos = z - range; zPos <= z + range; zPos++) {
                        if (this.isBlockAdequate(world, pos.add(xPos, yPos, zPos))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    /**
     * @return true, if this block corresponds to the required block *
     */
    private boolean isBlockAdequate(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        if (this.requiredBlocks.isEmpty()) {
            return true;
        }
        Pair<BlockWithMeta, RequirementType> req = this.requiredBlocks.get(0);
        if (req.getKey().isUseOreDict()) {
            return OreDictHelper.isSameOre(block, meta, req.getKey().getBlock(), req.getKey().getMeta());
        } else {
            return block == req.getKey().getBlock() && meta == req.getKey().getMeta();
        }
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
        return this.getRequiredType() != RequirementType.NONE;
    }

    @Override
    public RequirementType getRequiredType() {
        return requiredBlocks.stream().map(p -> p.getValue()).findFirst().orElse(RequirementType.NONE);
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
