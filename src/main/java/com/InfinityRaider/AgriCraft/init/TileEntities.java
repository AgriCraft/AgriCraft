package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.*;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
    public static void init() {
        GameRegistry.registerTileEntity(TileEntityAgricraft.class, Reference.MOD_ID+':'+Names.tileEntity+'_'+Reference.MOD_ID);
        GameRegistry.registerTileEntity(TileEntityCrop.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.crop);
        GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.seedAnalyzer);
        GameRegistry.registerTileEntity(TileEntityCustomWood.class, Reference.MOD_ID+':'+Names.tileEntity+'_'+"customWood");
        GameRegistry.registerTileEntity(TileEntityTank.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.tank);
        GameRegistry.registerTileEntity(TileEntityChannel.class, Reference.MOD_ID+':'+ Names.tileEntity+'_'+Names.channel);
        GameRegistry.registerTileEntity(TileEntitySprinkler.class, Reference.MOD_ID+':'+Names.tileEntity+'_'+Names.sprinkler);

        LogHelper.info("Tile Entities registered");
    }
}
