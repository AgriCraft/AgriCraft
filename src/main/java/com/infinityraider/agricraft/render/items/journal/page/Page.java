package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

public abstract class Page {
    public abstract void drawLeftSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal);

    public abstract void drawRightSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal);

    public void onPageOpened() {}
}
