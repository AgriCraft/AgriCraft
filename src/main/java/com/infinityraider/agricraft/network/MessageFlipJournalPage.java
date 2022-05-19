package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageFlipJournalPage extends MessageBase {
    private int page;
    private InteractionHand hand;

    public MessageFlipJournalPage() {
        super();
    }

    public MessageFlipJournalPage(int page, InteractionHand hand) {
        this();
        this.page = page;
        this.hand = hand;
    }

    public int getPage() {
        return this.page;
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_SERVER;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        if(ctx.getSender() == null) {
            return;
        }
        ItemStack journalStack = ctx.getSender().getItemInHand(this.getHand());
        if(journalStack.getItem() instanceof IAgriJournalItem) {
            IAgriJournalItem journalItem = (IAgriJournalItem) journalStack.getItem();
            int currentPage = journalItem.getCurrentPageIndex(journalStack);
            journalItem.setCurrentPageIndex(journalStack, this.getPage());
            int newPage = journalItem.getCurrentPageIndex(journalStack);
            if(newPage != currentPage) {
                journalItem.getPages(journalStack).get(newPage).onPageOpened(ctx.getSender(), journalStack, journalItem);
            }
        }
    }
}
