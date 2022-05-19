package com.infinityraider.agricraft.impl.v1.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public final class IntroductionPage implements IAgriJournalItem.IPage {
    public static final IAgriJournalItem.IPage INSTANCE = new IntroductionPage();

    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "introduction_page");

    private IntroductionPage() {}

    @Nonnull
    @Override
    public ResourceLocation getDataDrawerId() {
        return ID;
    }

    @Nonnull
    @Override
    public Type getPageType() {
        return Type.INTRO;
    }

    @Override
    public void onPageOpened(Player player, ItemStack stack, IAgriJournalItem journal) {}
}
