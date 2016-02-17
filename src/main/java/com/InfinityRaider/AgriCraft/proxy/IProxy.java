package com.infinityraider.agricraft.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings("unused")
public interface IProxy {
    /** Returns the instance of the EntityPlayer on the client, null on the server */
    EntityPlayer getClientPlayer();

    /** Returns the client World object on the client, null on the server */
    World getClientWorld();

    /** Returns the World object corresponding to the dimension id */
    World getWorldByDimensionId(int dimension);

    /** Returns the entity in that dimension with that id */
    Entity getEntityById(int dimension, int id);

    /** Returns the entity in that World object with that id */
    Entity getEntityById(World world, int id);

    /** Registers the renderers on the client, does nothing on the server */
    void registerRenderers();

    /** Initializes the NEI configuration */
    void initNEI();

    /** Hides an ItemStack from NEI on the client, does nothing on the server */
    void hideItemInNEI(ItemStack stack);

    /** Registers all the needed event handlers to the correct event bus */
    void registerEventHandlers();

    /** Registers a villager skin on the client, does nothing on the server */
    void registerVillagerSkin(int id, String resource);

    /** Initializes the configuration file */
    void initConfiguration(FMLPreInitializationEvent event);
}
