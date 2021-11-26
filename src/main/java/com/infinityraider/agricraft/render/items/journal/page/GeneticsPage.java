package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GeneticsPage extends Page {
    private final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
    private final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
    private final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
    private final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");

    @Override
    public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms) {

    }

    @Override
    public void drawRightSheet(PageRenderer renderer, MatrixStack transforms) {

    }
}
