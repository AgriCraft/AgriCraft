package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import net.minecraft.item.ItemStack;
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

    private int page;
    private int target;

    private int animationCounter;
    private int prevAnimationCounter;

    public JournalClientData(ItemStack journalStack) {
        this.journal = (IAgriJournalItem) journalStack.getItem();
        this.journalStack = journalStack;
        this.pages = journal.getPages(this.getJournalStack());
    }

    public IAgriJournalItem getJournal() {
        return this.journal;
    }

    public ItemStack getJournalStack() {
        return this.journalStack;
    }

    public IAgriJournalItem.IPage getCurrentPage() {
        if(this.target >= this.page) {
            return this.pages.get(this.page);
        } else {
            return this.pages.get(this.page - 1);
        }
    }

    public IAgriJournalItem.IPage getFlippedPage() {
        if(this.target > this.page) {
            return this.pages.get(this.page + 1);
        } else  {
            return this.pages.get(this.page);
        }
    }

    public void incrementPage() {
        this.target = Math.min(this.pages.size() - 1, this.target + 1);
    }

    public void decrementPage() {
        this.target = Math.max(0, this.target - 1);
    }

    public void tick() {
        this.prevAnimationCounter = this.animationCounter;
        if(this.target > this.page) {
            if(this.animationCounter == 0) {
                this.animationCounter = FLIPPING_DURATION;
                this.prevAnimationCounter = this.animationCounter;
            }
            this.animationCounter -= 1;
            if(this.animationCounter <= 0) {
                this.animationCounter = 0;
                this.page += 1;
                this.pages.get(this.page).onPageOpened(AgriCraft.instance.getClientPlayer(), this.getJournalStack(), this.getJournal());
            }
        } else if(this.target < this.page) {
            if(this.animationCounter == 0) {
                this.animationCounter = -FLIPPING_DURATION;
                this.prevAnimationCounter = this.animationCounter;
            }
            this.animationCounter += 1;
            if(this.animationCounter >= 0) {
                this.animationCounter = 0;
                this.page -= 1;
                this.pages.get(this.page).onPageOpened(AgriCraft.instance.getClientPlayer(), this.getJournalStack(), this.getJournal());
            }
        }
    }

    public float getFlippingProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevAnimationCounter, this.animationCounter)/ FLIPPING_DURATION;
    }
}
