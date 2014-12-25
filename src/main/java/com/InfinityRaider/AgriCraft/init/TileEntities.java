package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.*;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
    public static void init() {
        GameRegistry.registerTileEntity(TileEntityAgricraft.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+Reference.MOD_ID);
        GameRegistry.registerTileEntity(TileEntityCrop.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.crop);
        GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.seedAnalyzer);
        GameRegistry.registerTileEntity(TileEntityCustomWood.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+"customWood");
        GameRegistry.registerTileEntity(TileEntityTank.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.tank);
        GameRegistry.registerTileEntity(TileEntityChannel.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.channel);
        GameRegistry.registerTileEntity(TileEntitySprinkler.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.sprinkler);
        LogHelper.info("Tile Entities registered");
    }
}
