package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import chococraft.common.config.ChocoCraftBlocks;
import chococraft.common.config.ChocoCraftItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class ChococraftHelper {

    private static Random rand = new Random();

    public static ArrayList<ItemStack> getFruits() {
        ArrayList<ItemStack> fruits = new ArrayList<ItemStack>();
        fruits.add(new ItemStack(ChocoCraftBlocks.gysahlGreenBlock));
        fruits.add(new ItemStack(ChocoCraftItems.gysahlLoverlyItem));
        fruits.add(new ItemStack(ChocoCraftItems.gysahlGoldenItem));
        return fruits;
    }

    public static ItemStack getFruit(int gain, int nr) {
        Block normal = ChocoCraftBlocks.gysahlGreenBlock;
        Item lovely = ChocoCraftItems.gysahlLoverlyItem;
        Item golden = ChocoCraftItems.gysahlGoldenItem;
        double random = rand.nextDouble();
        ItemStack fruitStack;
        if(gain==10) {
            Item fruit = random<0.2?golden:(random<0.6?lovely:null);
            if(fruit==null) {
                fruitStack = new ItemStack(normal, nr);
            }
            else {
                fruitStack = new ItemStack(fruit, 1);
            }
        }
        else {
            fruitStack = (random<gain*0.04?new ItemStack(lovely, 1):new ItemStack(normal, nr));
        }
        return fruitStack;
    }

    public static int transformMeta(int meta) {
        return (int) Math.ceil(((float)meta)/2.0F);
    }
}
