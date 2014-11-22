package com.InfinityRaider.AgriCraft.utility;

import com.InfinityRaider.AgriCraft.reference.Reference;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public abstract class RegisterHelper {
    public static void registerBlock(Block block,String name) {
        RegisterHelper.registerBlock(block, name, null);
    }

    public static void registerBlock(Block block,String name, Class<? extends ItemBlock> itemClass) {
        block.setBlockName(Reference.MOD_ID.toLowerCase()+':'+name);
        LogHelper.info("registering " + block.getUnlocalizedName());
        if(itemClass!=null) {
            GameRegistry.registerBlock(block, itemClass, name);
        }
        else {
            GameRegistry.registerBlock(block, name);
        }
    }

    public static void registerItem(Item item,String name) {
        item.setUnlocalizedName(Reference.MOD_ID.toLowerCase()+':'+name);
        LogHelper.info("registering " + item.getUnlocalizedName());
        GameRegistry.registerItem(item, name);
    }


}
