package com.InfinityRaider.AgriCraft.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.ItemStack;

public interface IProxy {

    public void registerTileEntities();

    public void registerRenderers();

    public void initNEI();

    public void hideItemInNEI(ItemStack stack);

    public void registerEventHandlers();

    public int getRenderId(int nr);

    public void registerVillagerSkin(int id, String resource);

    public void initConfiguration(FMLPreInitializationEvent event);
}
