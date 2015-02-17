package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import com.InfinityRaider.AgriCraft.utility.BlockWithMeta;
import com.jaquadro.minecraft.gardencore.block.tile.TileEntityGarden;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class GardenStuffHelper {
    public static BlockWithMeta getSoil(World world, int x, int y, int z) {
        BlockWithMeta soil = null;
        TileEntity te = world.getTileEntity(x, y, z);
        if(te!=null && te instanceof TileEntityGarden) {
            TileEntityGarden garden = (TileEntityGarden) te;
            ItemStack substrate = garden.getSubstrate();
            if (substrate != null && substrate.getItem() != null && substrate.getItem() instanceof ItemBlock){
                soil = new BlockWithMeta(((ItemBlock) substrate.getItem()).field_150939_a, substrate.getItemDamage());
            }
        }
        return soil;
    }

    public static void addDebugInfo(List<String> list, TileEntityGarden garden) {
        list.add("Substrate: "+ garden.getSubstrate().getDisplayName());
        list.add("Inventory contents:");
        List<ItemStack> contents = garden.getReachableContents();
        for(ItemStack content:contents) {
            list.add(" - "+content.getDisplayName());
        }
    }
}

