package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class IntroductionPage extends Page {
    public static final Page INSTANCE = new IntroductionPage();

    private final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
    private final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
    private final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
    private final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");

    private IntroductionPage() {}

    @Override
    public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms) {
        // Draw Nothing
    }

    @Override
    public void drawRightSheet(PageRenderer renderer, MatrixStack transforms) {
        float dy = 10;
        float dx = 6;
        float spacing = 4;
        // Title
        dy += renderer.drawText(transforms, INTRODUCTION, dx, dy);
        dy += spacing;
        // First paragraph
        dy += renderer.drawText(transforms, PARAGRAPH_1, dx, dy, 0.70F);
        dy += spacing;
        // Second paragraph
        dy += renderer.drawText(transforms, PARAGRAPH_2, dx, dy, 0.70F);
        dy += spacing;
        // Third paragraph
        renderer.drawText(transforms, PARAGRAPH_3, dx, dy, 0.70F);
    }
}
