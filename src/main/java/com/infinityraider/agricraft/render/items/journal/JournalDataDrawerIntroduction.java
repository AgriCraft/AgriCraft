package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.impl.v1.journal.IntroductionPage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerIntroduction extends JournalDataDrawerBase<IntroductionPage> {
    private final Component INTRODUCTION = new TranslatableComponent("agricraft.journal.introduction");
    private final Component PARAGRAPH_1 = new TranslatableComponent("agricraft.journal.introduction.paragraph_1");
    private final Component PARAGRAPH_2 = new TranslatableComponent("agricraft.journal.introduction.paragraph_2");
    private final Component PARAGRAPH_3 = new TranslatableComponent("agricraft.journal.introduction.paragraph_3");
    private final Component DISCOVERED = new TranslatableComponent("agricraft.journal.introduction.discovered");

    @Override
    public ResourceLocation getId() {
        return IntroductionPage.INSTANCE.getDataDrawerId();
    }

    @Override
    public void drawLeftSheet(IntroductionPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        // Draw Nothing
    }

    @Override
    public void drawRightSheet(IntroductionPage page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
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
        Component discovered = new TextComponent("")
                .append(DISCOVERED)
                .append(": " + journal.getDiscoveredSeeds(stack).size() + " / " + AgriApi.getPlantRegistry().count());
        context.drawText(transforms, discovered, dx, dy, 0.70F);
    }
}
