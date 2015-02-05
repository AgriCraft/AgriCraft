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
    public static BlockModPlant botaniaOrange;
    public static BlockModPlant botaniaMagenta;
    public static BlockModPlant botaniaLightBlue;
    public static BlockModPlant botaniaYellow;
    public static BlockModPlant botaniaLime;
    public static BlockModPlant botaniaPink;
    public static BlockModPlant botaniaGray;    //I'm in favor of the British grey, but I'm just being consistent with Vazkii being consistent with Vanilla
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
        pumpkin = new BlockModPlant(net.minecraft.item.Item.getItemFromBlock(net.minecraft.init.Blocks.pumpkin));
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
        cactus = new BlockModPlant(Blocks.sand, net.minecraft.init.Items.dye, 2);
        shroomRed = new BlockModPlant(Blocks.mycelium, Item.getItemFromBlock(Blocks.red_mushroom));
        shroomBrown = new BlockModPlant(Blocks.mycelium, Item.getItemFromBlock(Blocks.brown_mushroom));

        RegisterHelper.registerBlock(potato, Names.Crops.cropPotato);
        RegisterHelper.registerBlock(carrot, Names.Crops.cropCarrot);
        RegisterHelper.registerBlock(melon, Names.Crops.cropMelon);
        RegisterHelper.registerBlock(pumpkin, Names.Crops.cropPumpkin);
        RegisterHelper.registerBlock(sugarcane, Names.Crops.cropSugarcane);
        RegisterHelper.registerBlock(dandelion, Names.Crops.cropDandelion);
        RegisterHelper.registerBlock(poppy, Names.Crops.cropPoppy);
        RegisterHelper.registerBlock(orchid, Names.Crops.cropOrchid);
        RegisterHelper.registerBlock(allium, Names.Crops.cropAllium);
        RegisterHelper.registerBlock(tulipRed, Names.Crops.cropTulipRed);
        RegisterHelper.registerBlock(tulipOrange, Names.Crops.cropTulipOrange);
        RegisterHelper.registerBlock(tulipWhite, Names.Crops.cropTulipWhite);
        RegisterHelper.registerBlock(tulipPink, Names.Crops.cropTulipPink);
        RegisterHelper.registerBlock(daisy, Names.Crops.cropDaisy);
        RegisterHelper.registerBlock(cactus, Names.Crops.cropCactus);
        RegisterHelper.registerBlock(shroomRed, Names.Crops.cropShroomRed);
        RegisterHelper.registerBlock(shroomBrown, Names.Crops.cropShroomBrown);

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

            RegisterHelper.registerBlock(botaniaWhite, Names.Crops.cropBotaniaWhite);
            RegisterHelper.registerBlock(botaniaOrange, Names.Crops.cropBotaniaOrange);
            RegisterHelper.registerBlock(botaniaMagenta, Names.Crops.cropBotaniaMagenta);
            RegisterHelper.registerBlock(botaniaLightBlue, Names.Crops.cropBotaniaLightBlue);
            RegisterHelper.registerBlock(botaniaYellow, Names.Crops.cropBotaniaYellow);
            RegisterHelper.registerBlock(botaniaLime, Names.Crops.cropBotaniaLime);
            RegisterHelper.registerBlock(botaniaPink, Names.Crops.cropBotaniaPink);
            RegisterHelper.registerBlock(botaniaGray, Names.Crops.cropBotaniaGray);
            RegisterHelper.registerBlock(botaniaLightGray, Names.Crops.cropBotaniaLightGray);
            RegisterHelper.registerBlock(botaniaCyan, Names.Crops.cropBotaniaCyan);
            RegisterHelper.registerBlock(botaniaPurple, Names.Crops.cropBotaniaPurple);
            RegisterHelper.registerBlock(botaniaBlue, Names.Crops.cropBotaniaBlue);
            RegisterHelper.registerBlock(botaniaBrown, Names.Crops.cropBotaniaBrown);
            RegisterHelper.registerBlock(botaniaGreen, Names.Crops.cropBotaniaGreen);
            RegisterHelper.registerBlock(botaniaRed, Names.Crops.cropBotaniaRed);
            RegisterHelper.registerBlock(botaniaBlack, Names.Crops.cropBotaniaBlack);

            Seeds.initBotaniaSeeds();

            LogHelper.info("Botania crops registered");
        }
    }
}
