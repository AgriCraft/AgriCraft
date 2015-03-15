package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import vazkii.botania.common.item.ModItems;

public class Crops {
    //AgriCraft crops
    public static BlockModPlant potato;
    public static BlockModPlant carrot;
    public static BlockModPlant melon;
    public static BlockModPlant pumpkin;
    public static BlockModPlant sugarcane;
    public static BlockModPlant dandelion;
    public static BlockModPlant poppy;
    public static BlockModPlant orchid;
    public static BlockModPlant allium;
    public static BlockModPlant tulipRed;
    public static BlockModPlant tulipOrange;
    public static BlockModPlant tulipWhite;
    public static BlockModPlant tulipPink;
    public static BlockModPlant daisy;
    public static BlockModPlant cactus;
    public static BlockModPlant shroomRed;
    public static BlockModPlant shroomBrown;
    public static BlockModPlant botaniaWhite;

    //Botania crops
    public static BlockModPlant botaniaOrange;
    public static BlockModPlant botaniaMagenta;
    public static BlockModPlant botaniaLightBlue;
    public static BlockModPlant botaniaYellow;
    public static BlockModPlant botaniaLime;
    public static BlockModPlant botaniaPink;
    public static BlockModPlant botaniaGray; //I'm in favor of the British grey, but I'm just being consistent with Vazkii being consistent with Vanilla
    public static BlockModPlant botaniaLightGray;
    public static BlockModPlant botaniaCyan;
    public static BlockModPlant botaniaPurple;
    public static BlockModPlant botaniaBlue;
    public static BlockModPlant botaniaBrown;
    public static BlockModPlant botaniaGreen;
    public static BlockModPlant botaniaRed;
    public static BlockModPlant botaniaBlack;

