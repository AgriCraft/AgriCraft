package com.InfinityRaider.AgriCraft.proxy;

import net.minecraft.item.ItemStack;

public interface IProxy {

    public void registerTileEntities();

    public void registerRenderers();

    public void initNEI();

    public void hideItemInNEI(ItemStack stack);

    public void registerEventHandlers();
}
