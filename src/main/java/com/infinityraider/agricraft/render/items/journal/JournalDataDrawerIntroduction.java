package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.impl.v1.journal.IntroductionPage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerIntroduction extends JournalDataDrawerBase<IntroductionPage> {
    private final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
    private final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
    private final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
    private final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");
    private final ITextComponent DISCOVERED = new TranslationTextComponent("agricraft.journal.introduction.discovered");

    @Override
    public ResourceLocation getId() {
        return IntroductionPage.INSTANCE.getDataDrawerId();
    }

    @Override
    public void drawLeftSheet(IntroductionPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        // Draw Nothing
    }

    @Override
    public void drawRightSheet(IntroductionPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        float dy = 10;
        float dx = 6;
        float spacing = 4;
        // Title
        dy += context.drawText(transforms, INTRODUCTION, dx, dy);
        dy += spacing;
        // First paragraph
        dy += context.drawText(transforms, PARAGRAPH_1, dx, dy, 0.70F);
        dy += spacing;
        // Second paragraph
        dy += context.drawText(transforms, PARAGRAPH_2, dx, dy, 0.70F);
        dy += spacing;
        // Third paragraph
        dy += context.drawText(transforms, PARAGRAPH_3, dx, dy, 0.70F);
        dy += spacing;
        dy += spacing;
        // Final paragraph:
        ITextComponent discovered = new StringTextComponent("")
                .appendSibling(DISCOVERED)
                .appendString(": " + journal.getDiscoveredSeeds(stack).size() + " / " + AgriApi.getPlantRegistry().count());
        context.drawText(transforms, discovered, dx, dy, 0.70F);
    }
}
