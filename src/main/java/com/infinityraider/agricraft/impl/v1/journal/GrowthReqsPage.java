package com.infinityraider.agricraft.impl.v1.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public final class GrowthReqsPage implements IAgriJournalItem.IPage {
    public static final IAgriJournalItem.IPage INSTANCE = new GrowthReqsPage();

    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "growth_reqs_page");

    private GrowthReqsPage() {}

    @Nonnull
    @Override
    public ResourceLocation getDataDrawerId() {
        return ID;
    }

    @Override
    public void onPageOpened(PlayerEntity player, ItemStack stack, IAgriJournalItem journal) {

    }
}
