package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.handler.PlayerInteractEventHandler;
import com.InfinityRaider.AgriCraft.init.TileEntities;
import net.minecraftforge.common.MinecraftForge;

public abstract class CommonProxy implements IProxy {
    private int cropRenderID = -1;

    public int getCropRenderID() {
        return cropRenderID;
    }

    public void registerTileEntities() {
        TileEntities.init();
    }

    public void registerEventHandlers() {
        PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler();

        MinecraftForge.EVENT_BUS.register(playerInteractEventHandler);
    }
}
