package com.InfinityRaider.AgriCraft.proxy;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.PlayerInteractEventHandler;
import com.InfinityRaider.AgriCraft.handler.PlayerConnectToServerHandler;
import com.InfinityRaider.AgriCraft.utility.RenderLogger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
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

        PlayerConnectToServerHandler playerConnectToServerHandler = new PlayerConnectToServerHandler();
        FMLCommonHandler.instance().bus().register(playerConnectToServerHandler);
        MinecraftForge.EVENT_BUS.register(playerConnectToServerHandler);

        if (ConfigurationHandler.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
    }

    @Override
    public void initConfiguration(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event);
    }
}
