package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.*;

/**
 * Holds all the default soils and soil.
 * Also holds all GrowthRequirements.
 */
public class GrowthRequirements {

    private static Map<ItemSeeds, Map<Integer, GrowthRequirement>> overrides = new HashMap<ItemSeeds, Map<Integer, GrowthRequirement>>();

    // Package private so GrowthRequirement can access it
    static Set<BlockWithMeta> defaultSoils = new HashSet<BlockWithMeta>();
    static Set<BlockWithMeta> soils = new HashSet<BlockWithMeta>();

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
            if (success) {
                soils.add(new BlockWithMeta(block, stack.getItemDamage()));
            } else {
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

    /** Finds the growth requirement for a seed */
    public static GrowthRequirement getGrowthRequirement(ItemSeeds seed, int meta) {
        if(SeedHelper.getPlant(seed) instanceof BlockModPlant) {
            return ((BlockModPlant) SeedHelper.getPlant(seed)).getGrowthRequirement();
        }
        else if (overrides.get(seed)!=null && overrides.get(seed).get(meta)!=null) {
            return overrides.get(seed).get(meta);
        }
        return GrowthRequirement.DEFAULT;
    }

    /** Removes the requirement for a seed */
    public static void resetGrowthRequirement(ItemSeeds seed, int meta) {
        if(seed instanceof ItemModSeed) {
            ((ItemModSeed) seed).getPlant().setGrowthRequirement(GrowthRequirement.DEFAULT);
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
}
