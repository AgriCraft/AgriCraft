package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.*;
import com.InfinityRaider.AgriCraft.items.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;

public class Blocks {
    public static BlockCrop blockCrop;
    public static BlockSeedAnalyzer seedAnalyzer;
    public static BlockWaterTank blockWaterTank;
    public static BlockWaterChannel blockWaterChannel;
    public static BlockSprinkler blockSprinkler;

    public static void init() {
        blockCrop = new BlockCrop();
        seedAnalyzer = new BlockSeedAnalyzer();
        blockWaterTank = new BlockWaterTank();
        blockWaterChannel = new BlockWaterChannel();
        blockSprinkler = new BlockSprinkler();

        RegisterHelper.registerBlock(blockCrop, Names.crops);
        RegisterHelper.registerBlock(seedAnalyzer, Names.seedAnalyzer);
        RegisterHelper.registerBlock(blockWaterTank, Names.tank, ItemBlockCustomWood.class);
        RegisterHelper.registerBlock(blockWaterChannel, Names.channel, ItemBlockCustomWood.class);
        RegisterHelper.registerBlock(blockSprinkler, Names.sprinkler);
        LogHelper.info("Blocks registered");
    }
}
