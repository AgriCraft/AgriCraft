package com.infinityraider.agricraft.farming.growthrequirement;

import com.infinityraider.agricraft.api.v1.*;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.utility.IOHelper;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * Holds all the default soils and soil.
 * Also holds all GrowthRequirements.
 */
public class GrowthRequirementHandler {
    public static final IGrowthRequirement NULL = new GrowthRequirementNull();
    public static IGrowthRequirementBuilder getNewBuilder() {return new GrowthRequirementHandler.Builder();}

    /**
     * This list contains soils which pose as a default soil, meaning any CropPlant which doesn't require a specific soil will be able to grown on these
     * This list can be modified with MineTweaker
     */
    public static List<BlockWithMeta> defaultSoils = new ArrayList<>();

    /**
     * This list contains soils needed for certain CropPlants
     * This list cannot be modified externally
     */
    static List<BlockWithMeta> soils = new ArrayList<>();

    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(World world,BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getDamageValue(world, pos);
        BlockWithMeta soil;
        if (block instanceof ISoilContainer) {
            soil = new BlockWithMeta(((ISoilContainer) block).getSoil(world, pos), ((ISoilContainer) block).getSoilMeta(world, pos));
        } else {
            soil = new BlockWithMeta(block, meta);
        }
        return soils.contains(soil) || defaultSoils.contains(soil);
    }

    public static void init() {
        registerSoils();
        registerCustomEntries();
    }

    private static void registerSoils() {
        addDefaultSoil(new BlockWithMeta(Blocks.farmland));
    }

    private static void registerCustomEntries() {
        //reads custom entries
    	LogHelper.info("Registering soils to whitelist:");
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSoils());
        String total = " of " + data.length + ".";
        for (String line : data) {
            LogHelper.debug("  Parsing " + line + total);
            ItemStack stack = IOHelper.getStack(line);
            Block block = (stack != null && stack.getItem() instanceof ItemBlock) ? ((ItemBlock) stack.getItem()).block : null;
            
            if (block != null) {
                addDefaultSoil(new BlockWithMeta(block, stack.getItemDamage()));
            } else {
                LogHelper.info(" Error when adding block to soil whitelist: Invalid block (line: " + line + ")");
            }
        }
        
        LogHelper.info("Completed soil whitelist:");
        for (BlockWithMeta soil : soils) {
            LogHelper.info(" - " + Block.blockRegistry.getNameForObject(soil.getBlock()) + ":" + soil.getMeta());
        }
    }

    public static void addAllToSoilWhitelist(Collection<? extends BlockWithMeta> list) {
        for (BlockWithMeta block : list) {
            addDefaultSoil(block);
        }
    }

    public static void removeAllFromSoilWhitelist(Collection<? extends BlockWithMeta> list) {
        defaultSoils.removeAll(list);
    }

    public static void addSoil(BlockWithMeta block) {
        if (!soils.contains(block)) {
            soils.add(block);
        }
    }

    public static boolean addDefaultSoil(BlockWithMeta block) {
        if (!defaultSoils.contains(block)) {
            defaultSoils.add(block);
            return true;
        }
        return false;
    }

    //Builder class
    //-------------
    private static class Builder implements IGrowthRequirementBuilder {

        private final GrowthRequirement growthRequirement;

        public Builder() {
            this.growthRequirement = new GrowthRequirement();
        }

        /** Adds a required block to this GrowthRequirement instance */
        @Override
        public Builder requiredBlock(BlockWithMeta requiredBlock, RequirementType requiredType, boolean oreDict) {
            if (requiredBlock == null || requiredType == RequirementType.NONE) {
                throw new IllegalArgumentException("Required block must be not null and required type must be other than NONE.");
            }
            growthRequirement.setRequiredBlock(requiredBlock, requiredType, oreDict);
            return this;
        }

        /** Sets the required soil */
        @Override
        public Builder soil(BlockWithMeta block) {
            growthRequirement.setSoil(block);
            addSoil(block);
            return this;
        }

        @Override
        public Builder brightnessRange(int min, int max) {
            this.growthRequirement.setBrightnessRange(Math.max(0, min), Math.min(16, max));
            return this;
        }

        @Override
        public IGrowthRequirement build() {
            return growthRequirement;
        }
    }
}
