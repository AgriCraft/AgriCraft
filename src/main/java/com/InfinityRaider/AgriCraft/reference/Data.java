package com.InfinityRaider.AgriCraft.reference;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class Data {
    //give data in this order: {String name, Item fruit, int fuitMeta, Block soil, Block base, int baseMeta, int tier, int renderType}
    public static final Object[][] defaults = {
            {"Potato", Items.potato, 0, null, null, 0, 1, 6},
            {"Carrot", Items.carrot, 0, null, null, 0, 1, 6},
            {"Melon", Items.melon, 0, null, null, 0, 1, 6},
            {"Pumpkin", Item.getItemFromBlock(Blocks.pumpkin), 0, null, null, 0, 1, 6},
            {"Sugarcane", Items.reeds, 0, Blocks.sand, null, 0, 1, 6},
            {"Dandelion", Items.dye, 11, null, null, 0, 1, 6} ,
            {"Poppy", Items.dye, 1, null, null, 0, 1, 6},
            {"Orchid", Items.dye, 12, null, null, 0, 1, 6},
            {"Allium", Items.dye, 13, null, null, 0, 2, 6},
            {"TulipRed", Items.dye, 1, null, null, 0, 2, 6},
            {"TulipOrange", Items.dye, 14,  null, null, 0, 2, 6},
            {"TulipWhite", Items.dye, 7,  null, null, 0, 2, 6},
            {"TulipPink", Items.dye, 9,  null, null, 0, 2, 6},
            {"Daisy", Items.dye, 7,  null, null, 0, 2, 6},
            {"Cactus", ConfigurationHandler.cactusGivesCactus?Item.getItemFromBlock(Blocks.cactus):net.minecraft.init.Items.dye, ConfigurationHandler.cactusGivesCactus?0:2,  Blocks.sand, null, 0, 2, 6},
            {"ShroomRed", Item.getItemFromBlock(Blocks.red_mushroom), 0,  Blocks.mycelium, null, 0, 2, 6},
            {"ShroomBrown", Item.getItemFromBlock(Blocks.brown_mushroom), 0,  Blocks.mycelium, null, 0, 2, 6}
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
            "Diamond"
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
