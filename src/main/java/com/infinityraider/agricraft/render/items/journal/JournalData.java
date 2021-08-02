package com.infinityraider.agricraft.render.items.journal;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.render.items.journal.page.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JournalData {
    private static final int FLIPPING_DURATION = 20;

    private final ItemStack journal;
    private final List<Page> pages;

    private int page;
    private int target;

    private int animationCounter;
    private int prevAnimationCounter;

    public JournalData(ItemStack journalStack) {
        this.journal = journalStack;
        this.pages = initializePages(journalStack);
    }

    public ItemStack getJournal() {
        return this.journal;
    }

    public Page getCurrentPage() {
        if(this.target >= this.page) {
            return this.pages.get(this.page);
        } else {
            return this.pages.get(this.page - 1);
        }
    }

    public Page getFlippedPage() {
        if(this.target > this.page) {
            return this.pages.get(this.page + 1);
        }else  {
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
                this.pages.get(this.page).onPageOpened();
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
                this.pages.get(this.page).onPageOpened();
            }
        }
    }

    public float getFlippingProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevAnimationCounter, this.animationCounter)/ FLIPPING_DURATION;
    }

    public static List<Page> initializePages(ItemStack journal) {
        ImmutableList.Builder<Page> builder = new ImmutableList.Builder<>();
        builder.add(FrontPage.INSTANCE);
        builder.add(IntroductionPage.INSTANCE);
        if (journal.getItem() instanceof IAgriJournalItem) {
            IAgriJournalItem journalItem = (IAgriJournalItem) journal.getItem();
            builder.addAll(getPlantPages(
                    journalItem.getDiscoveredSeeds(journal).stream()
                            .sorted(Comparator.comparing(plant -> plant.getPlantName().getString()))
                            .collect(Collectors.toList())));
        }
        return builder.build();
    }

    public static List<Page> getPlantPages(List<IAgriPlant> plants) {
        ImmutableList.Builder<Page> pages = ImmutableList.builder();
        plants.forEach(plant -> {
            PlantPage page = new PlantPage(plant, plants);
            pages.add(page);
            List<List<IAgriPlant>> mutations = page.getOffPageMutations();
            int size = mutations.size();
            if (size > 0) {
                int remaining = size;
                int from = 0;
                int to = Math.min(remaining, MutationPage.LIMIT);
                while (remaining > 0) {
                    pages.add(new MutationPage(mutations.subList(from, to)));
                    remaining -= (to - from);
                    from = to;
                    to = from + Math.min(remaining, MutationPage.LIMIT);
                }
            }
        });
        return pages.build();
    }
}
