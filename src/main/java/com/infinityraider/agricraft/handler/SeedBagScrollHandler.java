package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.agricraft.network.MessageSyncSeedBagSortMode;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class SeedBagScrollHandler {
    private static final SeedBagScrollHandler INSTANCE = new SeedBagScrollHandler();

    public static SeedBagScrollHandler getInstance() {
        return INSTANCE;
    }

    private SeedBagScrollHandler() {}

    public PlayerEntity getPlayer() {
        return AgriCraft.instance.getClientPlayer();
    }

    public boolean tryCycleSortMode(Hand hand, int delta) {
        ItemStack stack = this.getPlayer().getHeldItem(hand);
        if(stack.getItem() instanceof ItemSeedBag) {
            ItemSeedBag bag = (ItemSeedBag) stack.getItem();
            if(bag.incrementSorter(stack, delta)) {
                new MessageSyncSeedBagSortMode(hand, bag.getContents(stack).getSorterIndex()).sendToServer();
                SeedBagShakeHandler.getInstance().shake(hand);
                AgriCraft.instance.getClientPlayer().sendMessage(AgriToolTips.MSG_SEED_BAG_SHAKE, Util.DUMMY_UUID);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        if(this.getPlayer().isSneaking()) {
            int delta = (int) -event.getScrollDelta();
            boolean main = this.tryCycleSortMode(Hand.MAIN_HAND, delta);
            boolean off = this.tryCycleSortMode(Hand.OFF_HAND, delta);
            if(main || off) {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }
}
