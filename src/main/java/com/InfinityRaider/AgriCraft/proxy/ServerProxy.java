package com.InfinityRaider.AgriCraft.proxy;

import net.minecraft.item.ItemStack;

public class ServerProxy extends CommonProxy {
    @Override
    public ClientProxy getClientProxy() {return null;}

    @Override
    public void registerRenderers() {}

    @Override
    public void hideItemInNEI(ItemStack stack) {}

    @Override
    public void registerEventHandlers() {super.registerEventHandlers();}
}
