package com.InfinityRaider.AgriCraft.init;

import net.minecraft.block.Block;

import com.InfinityRaider.AgriCraft.blocks.BlockChannelValve;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockFence;
import com.InfinityRaider.AgriCraft.blocks.BlockFenceGate;
import com.InfinityRaider.AgriCraft.blocks.BlockGrate;
import com.InfinityRaider.AgriCraft.blocks.BlockPeripheral;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedAnalyzer;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedStorage;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedStorageController;
import com.InfinityRaider.AgriCraft.blocks.BlockSprinkler;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannelFull;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterPad;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterPadFull;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterTank;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

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
                blockSeedStorageController = new BlockSeedStorageController();
            }
        }
        if(ModHelper.allowIntegration(Names.Mods.computerCraft)) {
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
