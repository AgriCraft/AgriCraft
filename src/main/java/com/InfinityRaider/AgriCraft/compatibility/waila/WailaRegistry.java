package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.blocks.BlockChannelValve;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterChannel;
import com.InfinityRaider.AgriCraft.blocks.BlockWaterTank;
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

            //channels
            IWailaDataProvider channelProvider = new AgriCraftChannelDataProvider();
            registry.registerStackProvider(channelProvider, BlockWaterChannel.class);
            registry.registerBodyProvider(channelProvider, BlockWaterChannel.class);

            //valves
            IWailaDataProvider valveProvider = new AgriCraftValveDataProvider();
            registry.registerStackProvider(valveProvider, BlockChannelValve.class);
            registry.registerBodyProvider(valveProvider, BlockChannelValve.class);
        }
    }
}
