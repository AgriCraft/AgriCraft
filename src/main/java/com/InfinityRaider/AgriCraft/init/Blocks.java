package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedAnalyzer;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterTank;
import com.InfinityRaider.AgriCraft.items.ItemBlockTank;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.registry.GameRegistry;

public class Blocks {
    public static BlockCrop blockCrop;
    public static BlockSeedAnalyzer seedAnalyzer;
    public static BlockWaterTank blockWaterTank;

    public static void init() {
        blockCrop = new BlockCrop();
        seedAnalyzer = new BlockSeedAnalyzer();
        blockWaterTank = new BlockWaterTank();
        RegisterHelper.registerBlock(blockCrop, Names.crops);
        RegisterHelper.registerBlock(seedAnalyzer, Names.seedAnalyzer);
        //blockWaterTank.setBlockName(Reference.MOD_ID.toLowerCase()+':'+Names.tank);
        //GameRegistry.registerBlock(blockWaterTank, ItemBlockTank.class, Names.tank);
        LogHelper.info("Blocks registered");
    }
}
