package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.gardenstuff.GardenStuffHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.*;
import com.jaquadro.minecraft.gardencontainers.block.BlockLargePot;
import com.jaquadro.minecraft.gardencore.block.tile.TileEntityGarden;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
    public static List<BlockWithMeta> defaultSoils = new ArrayList<BlockWithMeta>();
    static List<BlockWithMeta> soils = new ArrayList<BlockWithMeta>();

    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        BlockWithMeta soil;
        if (ModIntegration.LoadedMods.gardenStuff && block instanceof BlockLargePot) {
            soil = GardenStuffHelper.getSoil((TileEntityGarden) world.getTileEntity(x, y, z));
        } else {
            soil = new BlockWithMeta(block, meta);
        }
        return soils.contains(soil) || defaultSoils.contains(soil);
    }

    public static void init() {
        registerSoils();
        registerOverrides();
        registerCustomEntries();
    }

    private static void registerSoils() {
        addDefaultSoil(new BlockWithMeta(Blocks.farmland));
        if (ModIntegration.LoadedMods.forestry) {
            addDefaultSoil(new BlockWithMeta((Block) Block.blockRegistry.getObject("Forestry:soil"), 0));
        }
        if (ModIntegration.LoadedMods.gardenStuff) {
            addDefaultSoil(new BlockWithMeta((Block) Block.blockRegistry.getObject("GardenCore:garden_farmland"), 0));
        }
    }

    private static void registerOverrides() {
        //adds a growth requirement for nether wart
        GrowthRequirement netherWartReq = new GrowthRequirement.Builder().soil(new BlockWithMeta(Blocks.soul_sand)).brightnessRange(0,8).build();
        overrides.put(new ItemWithMeta(Items.nether_wart, 0), netherWartReq);
    }

    private static void registerCustomEntries() {
        //reads custom entries
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSoils());
        for (String line : data) {
            LogHelper.debug("parsing " + line);
            ItemStack stack = IOHelper.getStack(line);
            Block block = (stack != null && stack.getItem() instanceof ItemBlock) ? ((ItemBlock) stack.getItem()).field_150939_a : null;
            boolean success = block != null;
            String errorMsg = "Invalid block";
            if (success) {
                addDefaultSoil(new BlockWithMeta(block, stack.getItemDamage()));
            } else {
                LogHelper.info("Error when adding block to soil whitelist: " + errorMsg + " (line: " + line + ")");
            }
        }
        LogHelper.info("Registered soil whitelist:");
        for (BlockWithMeta soil : soils) {
            LogHelper.info(" - " + Block.blockRegistry.getNameForObject(soil.getBlock()) + ":" + soil.getMeta());
        }}

    public static void addAllToSoilWhitelist(Collection<? extends BlockWithMeta> list) {
        for (BlockWithMeta block : list) {
            addDefaultSoil(block);
        }
    }

    public static void removeAllFromSoilWhitelist(Collection<? extends BlockWithMeta> list) {
        defaultSoils.removeAll(list);
    }

    /**
     * @return growthRequirement of the given seed.
     */
    public static GrowthRequirement getGrowthRequirement(ItemSeeds seed, int meta) {
        if (seed instanceof IAgriCraftSeed) {
            return ((IAgriCraftSeed) seed).getPlant().getGrowthRequirement();
        }
        else if(seed instanceof ICropOverridingSeed) {
            ICropOverridingSeed overrider = (ICropOverridingSeed) seed;
            if(overrider.hasGrowthRequirement()) {
                return overrider.getGrowthRequirement();
            }
        }
        GrowthRequirement growthRequirement = overrides.get(new ItemWithMeta(seed, meta));
        if (growthRequirement == null) {
            growthRequirement = new GrowthRequirement.Builder().build();
            overrides.put(new ItemWithMeta(seed, meta), growthRequirement);
        }
        return growthRequirement;
    }

    public static void addSoil(BlockWithMeta block) {
        if (!soils.contains(block)) {
            soils.add(block);
        }
    }

    public static void addDefaultSoil(BlockWithMeta block) {
        if (!defaultSoils.contains(block)) {
            defaultSoils.add(block);
        }
    }

}
