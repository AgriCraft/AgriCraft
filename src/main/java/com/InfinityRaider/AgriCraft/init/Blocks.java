package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.*;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;

public class Blocks {
    public static BlockCrop blockCrop;
    public static BlockSeedAnalyzer seedAnalyzer;
    public static BlockWaterTank blockWaterTank;
    public static BlockWaterChannel blockWaterChannel;
    public static BlockChannelValve blockChannelValve;
    public static BlockSprinkler blockSprinkler;
    public static BlockSeedStorage blockSeedStorage;

    public static void init() {
        blockCrop = new BlockCrop();
        RegisterHelper.registerBlock(blockCrop, Names.Objects.crops);
        seedAnalyzer = new BlockSeedAnalyzer();
        RegisterHelper.registerBlock(seedAnalyzer, Names.Objects.seedAnalyzer);
        if(ConfigurationHandler.enableSeedStorage) {
            blockSeedStorage = new BlockSeedStorage();
            RegisterHelper.registerBlock(blockSeedStorage, Names.Objects.seedStorage, ItemBlockCustomWood.class);
        }
        if(!ConfigurationHandler.disableIrrigation) {
            blockWaterTank = new BlockWaterTank();
            RegisterHelper.registerBlock(blockWaterTank, Names.Objects.tank, ItemBlockCustomWood.class);
            blockWaterChannel = new BlockWaterChannel();
            RegisterHelper.registerBlock(blockWaterChannel, Names.Objects.channel, ItemBlockCustomWood.class);
            blockChannelValve = new BlockChannelValve();
            RegisterHelper.registerBlock(blockChannelValve, Names.Objects.valve, ItemBlockCustomWood.class);
            blockSprinkler = new BlockSprinkler();
            RegisterHelper.registerBlock(blockSprinkler, Names.Objects.sprinkler);
        }
        LogHelper.info("Blocks registered");
    }
}
