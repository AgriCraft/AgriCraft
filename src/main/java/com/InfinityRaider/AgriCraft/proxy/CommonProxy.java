package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.handler.BonemealEventHandler;
import com.InfinityRaider.AgriCraft.init.TileEntities;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public abstract class CommonProxy implements IProxy {
    public void registerTileEntities() {
        TileEntities.init();
    }

    public void registerEventHandlers() {
        //bonemeal event handler
        BonemealEventHandler bonemealEventHandler = new BonemealEventHandler();

        FMLCommonHandler.instance().bus().register(bonemealEventHandler);
        MinecraftForge.EVENT_BUS.register(bonemealEventHandler);
    }
}
