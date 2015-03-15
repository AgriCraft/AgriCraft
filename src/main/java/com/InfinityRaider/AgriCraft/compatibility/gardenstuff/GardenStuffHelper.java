package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.jaquadro.minecraft.gardencore.block.tile.TileEntityGarden;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GardenStuffHelper {
    public static BlockWithMeta getSoil(TileEntityGarden garden) {
        ItemStack substrate = garden.getSubstrate();
        BlockWithMeta block = null;
        if(substrate!=null && substrate.getItem()!=null && substrate.getItem() instanceof ItemBlock) {
            block = new BlockWithMeta(((ItemBlock) substrate.getItem()).field_150939_a, substrate.getItemDamage());
        }
        return block;
    }

    public static void addDebugInfo(List<String> list, TileEntityGarden garden) {
        list.add("Substrate: "+ garden.getSubstrate().getDisplayName());
        list.add("Reachable contents: ");
        List<ItemStack> contents = garden.getReachableContents();
        for(ItemStack content:contents) {
            list.add(" - "+content.getDisplayName());
        }
    }
}

