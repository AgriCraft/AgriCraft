package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class Page {
    public abstract void drawLeftSheet(PageRenderer renderer, MatrixStack transforms);

    public abstract void drawRightSheet(PageRenderer renderer, MatrixStack transforms);

    public void onPageOpened() {}
}
