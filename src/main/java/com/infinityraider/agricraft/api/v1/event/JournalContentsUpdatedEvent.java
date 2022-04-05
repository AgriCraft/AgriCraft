package com.infinityraider.agricraft.api.v1.event;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * This event is fired every time a journal's contents are updated, for instance when a new plant is discovered.
 * It can be used to modify the contents of the journal by adding, removing are modifying pages.
 * At the end of the event, the pages which are in the list will be the ones that appear in the journal.
 * Remark that this event is also fired when journal data is read from nbt.
 *
 * This event is not cancellable
 */
public class JournalContentsUpdatedEvent extends Event {
    private final ItemStack stack;
    private final IAgriJournalItem journal;
    private final List<IAgriJournalItem.IPage> pages;

    public JournalContentsUpdatedEvent(ItemStack stack, IAgriJournalItem journal, List<IAgriJournalItem.IPage> pages) {
        this.stack = stack;
        this.journal = journal;
        this.pages = pages;
    }

    /**
     * @return The ItemStack containing the journal which updated
     */
    public ItemStack getJournalStack() {
        return this.stack;
    }

    /**
     * Note: do not fetch the pages from the journal at this point as they will be overwritten
     * by the list of pages in this event after the event has fired
     *
     * @return the item representation of the journal
     */
    public IAgriJournalItem getJournalItem() {
        return this.journal;
    }

    /**
     * Add, delete or modify pages in this list to change the contents of the journal
     * @return a modifiable list of pages.
     */
    public List<IAgriJournalItem.IPage> getPages() {
        return this.pages;
    }
}
