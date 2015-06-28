package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.blocks.*;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaRegistry {
    public static void initWaila(IWailaRegistrar registry) {
        //crops
        IWailaDataProvider cropProvider = new AgriCraftCropDataProvider();
        registry.registerStackProvider(cropProvider, BlockCrop.class);
        registry.registerBodyProvider(cropProvider, BlockCrop.class);

        if(!ConfigurationHandler.disableIrrigation) {
            //tanks
            IWailaDataProvider tankProvider = new AgriCraftTankDataProvider();
            registry.registerStackProvider(tankProvider, BlockWaterTank.class);
            registry.registerBodyProvider(tankProvider, BlockWaterTank.class);
            registry.registerNBTProvider(tankProvider, BlockWaterTank.class);

            //channels
            IWailaDataProvider channelProvider = new AgriCraftChannelDataProvider();
            registry.registerStackProvider(channelProvider, BlockWaterChannel.class);
            registry.registerBodyProvider(channelProvider, BlockWaterChannel.class);
            registry.registerNBTProvider(channelProvider, BlockWaterChannel.class);

            registry.registerStackProvider(channelProvider, BlockWaterChannelFull.class);
            registry.registerBodyProvider(channelProvider, BlockWaterChannelFull.class);
            registry.registerNBTProvider(channelProvider, BlockWaterChannelFull.class);

            //valves
            IWailaDataProvider valveProvider = new AgriCraftValveDataProvider();
            registry.registerStackProvider(valveProvider, BlockChannelValve.class);
            registry.registerBodyProvider(valveProvider, BlockChannelValve.class);
            registry.registerNBTProvider(valveProvider, BlockChannelValve.class);
        }
    }
}
