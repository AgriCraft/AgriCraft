package com.infinityraider.agricraft.impl.v1.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class FrontPage implements IAgriJournalItem.IPage {
    public static final IAgriJournalItem.IPage INSTANCE = new FrontPage();

    private static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "front_page");

    private FrontPage() {}

    @Nonnull
    @Override
    public ResourceLocation getDataDrawerId() {
        return ID;
    }

    @Nonnull
    @Override
    public Type getPageType() {
        return Type.FRONT;
    }

    @Override
    public void onPageOpened(PlayerEntity player, ItemStack stack, IAgriJournalItem journal) {}
}
