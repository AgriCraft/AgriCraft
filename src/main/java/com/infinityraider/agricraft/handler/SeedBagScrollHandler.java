package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.agricraft.network.MessageSyncSeedBagSortMode;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public Player getPlayer() {
        return AgriCraft.instance.getClientPlayer();
    }

    public boolean tryCycleSortMode(InteractionHand hand, int delta) {
        ItemStack stack = this.getPlayer().getItemInHand(hand);
        if(stack.getItem() instanceof ItemSeedBag) {
            ItemSeedBag bag = (ItemSeedBag) stack.getItem();
            if(bag.incrementSorter(stack, delta)) {
                new MessageSyncSeedBagSortMode(hand, bag.getContents(stack).getSorterIndex()).sendToServer();
                SeedBagShakeHandler.getInstance().shake(hand);
                ItemSeedBag.Contents contents = bag.getContents(stack);
                MutableComponent message = new TextComponent("")
                        .append(contents.getSorter().describe())
                        .append(new TextComponent(", "))
                        .append(AgriToolTips.MSG_SEED_BAG_SHAKE);
                if(contents.getCount() <= 0) {
                    message.append(new TextComponent(" ")).append(AgriToolTips.MSG_SEED_BAG_EMPTY);
                }
                AgriCraft.instance.getClientPlayer().sendMessage(message, Util.NIL_UUID);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        if(this.getPlayer().isDiscrete()) {
            int delta = (int) -event.getScrollDelta();
            boolean main = this.tryCycleSortMode(InteractionHand.MAIN_HAND, delta);
            boolean off = this.tryCycleSortMode(InteractionHand.OFF_HAND, delta);
            if(main || off) {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }
}
