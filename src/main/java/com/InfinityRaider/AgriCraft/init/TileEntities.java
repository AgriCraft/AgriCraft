package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.*;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntities {
    public static void init() {
        //GameRegistry.registerTileEntity(TileEntityAgricraft.class, Reference.MOD_ID+':'+ Names.TileEntity.tileEntity+'_'+Reference.MOD_ID);
        //GameRegistry.registerTileEntity(TileEntityCustomWood.class, getName("customWood"));
        GameRegistry.registerTileEntity(TileEntityCrop.class, getName(Names.Objects.crop));
        GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, getName(Names.Objects.seedAnalyzer));
        if(!ConfigurationHandler.disableIrrigation) {
            GameRegistry.registerTileEntity(TileEntityTank.class, getName(Names.Objects.tank));
            GameRegistry.registerTileEntity(TileEntityChannel.class, getName(Names.Objects.channel));
            GameRegistry.registerTileEntity(TileEntityChannelFull.class, getName(Names.Objects.channelFull));
            GameRegistry.registerTileEntity(TileEntityValve.class, getName(Names.Objects.valve));
            GameRegistry.registerTileEntity(TileEntitySprinkler.class, getName(Names.Objects.sprinkler));
        }
        if(!ConfigurationHandler.disableSeedStorage) {
            GameRegistry.registerTileEntity(TileEntitySeedStorage.class, getName(Names.Objects.seedStorage));
            if(!ConfigurationHandler.disableSeedWarehouse) {
                GameRegistry.registerTileEntity(TileEntitySeedStorageController.class, getName(Names.Objects.seedStorageController));
            }
        }
        if(ModHelper.allowIntegration(Names.Mods.computerCraft)) {
            GameRegistry.registerTileEntity(TileEntityPeripheral.class, getName(Names.Objects.peripheral));
        }
        LogHelper.debug("Tile Entities registered");
    }

    private static String getName(String name) {
        return Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + name;
    }
}
