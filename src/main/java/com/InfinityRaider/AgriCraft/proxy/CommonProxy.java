package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.handler.BonemealEventHandler;
import com.InfinityRaider.AgriCraft.handler.PlayerInteractEventHandler;
import com.InfinityRaider.AgriCraft.init.TileEntities;
import net.minecraftforge.common.MinecraftForge;

public abstract class CommonProxy implements IProxy {
    public void registerTileEntities() {
        TileEntities.init();
    }

    public void registerEventHandlers() {
        BonemealEventHandler bonemealEventHandler = new BonemealEventHandler();
        PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler();

        MinecraftForge.EVENT_BUS.register(bonemealEventHandler);
        MinecraftForge.EVENT_BUS.register(playerInteractEventHandler);
    }
}
