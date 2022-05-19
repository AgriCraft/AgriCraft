package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncSeedBagSortMode extends MessageBase {
    private InteractionHand hand;
    private int index;

    public MessageSyncSeedBagSortMode() {
        super();
    }

    public MessageSyncSeedBagSortMode(InteractionHand hand, int index) {
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
        if(this.hand != null && ctx.getSender() != null) {
            ItemStack stack = ctx.getSender().getItemInHand(this.hand);
            if(stack.getItem() instanceof ItemSeedBag) {
                ((ItemSeedBag) stack.getItem()).getContents(stack).setSorterIndex(this.index);
            }
        }
    }
}
