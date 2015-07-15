package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.PlayerInteractEventHandler;
import com.InfinityRaider.AgriCraft.handler.SyncMutationsHandler;
import com.InfinityRaider.AgriCraft.init.TileEntities;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public abstract class CommonProxy implements IProxy {

    @Override
    public void registerTileEntities() {
        TileEntities.init();
    }

    @Override
    public void registerEventHandlers() {
        PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler();
        MinecraftForge.EVENT_BUS.register(playerInteractEventHandler);

        SyncMutationsHandler syncMutationsHandler = new SyncMutationsHandler();
        FMLCommonHandler.instance().bus().register(syncMutationsHandler);
        MinecraftForge.EVENT_BUS.register(syncMutationsHandler);
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event);
    }
}
