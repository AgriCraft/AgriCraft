package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.jaquadro.minecraft.gardencore.block.tile.TileEntityGarden;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GardenStuffHelper {
    public static BlockWithMeta getSoil(Block block, int meta) {
        return new BlockWithMeta(block, meta);
    }

    public static void addDebugInfo(List<String> list, TileEntityGarden garden) {
        list.add("Substrate: "+ garden.getSubstrate().getDisplayName());
        list.add("Inventory contents:");
        for(int i=0;i<garden.getSizeInventory();i++) {
            list.add(" - slot "+i+": " + garden.getPlantInSlot(i).getDisplayName());
        }
        list.add("Reachable contents: ");
        List<ItemStack> contents = garden.getReachableContents();
        for(ItemStack content:contents) {
            list.add(" - "+content.getDisplayName());
        }
    }
}

