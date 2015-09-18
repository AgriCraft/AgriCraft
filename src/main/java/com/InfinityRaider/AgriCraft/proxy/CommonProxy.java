package com.InfinityRaider.AgriCraft.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.PlayerInteractEventHandler;
import com.InfinityRaider.AgriCraft.handler.SyncMutationsHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy implements IProxy {
    @Override
    public Entity getEntityById(int dimension, int id) {
        return getEntityById(getWorldByDimensionId(dimension), id);
    }

    @Override
    public Entity getEntityById(World world, int id) {
        return world.getEntityByID(id);
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
