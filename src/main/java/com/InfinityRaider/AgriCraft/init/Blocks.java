package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedAnalyzer;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterTank;
import com.InfinityRaider.AgriCraft.items.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;

public class Blocks {
    public static BlockCrop blockCrop;
    public static BlockSeedAnalyzer seedAnalyzer;
    public static BlockWaterTank blockWaterTank;
    public static BlockWaterChannel blockWaterChannel;

    public static void init() {
        blockCrop = new BlockCrop();
        seedAnalyzer = new BlockSeedAnalyzer();
        blockWaterTank = new BlockWaterTank();
        blockWaterChannel = new BlockWaterChannel();

        RegisterHelper.registerBlock(blockCrop, Names.crops);
        RegisterHelper.registerBlock(seedAnalyzer, Names.seedAnalyzer);
        RegisterHelper.registerBlock(blockWaterTank, Names.tank, ItemBlockCustomWood.class);
        RegisterHelper.registerBlock(blockWaterChannel, Names.channel, ItemBlockCustomWood.class);
        LogHelper.info("Blocks registered");
    }
}