    public static void init() {
        potato = new BlockModPlant(net.minecraft.init.Items.potato);
        carrot = new BlockModPlant(net.minecraft.init.Items.carrot);
        melon = new BlockModPlant(net.minecraft.init.Items.melon);
        pumpkin = new BlockModPlant(Item.getItemFromBlock(Blocks.pumpkin));
        sugarcane = new BlockModPlant(net.minecraft.init.Blocks.sand, net.minecraft.init.Items.reeds);
        dandelion = new BlockModPlant(net.minecraft.init.Items.dye, 11);
        poppy = new BlockModPlant(net.minecraft.init.Items.dye, 1);
        orchid = new BlockModPlant(net.minecraft.init.Items.dye, 12);
        allium = new BlockModPlant(net.minecraft.init.Items.dye, 13);
        tulipRed = new BlockModPlant(net.minecraft.init.Items.dye, 1, 2);
        tulipOrange = new BlockModPlant(net.minecraft.init.Items.dye, 14, 2);
        tulipWhite = new BlockModPlant(net.minecraft.init.Items.dye, 7, 2);
        tulipPink = new BlockModPlant(net.minecraft.init.Items.dye, 9, 2);
        daisy = new BlockModPlant(net.minecraft.init.Items.dye, 7, 2);
        cactus = new BlockModPlant(Blocks.sand, ConfigurationHandler.cactusGivesCactus?Item.getItemFromBlock(Blocks.cactus):net.minecraft.init.Items.dye, ConfigurationHandler.cactusGivesCactus?0:2);
        shroomRed = new BlockModPlant(Blocks.mycelium, Item.getItemFromBlock(Blocks.red_mushroom));
        shroomBrown = new BlockModPlant(Blocks.mycelium, Item.getItemFromBlock(Blocks.brown_mushroom));
        RegisterHelper.registerCrop(potato, Names.Plants.potato);
        RegisterHelper.registerCrop(carrot, Names.Plants.carrot);
        RegisterHelper.registerCrop(melon, Names.Plants.melon);
        RegisterHelper.registerCrop(pumpkin, Names.Plants.pumpkin);
        RegisterHelper.registerCrop(sugarcane, Names.Plants.sugarcane);
        RegisterHelper.registerCrop(dandelion, Names.Plants.dandelion);
        RegisterHelper.registerCrop(poppy, Names.Plants.poppy);
        RegisterHelper.registerCrop(orchid, Names.Plants.orchid);
        RegisterHelper.registerCrop(allium, Names.Plants.allium);
        RegisterHelper.registerCrop(tulipRed, Names.Plants.tulip + Names.Colors.red);
        RegisterHelper.registerCrop(tulipOrange, Names.Plants.tulip + Names.Colors.orange);
        RegisterHelper.registerCrop(tulipWhite, Names.Plants.tulip + Names.Colors.white);
        RegisterHelper.registerCrop(tulipPink, Names.Plants.tulip + Names.Colors.pink);
        RegisterHelper.registerCrop(daisy, Names.Plants.daisy);
        RegisterHelper.registerCrop(cactus, Names.Plants.cactus);
        RegisterHelper.registerCrop(shroomRed, Names.Plants.shroom + Names.Colors.red);
        RegisterHelper.registerCrop(shroomBrown, Names.Plants.shroom + Names.Colors.brown);
        LogHelper.info("Crops registered");
    }
    public static void initBotaniaCrops() {
        if(ModIntegration.LoadedMods.botania && ConfigurationHandler.integration_Botania) {
            botaniaWhite = new BlockModPlant(ModItems.petal, 0, 3, 1);
            botaniaOrange = new BlockModPlant(ModItems.petal, 1, 3, 1);
            botaniaMagenta = new BlockModPlant(ModItems.petal, 2, 3, 1);
            botaniaLightBlue = new BlockModPlant(ModItems.petal, 3, 3, 1);
            botaniaYellow = new BlockModPlant(ModItems.petal, 4, 3, 1);
            botaniaLime = new BlockModPlant(ModItems.petal, 5, 3, 1);
            botaniaPink = new BlockModPlant(ModItems.petal, 6, 3, 1);
            botaniaGray = new BlockModPlant(ModItems.petal, 7, 3, 1);
            botaniaLightGray = new BlockModPlant(ModItems.petal, 8, 3, 1);
            botaniaCyan = new BlockModPlant(ModItems.petal, 9, 3, 1);
            botaniaPurple = new BlockModPlant(ModItems.petal, 10, 3, 1);
            botaniaBlue = new BlockModPlant(ModItems.petal, 11, 3, 1);
            botaniaBrown = new BlockModPlant(ModItems.petal, 12, 3, 1);
            botaniaGreen = new BlockModPlant(ModItems.petal, 13, 3, 1);
            botaniaRed = new BlockModPlant(ModItems.petal, 14, 3, 1);
            botaniaBlack = new BlockModPlant(ModItems.petal, 15, 3, 1);
            RegisterHelper.registerCrop(botaniaWhite, Names.Mods.botania + Names.Colors.white);
            RegisterHelper.registerCrop(botaniaOrange, Names.Mods.botania + Names.Colors.orange);
            RegisterHelper.registerCrop(botaniaMagenta, Names.Mods.botania + Names.Colors.magenta);
            RegisterHelper.registerCrop(botaniaLightBlue, Names.Mods.botania + Names.Colors.lightBlue);
            RegisterHelper.registerCrop(botaniaYellow, Names.Mods.botania + Names.Colors.yellow);
            RegisterHelper.registerCrop(botaniaLime, Names.Mods.botania + Names.Colors.lime);
            RegisterHelper.registerCrop(botaniaPink, Names.Mods.botania + Names.Colors.pink);
            RegisterHelper.registerCrop(botaniaGray, Names.Mods.botania + Names.Colors.gray);
            RegisterHelper.registerCrop(botaniaLightGray, Names.Mods.botania + Names.Colors.lightGray);
            RegisterHelper.registerCrop(botaniaCyan, Names.Mods.botania + Names.Colors.cyan);
            RegisterHelper.registerCrop(botaniaPurple, Names.Mods.botania + Names.Colors.purple);
            RegisterHelper.registerCrop(botaniaBlue, Names.Mods.botania + Names.Colors.blue);
            RegisterHelper.registerCrop(botaniaBrown, Names.Mods.botania + Names.Colors.brown);
            RegisterHelper.registerCrop(botaniaGreen, Names.Mods.botania + Names.Colors.green);
            RegisterHelper.registerCrop(botaniaRed, Names.Mods.botania + Names.Colors.red);
            RegisterHelper.registerCrop(botaniaBlack, Names.Mods.botania + Names.Colors.black);
            Seeds.initBotaniaSeeds();
            LogHelper.info("Botania crops registered");
        }
    }
}