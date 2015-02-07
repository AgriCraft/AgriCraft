package com.InfinityRaider.AgriCraft.farming;


import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SoilWhitelist {

    /** List of ItemStacks which all contain ItemBlocks */
    private static final List<ItemStack> soilWhitelist = new ArrayList<ItemStack>();

    static {
        soilWhitelist.add(new ItemStack(Blocks.farmland));
    }

    public static List<ItemStack> getWhitelist() {
        return soilWhitelist;
    }

    public static boolean isSoilFertile(Block block, int meta) {
        if (block instanceof BlockFarmland) {
            return true;
        } else {
            for (ItemStack soil : soilWhitelist) {
                Block soilBlock = ((ItemBlock) soil.getItem()).field_150939_a;
                if (block == soilBlock && meta == soil.getItemDamage()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void initSoils() {
        //reads custom entries
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readSoils());
        for(String line:data) {
            LogHelper.debug("parsing " + line);
            ItemStack stack = IOHelper.getStack(line);
            Block block = (stack!=null && stack.getItem() instanceof ItemBlock)?((ItemBlock) stack.getItem()).field_150939_a:null;
            boolean success = block!=null;
            String errorMsg = "Invalid block";
            if(success && !soilWhitelist.contains(stack)) {
                soilWhitelist.add(stack);
            }
            else {
                LogHelper.info("Error when adding block to soil whitelist: "+errorMsg+" (line: "+line+")");
            }
        }

        LogHelper.info("Registered soil whitelist:");
        for (ItemStack soil : soilWhitelist) {
            Block soilBlock = ((ItemBlock) soil.getItem()).field_150939_a;
            LogHelper.info(" - " + Block.blockRegistry.getNameForObject(soilBlock) + ":" + soil.getItemDamage());
        }
    }

    public static void addAllToSoilWhitelist(Collection<? extends ItemStack> soils) {
        soilWhitelist.addAll(soils);
    }

    public static void removeAllFromSoilWhitelist(Collection<? extends ItemStack> soils) {
        soilWhitelist.removeAll(soils);
    }
}
