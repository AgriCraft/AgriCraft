package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.*;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
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
    public static Block blockPeripheral;
    public static Block blockFence;
    public static Block blockFenceGate;
    public static Block blockGrate;

    public static void init() {
        blockCrop = new BlockCrop();
        blockSeedAnalyzer = new BlockSeedAnalyzer();
        blockWaterPad = new BlockWaterPad();
        blockWaterPadFull = new BlockWaterPadFull();
        if(!ConfigurationHandler.disableIrrigation) {
            blockWaterTank = new BlockWaterTank();
            blockWaterChannel = new BlockWaterChannel();
            blockWaterChannelFull = new BlockWaterChannelFull();
            blockChannelValve = new BlockChannelValve();
            blockSprinkler = new BlockSprinkler();
        }
        if(!ConfigurationHandler.disableSeedStorage) {
            blockSeedStorage = new BlockSeedStorage();
            if(!ConfigurationHandler.disableSeedWarehouse) {
                //blockSeedStorageController = new BlockSeedStorageController();
            }
        }
        if(ModHelper.allowIntegration(Names.Mods.computerCraft) || ModHelper.allowIntegration(Names.Mods.openComputers)) {
            blockPeripheral = new BlockPeripheral();
        }
        if(!ConfigurationHandler.disableFences) {
            blockFence = new BlockFence();
            blockFenceGate = new BlockFenceGate();
        }
        if(!ConfigurationHandler.disableGrates) {
            blockGrate = new BlockGrate();
        }
        LogHelper.debug("Blocks registered");
    }
}
