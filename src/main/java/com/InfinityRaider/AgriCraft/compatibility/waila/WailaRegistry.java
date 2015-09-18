package com.InfinityRaider.AgriCraft.compatibility.waila;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

import com.InfinityRaider.AgriCraft.blocks.BlockAgriCraft;

public class WailaRegistry {
    public static void initWaila(IWailaRegistrar registry) {
        //All blocks.
        IWailaDataProvider agriProvider = new AgriCraftDataProvider();
        registry.registerStackProvider(agriProvider, BlockAgriCraft.class);
        registry.registerBodyProvider(agriProvider, BlockAgriCraft.class);
    }
}
