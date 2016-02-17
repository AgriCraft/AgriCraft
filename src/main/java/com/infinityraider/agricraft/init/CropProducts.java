package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.BlockModPlant;
import com.infinityraider.agricraft.farming.CropProduce;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CropProducts {
    //Add special cases for plants here
    public static void init() {
        //poisonous potato
        CropProduce potato = ((BlockModPlant) Block.getBlockFromName("AgriCraft:cropPotato")).products;
        potato.addProduce(new ItemStack(Items.poisonous_potato), 10);
    }
}
