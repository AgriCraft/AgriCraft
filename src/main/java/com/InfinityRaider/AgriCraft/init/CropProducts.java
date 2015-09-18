package com.InfinityRaider.AgriCraft.init;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.farming.CropProduce;

public class CropProducts {
    //Add special cases for plants here
    public static void init() {
        //poisonous potato
        CropProduce potato = ((BlockModPlant) Block.blockRegistry.getObject("AgriCraft:cropPotato")).products;
        potato.addProduce(new ItemStack(Items.poisonous_potato), 10);
    }
}
