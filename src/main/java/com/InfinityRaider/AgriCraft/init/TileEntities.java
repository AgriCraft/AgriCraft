package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityChannel;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntitySprinkler;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityTank;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
    public static void init() {
        GameRegistry.registerTileEntity(TileEntityAgricraft.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+Reference.MOD_ID);
        GameRegistry.registerTileEntity(TileEntityCrop.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.crop);
        GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+ Names.Objects.seedAnalyzer);
        GameRegistry.registerTileEntity(TileEntityCustomWood.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+"customWood");
        if(!ConfigurationHandler.disableIrrigation) {
            GameRegistry.registerTileEntity(TileEntityTank.class, Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + Names.Objects.tank);
            GameRegistry.registerTileEntity(TileEntityChannel.class, Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + Names.Objects.channel);
            GameRegistry.registerTileEntity(TileEntityValve.class, Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + Names.Objects.valve);
            GameRegistry.registerTileEntity(TileEntitySprinkler.class, Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + Names.Objects.sprinkler);
        }
        /*
        if(!ConfigurationHandler.disableSeedStorage) {
            GameRegistry.registerTileEntity(TileEntitySeedStorage.class, Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + Names.Objects.seedStorage);
            if(!ConfigurationHandler.disableSeedWarehouse) {
                GameRegistry.registerTileEntity(TileEntitySeedStorageController.class, Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + Names.Objects.seedStorageController);
            }
        }
        */
        LogHelper.debug("Tile Entities registered");
    }
}
