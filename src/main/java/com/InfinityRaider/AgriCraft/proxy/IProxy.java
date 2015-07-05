package com.InfinityRaider.AgriCraft.proxy;

import net.minecraft.item.ItemStack;

public interface IProxy {

    public void registerTileEntities();

    public void registerRenderers();

    public void initNEI();

    public void hideItemInNEI(ItemStack stack);

    public void registerEventHandlers();

    public int getRenderId(int nr);

    public void registerVillagerSkin(int id, String resource);
}
