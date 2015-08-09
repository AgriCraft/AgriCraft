package com.InfinityRaider.AgriCraft.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IProxy {
    EntityPlayer getClientPlayer();

    void registerRenderers();

    void initNEI();

    void hideItemInNEI(ItemStack stack);

    void registerEventHandlers();

    int getRenderId(Block block);

    void registerVillagerSkin(int id, String resource);

    void initConfiguration(FMLPreInitializationEvent event);
}
