package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BonemealHandler {
    private static final BonemealHandler INSTANCE = new BonemealHandler();

    public static BonemealHandler getInstance() {
        return INSTANCE;
    }

    private BonemealHandler() {}

    /*
     * Event handler to deny bonemeal while sneaking on crops that are not
     * allowed to have bonemeal applied to them
     */
    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void denyBonemeal(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntityLiving().isSneaking()) {
            return;
        }
        ItemStack heldItem = event.getEntityLiving().getActiveItemStack();
        if (!heldItem.isEmpty() && heldItem.getItem() == Items.BONE_MEAL) {
            AgriApi.getCrop(event.getWorld(), event.getPos()).ifPresent(crop -> event.setUseItem(Event.Result.DENY));
        }
    }
}
