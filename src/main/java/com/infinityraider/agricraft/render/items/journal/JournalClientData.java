package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.network.MessageFlipJournalPage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class JournalClientData {
    private static final int FLIPPING_DURATION = 20;

    private final IAgriJournalItem journal;
    private final ItemStack journalStack;
    private final List<IAgriJournalItem.IPage> pages;
    private final Hand hand;

    private int target;

    private int animationCounter;
    private int prevAnimationCounter;

    public JournalClientData(ItemStack journalStack, Hand hand) {
        this.journal = (IAgriJournalItem) journalStack.getItem();
        this.journalStack = journalStack;
        this.pages = journal.getPages(this.getJournalStack());
        this.hand = hand;
        this.target = this.getJournal().getCurrentPageIndex(this.getJournalStack());
    }

    public IAgriJournalItem getJournal() {
        return this.journal;
    }

    public ItemStack getJournalStack() {
        return this.journalStack;
    }

    public Hand getHand() {
        return this.hand;
    }

    public int getPageIndex() {
        return this.getJournal().getCurrentPageIndex(this.getJournalStack());
    }

    public IAgriJournalItem.IPage getCurrentPage() {
        int page = this.getPageIndex();
        if(this.target >= page) {
            return this.pages.get(page);
        } else {
            return this.pages.get(page - 1);
        }
    }

    public IAgriJournalItem.IPage getFlippedPage() {
        int page = this.getPageIndex();
        if(this.target > page) {
            return this.pages.get(page + 1);
        } else  {
            return this.pages.get(page);
        }
    }

    public void incrementPage() {
        this.target = Math.min(this.pages.size() - 1, this.target + 1);
    }

    public void decrementPage() {
        this.target = Math.max(0, this.target - 1);
    }

    public void tick() {
        int page = this.getPageIndex();
        this.prevAnimationCounter = this.animationCounter;
        if(this.target > page) {
            if(this.animationCounter == 0) {
                this.animationCounter = FLIPPING_DURATION;
                this.prevAnimationCounter = this.animationCounter;
            }
            this.animationCounter -= 1;
            if(this.animationCounter <= 0) {
                this.animationCounter = 0;
                this.getJournal().setCurrentPageIndex(this.getJournalStack(), page + 1);
                new MessageFlipJournalPage(page + 1, this.getHand()).sendToServer();
            }
        } else if(this.target < page) {
            if(this.animationCounter == 0) {
                this.animationCounter = -FLIPPING_DURATION;
                this.prevAnimationCounter = this.animationCounter;
            }
            this.animationCounter += 1;
            if(this.animationCounter >= 0) {
                this.animationCounter = 0;
                this.getJournal().setCurrentPageIndex(this.getJournalStack(), page - 1);
                new MessageFlipJournalPage(page -1, this.getHand()).sendToServer();
            }
        }
    }

    public float getFlippingProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevAnimationCounter, this.animationCounter)/ FLIPPING_DURATION;
    }
}
