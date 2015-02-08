package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;

/**
 * Encodes all requirements a plant needs to mutate and grow
 * Uses the Builder class inside to construct instances.
 */
public class GrowthRequirement {
    //static fields storing other requirements for seeds from other mods
    public static final GrowthRequirement DEFAULT = new GrowthRequirement();
    public static Map<ItemSeeds, Map<Integer, GrowthRequirement>> overrides = new HashMap<ItemSeeds, Map<Integer, GrowthRequirement>>();

    //brightness
    /** Maximum allowed brightness, exclusive **/
    private int maxBrightness = 16;
    /** Minimum allowed brightness, inclusive **/
    private int minBrightness = 8;

    //soil
    private static List<BlockWithMeta> defaultSoils = new ArrayList<BlockWithMeta>();
    private static List<BlockWithMeta> soils = new ArrayList<BlockWithMeta>();
    private BlockWithMeta soil = null;

    //block requirement
    private BlockWithMeta requiredBlock = null;
    private boolean oreDict = false;
    private RequirementType requiredType = RequirementType.NONE;
    public static enum RequirementType {
        NONE, BELOW, NEARBY
    }

    //Methods to check if a seed can grow
    //-----------------------------------
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



    //Methods to change specific requirements
    //--------------------------------------
    public BlockWithMeta getSoil() {return this.soil;}

    public void setSoil(BlockWithMeta soil) {
        this.soil = soil;
        if(!soils.contains(soil)) {
            soils.add(soil);
        }
    }

    public int[] getBrightnessRange() {return new int[] {minBrightness, maxBrightness};}

    public void setBrightnessRange(int min, int max) {
        this.minBrightness = min;
        this.maxBrightness = max;
    }



    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(Block block, int meta) {
        BlockWithMeta soil = new BlockWithMeta(block, meta);
        return soils.contains(soil) || defaultSoils.contains(soil);
    }

    public static void initSoils() {
        //add standard soils
        defaultSoils.add(new BlockWithMeta(Blocks.farmland, 7));
        if(ModIntegration.LoadedMods.forestry) {
            defaultSoils.add(new BlockWithMeta((Block) Block.blockRegistry.getObject("Forestry:soil"), 0));
        }
        //reads custom entries
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSoils());
        for(String line:data) {
            LogHelper.debug("parsing " + line);
            ItemStack stack = IOHelper.getStack(line);
            Block block = (stack!=null && stack.getItem() instanceof ItemBlock)?((ItemBlock) stack.getItem()).field_150939_a:null;
            boolean success = block!=null;
            String errorMsg = "Invalid block";
            if(success && !soils.contains(new BlockWithMeta(block, stack.getItemDamage()))) {
                soils.add(new BlockWithMeta(block, stack.getItemDamage()));
            }
            else {
                LogHelper.info("Error when adding block to soil whitelist: "+errorMsg+" (line: "+line+")");
            }
        }
        LogHelper.info("Registered soil whitelist:");
        for (BlockWithMeta soil : soils) {
            LogHelper.info(" - " + Block.blockRegistry.getNameForObject(soil.getBlock()) + ":" + soil.getMeta());
        }
    }

    public static void addAllToSoilWhitelist(Collection<? extends BlockWithMeta> list) {
        defaultSoils.addAll(list);
    }

    public static void removeAllFromSoilWhitelist(Collection<? extends  BlockWithMeta> list) {
        defaultSoils.removeAll(list);
    }



    //Methods to get/set requirements for seeds
    //-----------------------------------------
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

    /** Removes the requirement for a seed */
    public static void resetGrowthRequirement(ItemSeeds seed, int meta) {
        if(seed instanceof ItemModSeed) {
            ((ItemModSeed) seed).getPlant().setGrowthRequirement(DEFAULT);
        }
        else {
            Map<Integer, GrowthRequirement> metaMap = overrides.get(seed);
            if(metaMap!=null) {
                metaMap.remove(meta);
                if(metaMap.size()==0) {
                    overrides.remove(seed);
                }
            }
        }
    }

    /** Checks if a seed is using the default requirements */
    public static boolean hasDefault(ItemSeeds seed, int meta) {
        if(SeedHelper.getPlant(seed) instanceof BlockModPlant) {
            return false;
        }
        if (overrides.get(seed)!=null && overrides.get(seed).get(meta)!=null) {
            return false;
        }
        return true;
    }

    /** adds a new requirement to a seed */
    public static void setRequirement(ItemSeeds seed, int meta, GrowthRequirement req) {
        Map<Integer, GrowthRequirement> metaMap = overrides.get(seed);
        if(metaMap!=null) {
            metaMap.put(meta, req);
        }
        else {
            metaMap = new HashMap<Integer, GrowthRequirement>();
            metaMap.put(meta, req);
            overrides.put(seed, metaMap);
        }
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
}
