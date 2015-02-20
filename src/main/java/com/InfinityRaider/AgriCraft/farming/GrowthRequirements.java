package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.gardenstuff.GardenStuffHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.*;
import com.jaquadro.minecraft.gardencontainers.block.BlockLargePot;
import com.jaquadro.minecraft.gardencore.block.tile.TileEntityGarden;
import com.jaquadro.minecraft.gardencore.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.*;

/**
 * Holds all the default soils and soil.
 * Also holds all GrowthRequirements.
 */
public class GrowthRequirements {

    private static Map<ItemWithMeta, GrowthRequirement> overrides = new HashMap<ItemWithMeta, GrowthRequirement>();

    // Package private so GrowthRequirement can access it
    public static Set<BlockWithMeta> defaultSoils = new HashSet<BlockWithMeta>();
    static Set<BlockWithMeta> soils = new HashSet<BlockWithMeta>();

    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y ,z);
        BlockWithMeta soil;
        if(ModIntegration.LoadedMods.gardenStuff && block instanceof BlockLargePot) {
            soil = GardenStuffHelper.getSoil((TileEntityGarden) world.getTileEntity(x, y, z));
        }
        else {
            soil = new BlockWithMeta(block, meta);
        }
        return soils.contains(soil) || defaultSoils.contains(soil);
    }

    public static void initSoils() {
        //add standard soils
        defaultSoils.add(new BlockWithMeta(Blocks.farmland, 7));
        if(ModIntegration.LoadedMods.forestry) {
            defaultSoils.add(new BlockWithMeta((Block) Block.blockRegistry.getObject("Forestry:soil"), 0));
        }
        if(ModIntegration.LoadedMods.gardenStuff) {
            defaultSoils.add(new BlockWithMeta(ModBlocks.gardenFarmland, 0));
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

    /**
     * @return growthRequirement of the given seed.
     */
    public static GrowthRequirement getGrowthRequirement(ItemSeeds seed, int meta) {
        if (SeedHelper.getPlant(seed) instanceof BlockModPlant) {
            return ((BlockModPlant) SeedHelper.getPlant(seed)).getGrowthRequirement();
        }

        GrowthRequirement growthRequirement = overrides.get(new ItemWithMeta(seed, meta));
        if (growthRequirement == null) {
            growthRequirement = new GrowthRequirement.Builder().build();
            overrides.put(new ItemWithMeta(seed, meta), growthRequirement);
        }
        return growthRequirement;
    }
}
