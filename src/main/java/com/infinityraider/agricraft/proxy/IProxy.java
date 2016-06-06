package com.infinityraider.agricraft.proxy;

import com.infinityraider.agricraft.handler.PlayerConnectToServerHandler;
import com.infinityraider.agricraft.handler.PlayerInteractEventHandler;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.utility.RenderLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("unused")
public interface IProxy {
    /** Returns the physical side, is always Side.SERVER on the server and Side.CLIENT on the client */
    Side getPhysicalSide();

    /** Returns the effective side, on the server, this is always Side.SERVER, on the client it is dependent on the thread */
    Side getEffectiveSide();

    /** Returns the instance of the EntityPlayer on the client, null on the server */
    EntityPlayer getClientPlayer();

    /** Returns the client World object on the client, null on the server */
    World getClientWorld();

    /** Returns the World object corresponding to the dimension id */
    World getWorldByDimensionId(int dimension);

    /** Returns the entity in that dimension with that id */
	default Entity getEntityById(int dimension, int id) {
        return getEntityById(getWorldByDimensionId(dimension), id);
    }

    /** Returns the entity in that World object with that id */
	default Entity getEntityById(World world, int id) {
        return world.getEntityByID(id);
    }

    /** Registers the renderers on the client, does nothing on the server */
    void registerRenderers();

    /** Initializes the NEI configuration */
    void initNEI();

    /** Hides an ItemStack from NEI on the client, does nothing on the server */
    void hideItemInNEI(ItemStack stack);

    /** Registers all the needed event handlers to the correct event bus */
	default public void registerEventHandlers() {
        PlayerInteractEventHandler playerInteractEventHandler = new PlayerInteractEventHandler();
        MinecraftForge.EVENT_BUS.register(playerInteractEventHandler);

        PlayerConnectToServerHandler playerConnectToServerHandler = new PlayerConnectToServerHandler();
        FMLCommonHandler.instance().bus().register(playerConnectToServerHandler);
        MinecraftForge.EVENT_BUS.register(playerConnectToServerHandler);

        if (AgriCraftConfig.debug) {
            FMLCommonHandler.instance().bus().register(new RenderLogger());
        }
    }

    /** Registers a villager skin on the client, does nothing on the server */
    void registerVillagerSkin(int id, String resource);

    /** Initializes the configuration file */
	default void initConfiguration(FMLPreInitializationEvent event) {
        // Move along! Nothing to see here!
    }

    /** Queue a task */
    void queueTask(Runnable task);
}
