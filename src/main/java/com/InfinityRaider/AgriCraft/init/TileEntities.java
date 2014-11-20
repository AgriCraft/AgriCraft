package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityTank;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
    public static void init() {
        GameRegistry.registerTileEntity(TileEntityCrop.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.crop);
        GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.seedAnalyzer);
        GameRegistry.registerTileEntity(TileEntityTank.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.tank);
        GameRegistry.registerTileEntity(TileEntityChannel.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.channel);

        LogHelper.info("Tile Entities registered");
    }
}
