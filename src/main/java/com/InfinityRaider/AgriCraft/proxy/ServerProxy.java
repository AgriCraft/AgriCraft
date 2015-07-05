package com.InfinityRaider.AgriCraft.proxy;

import net.minecraft.item.ItemStack;

public class ServerProxy extends CommonProxy {
    @Override
    public void registerRenderers() {}

    @Override
    public void initNEI() {}

    @Override
    public void hideItemInNEI(ItemStack stack) {}

    @Override
    public void registerEventHandlers() {super.registerEventHandlers();}

    @Override
    public int getRenderId(int nr) {
        return 0;
    }

    @Override
    public void registerVillagerSkin(int id, String resource) {
    }
}
