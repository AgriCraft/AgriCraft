package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncSeedBagSortMode extends MessageBase {
    private Hand hand;
    private int index;

    public MessageSyncSeedBagSortMode() {
        super();
    }

    public MessageSyncSeedBagSortMode(Hand hand, int index) {
        this();
        this.hand = hand;
        this.index = index;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(this.hand != null) {
            ItemStack stack = ctx.getSender().getHeldItem(this.hand);
            if(stack.getItem() instanceof ItemSeedBag) {
                ((ItemSeedBag) stack.getItem()).getContents(stack).setSorterIndex(this.index);
            }
        }
    }
}
