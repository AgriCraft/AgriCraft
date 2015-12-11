package com.InfinityRaider.AgriCraft.reference;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Data {
    //give data in this order: {String name, Item fruit, int fuitMeta, Block soil, Block base, int baseMeta, int tier, int renderType}
    public static final Object[][] defaults = {
            {"Potato", new ItemStack(Items.potato), 1, RenderMethod.HASHTAG},
            {"Carrot", new ItemStack(Items.carrot), 1, RenderMethod.HASHTAG},
            {"Sugarcane", new ItemStack(Items.reeds), new BlockWithMeta(Blocks.sand), 1, RenderMethod.HASHTAG},
            {"Dandelion", new ItemStack(Items.dye, 1, 11), 1, RenderMethod.HASHTAG, new ItemStack(Blocks.yellow_flower)} ,
            {"Poppy", new ItemStack(Items.dye, 1, 1), 1, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower)},
            {"Orchid", new ItemStack(Items.dye, 1, 12), 1, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 1)},
            {"Allium", new ItemStack(Items.dye, 1, 13), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 2)},
            {"TulipRed", new ItemStack(Items.dye, 1, 1), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 4)},
            {"TulipOrange", new ItemStack(Items.dye, 1, 14), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 5)},
            {"TulipWhite", new ItemStack(Items.dye, 1, 7), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 6)},
            {"TulipPink", new ItemStack(Items.dye, 1, 9), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 7)},
            {"Daisy", new ItemStack(Items.dye, 1, 7), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.red_flower, 1, 8)},
            {"Cactus",  new ItemStack(net.minecraft.init.Items.dye, 1, 2),  new BlockWithMeta(Blocks.sand), 2, RenderMethod.HASHTAG, new ItemStack(Blocks.cactus)},
            {"ShroomRed", new ItemStack(Item.getItemFromBlock(Blocks.red_mushroom)), new BlockWithMeta(Blocks.mycelium), 2, RenderMethod.HASHTAG, new int[]{0, 8}},
            {"ShroomBrown", new ItemStack(Item.getItemFromBlock(Blocks.brown_mushroom)), new BlockWithMeta(Blocks.mycelium), 2, RenderMethod.HASHTAG, new int[]{0,8}}
    };

    public static final String[] botania = {
            "BotaniaWhite",
            "BotaniaOrange",
            "BotaniaMagenta",
            "BotaniaLightBlue",
            "BotaniaYellow",
            "BotaniaLime",
            "BotaniaPink",
            "BotaniaGray",   //I'm in favor of the British grey, but I'm just being consistent with Vazkii being consistent with Vanilla
            "BotaniaLightGray",
            "BotaniaCyan",
            "BotaniaPurple",
            "BotaniaBlue",
            "BotaniaBrown",
            "BotaniaGreen",
            "BotaniaRed",
            "BotaniaBlack"
    };

    public static final String[] vanillaNuggets = {
            "Iron",
            "Emerald",
            "Diamond",
            "Quartz",
    };

    public static final String[][] modResources = {
            {"Copper", "Cuprosia"},
            {"Tin", "Petinia"},
            {"Lead", "Plombean"},
            {"Silver", "Silverweed"},
            {"Aluminum", "Jaslumine"},
            {"Nickel", "Niccissus"},
            {"Platinum", "Platiolus"},
            {"Osmium", "Osmonium"}
    };
}
