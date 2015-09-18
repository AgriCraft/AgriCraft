package com.InfinityRaider.AgriCraft.farming;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftSeed;
import com.InfinityRaider.AgriCraft.api.v1.IGrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.ISoilContainer;
import com.InfinityRaider.AgriCraft.api.v1.ItemWithMeta;
import com.InfinityRaider.AgriCraft.apiimpl.v1.GrowthRequirement;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.exception.InvalidSeedException;

/**
 * Holds all the default soils and soil.
 * Also holds all GrowthRequirements.
 */
public class GrowthRequirementHandler {

    private static Map<ItemWithMeta, IGrowthRequirement> growthRequirements = new HashMap<ItemWithMeta, IGrowthRequirement>();

    // Package private so GrowthRequirement can access it
    public static List<BlockWithMeta> defaultSoils = new ArrayList<BlockWithMeta>();
    static List<BlockWithMeta> soils = new ArrayList<BlockWithMeta>();

    public static void registerGrowthRequirement(ItemWithMeta item, IGrowthRequirement requirement) throws InvalidSeedException {
        if(CropPlantHandler.isValidSeed(item.toStack())) {
            throw new InvalidSeedException();
        }
        growthRequirements.put(item, requirement);
    }

    //Methods for fertile soils
    //-------------------------
    public static boolean isSoilValid(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        BlockWithMeta soil;
        if (block instanceof ISoilContainer) {
            soil = new BlockWithMeta(((ISoilContainer) block).getSoil(world, x, y, z), ((ISoilContainer) block).getSoilMeta(world, x, y, z));
        } else {
            soil = new BlockWithMeta(block, meta);
        }
        return soils.contains(soil) || defaultSoils.contains(soil);
    }

    public static void init() {
        registerSoils();
        initGrowthReqs();
        registerOverrides();
        registerCustomEntries();
    }

    private static void registerSoils() {
        addDefaultSoil(new BlockWithMeta(Blocks.farmland));
    }

    private static void initGrowthReqs() {
        //Set these crops to need darkness instead of light
        ArrayList<IGrowthRequirement> darkCrops = new ArrayList<IGrowthRequirement>();
        darkCrops.add(getGrowthRequirement((Item) Item.itemRegistry.getObject("AgriCraft:seedShroomRed"), 0));
        darkCrops.add(getGrowthRequirement((Item) Item.itemRegistry.getObject("AgriCraft:seedShroomBrown"), 0));
        if(ConfigurationHandler.resourcePlants) {
            darkCrops.add(getGrowthRequirement((Item) Item.itemRegistry.getObject("AgriCraft:seedNitorWart"), 0));
        }
        darkCrops.add(getGrowthRequirement(Items.nether_wart, 0));
        for(IGrowthRequirement req:darkCrops) {
            req.setBrightnessRange(0, 8);
        }
    }

    private static void registerOverrides() {
        //adds a growth requirement for nether wart
        GrowthRequirement netherWartReq = new GrowthRequirement.Builder().soil(new BlockWithMeta(Blocks.soul_sand)).brightnessRange(0,8).build();
        growthRequirements.put(new ItemWithMeta(Items.nether_wart, 0), netherWartReq);
    }

    private static void registerCustomEntries() {
        //reads custom entries
    	LogHelper.info("Registering soils to whitelist:");
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSoils());
        String total = " of " + data.length + ".";
        for (String line : data) {
            LogHelper.debug("  Parsing " + line + total);
            ItemStack stack = IOHelper.getStack(line);
            Block block = (stack != null && stack.getItem() instanceof ItemBlock) ? ((ItemBlock) stack.getItem()).field_150939_a : null;
            
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

    /**
     * @return growthRequirement of the given seed.
     */
    public static IGrowthRequirement getGrowthRequirement(Item seed, int meta) {
        
    	if(seed == null) {
            return null;
        }
        
        if (seed instanceof IAgriCraftSeed) {
            return ((IAgriCraftSeed) seed).getPlant().getGrowthRequirement();
        }
        
        IGrowthRequirement growthRequirement = growthRequirements.get(new ItemWithMeta(seed, meta));
        
        if (growthRequirement == null) {
            growthRequirement = new GrowthRequirement.Builder().build();
            growthRequirements.put(new ItemWithMeta(seed, meta), growthRequirement);
        }
        
        return growthRequirement;
    }

    public static IGrowthRequirement getGrowthRequirement(CropPlant plant) {
        ItemStack seed = plant.getSeed();
        return getGrowthRequirement(seed.getItem(), seed.getItemDamage());
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

}
