package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SeedBagEnchantingHandler {
    private static final SeedBagEnchantingHandler INSTANCE = new SeedBagEnchantingHandler();

    public static SeedBagEnchantingHandler getInstance() {
        return INSTANCE;
    }

    private SeedBagEnchantingHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onEnchant(EnchantmentLevelSetEvent event) {
        if(event.getItem().getItem() instanceof ItemSeedBag) {
            event.setLevel(AgriCraft.instance.getConfig().seedBagEnchantCost());
        }
    }
}
