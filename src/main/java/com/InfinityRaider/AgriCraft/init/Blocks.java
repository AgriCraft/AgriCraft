package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.*;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import net.minecraft.block.Block;

public class Blocks {
    public static Block blockCrop;
    public static Block blockSeedAnalyzer;
    public static Block blockWaterPad;
    public static Block blockWaterPadFull;
    public static Block blockWaterTank;
    public static Block blockWaterChannel;
    public static Block blockWaterChannelFull;
    public static Block blockChannelValve;
    public static Block blockSprinkler;
    public static Block blockSeedStorage;
    public static Block blockSeedStorageController;

    public static void init() {
        blockCrop = new BlockCrop();
        RegisterHelper.registerBlock(blockCrop, Names.Objects.crops);
        blockSeedAnalyzer = new BlockSeedAnalyzer();
        RegisterHelper.registerBlock(blockSeedAnalyzer, Names.Objects.seedAnalyzer);
        blockWaterPad = new BlockWaterPad();
        RegisterHelper.registerBlock(blockWaterPad, Names.Objects.waterPad, BlockWaterPad.ItemBlockWaterPad.class);
        blockWaterPadFull = new BlockWaterPadFull();
        RegisterHelper.registerBlock(blockWaterPadFull, Names.Objects.waterPadFull, BlockWaterPadFull.ItemBlockWaterPadFull.class);
        if(!ConfigurationHandler.disableIrrigation) {
            blockWaterTank = new BlockWaterTank();
            RegisterHelper.registerBlock(blockWaterTank, Names.Objects.tank, ItemBlockCustomWood.class);
            blockWaterChannel = new BlockWaterChannel();
            RegisterHelper.registerBlock(blockWaterChannel, Names.Objects.channel, ItemBlockCustomWood.class);
            blockWaterChannelFull = new BlockWaterChannelFull();
            RegisterHelper.registerBlock(blockWaterChannelFull, Names.Objects.channelFull, ItemBlockCustomWood.class);
            blockChannelValve = new BlockChannelValve();
            RegisterHelper.registerBlock(blockChannelValve, Names.Objects.valve, ItemBlockCustomWood.class);
            blockSprinkler = new BlockSprinkler();
            RegisterHelper.registerBlock(blockSprinkler, Names.Objects.sprinkler);
        }
        if(!ConfigurationHandler.disableSeedStorage) {
            blockSeedStorage = new BlockSeedStorage();
            RegisterHelper.registerBlock(blockSeedStorage, Names.Objects.seedStorage, ItemBlockCustomWood.class);
            if(!ConfigurationHandler.disableSeedWarehouse) {
                blockSeedStorageController = new BlockSeedStorageController();
                RegisterHelper.registerBlock(blockSeedStorageController, Names.Objects.seedStorageController, ItemBlockCustomWood.class);
            }
        }
        LogHelper.debug("Blocks registered");
    }
}
