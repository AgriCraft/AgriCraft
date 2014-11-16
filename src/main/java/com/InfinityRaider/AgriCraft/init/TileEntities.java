package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
    public static void init() {
        GameRegistry.registerTileEntity(TileEntityCrop.class, "AgriCraft:TileEntityCrop");
        GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, "AgriCraft:TileEntitySeedAnalyzer");
        GameRegistry.registerTileEntity(TileEntityTank.class, "AgriCraft:TileEntityTank");

        LogHelper.info("Tile Entities registered");
    }
}
