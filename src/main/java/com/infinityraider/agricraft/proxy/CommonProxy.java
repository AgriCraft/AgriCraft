package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.handler.PlayerInteractEventHandler;
import com.infinityraider.agricraft.utility.RenderLogger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy implements IProxy {
    @Override
    public void registerEventHandlers() {
        PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler();
        MinecraftForge.EVENT_BUS.register(playerInteractEventHandler);

        if (AgriCraftConfig.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        // Move along! Nothing to see here!
    }
}
