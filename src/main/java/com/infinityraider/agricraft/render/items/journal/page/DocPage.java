package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class DocPage extends Page {
    public static final Page BREEDING_AND_STATS = new BreedingAndStats();
    public static final Page GROWTH_REQS = new GrowthReqs();

    private static class BreedingAndStats extends DocPage {

        private final ITextComponent CROP_BREEDING = new TranslationTextComponent("agricraft.journal.crop_breeding");
        private final ITextComponent PARAGRAPH_L_1 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_1");
        private final ITextComponent PARAGRAPH_L_2 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_2");
        private final ITextComponent PARAGRAPH_L_3 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_3");

        private final ITextComponent STATS = new TranslationTextComponent("agricraft.journal.stats");
        private final ITextComponent PARAGRAPH_R_1 = new TranslationTextComponent("agricraft.journal.stats.paragraph_1");
        private final ITextComponent PARAGRAPH_GROWTH = new TranslationTextComponent("agricraft.journal.stats.growth");
        private final ITextComponent PARAGRAPH_GAIN = new TranslationTextComponent("agricraft.journal.stats.gain");
        private final ITextComponent PARAGRAPH_STRENGTH = new TranslationTextComponent("agricraft.journal.stats.strength");
        private final ITextComponent PARAGRAPH_RESISTANCE = new TranslationTextComponent("agricraft.journal.stats.resistance");
        private final ITextComponent PARAGRAPH_FERTILITY = new TranslationTextComponent("agricraft.journal.stats.fertility");
        private final ITextComponent PARAGRAPH_MUTATIVITY = new TranslationTextComponent("agricraft.journal.stats.mutativity");

        @Override
        public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            float dy = 10;
            float dx = 6;
            float spacing = 4;
            // Title
            dy += renderer.drawText(transforms, CROP_BREEDING, dx, dy);
            dy += spacing;
            // First paragraph
            dy += renderer.drawText(transforms, PARAGRAPH_L_1, dx, dy, 0.65F);
            dy += spacing;
            // TODO: Add illustration
            // Second paragraph
            dy += renderer.drawText(transforms, PARAGRAPH_L_2, dx, dy, 0.65F);
            dy += spacing;
            // Third paragraph
            renderer.drawText(transforms, PARAGRAPH_L_3, dx, dy, 0.65F);
        }

        @Override
        public void drawRightSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            float dy = 10;
            float dx = 6;
            float spacing = 4;
            // Title
            dy += renderer.drawText(transforms, STATS, dx, dy);
            dy += spacing;
            // First paragraph
            dy += renderer.drawText(transforms, PARAGRAPH_R_1, dx, dy, 0.50F);
            dy += spacing;
            // Growth
            if (!AgriCraft.instance.getConfig().isGrowthStatHidden()) {
                dy += renderer.drawText(transforms, AgriStatRegistry.getInstance().growthStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                        dx, dy, 0.65F);
                dy += renderer.drawText(transforms, PARAGRAPH_GROWTH, dx, dy, 0.50F);
                dy += spacing;
            }
            // Gain
            if (!AgriCraft.instance.getConfig().isGainStatHidden()) {
                dy += renderer.drawText(transforms, AgriStatRegistry.getInstance().gainStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                        dx, dy, 0.65F);
                dy += renderer.drawText(transforms, PARAGRAPH_GAIN, dx, dy, 0.50F);
                dy += spacing;
            }
            // Strength
            if (!AgriCraft.instance.getConfig().isStrengthStatHidden()) {
                dy += renderer.drawText(transforms, AgriStatRegistry.getInstance().strengthStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                        dx, dy, 0.65F);
                dy += renderer.drawText(transforms, PARAGRAPH_STRENGTH, dx, dy, 0.50F);
                dy += spacing;
            }
            // Resistance
            if (!AgriCraft.instance.getConfig().isResistanceStatHidden()) {
                dy += renderer.drawText(transforms, AgriStatRegistry.getInstance().resistanceStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                        dx, dy, 0.65F);
                dy += renderer.drawText(transforms, PARAGRAPH_RESISTANCE, dx, dy, 0.50F);
                dy += spacing;
            }
            // Fertility
            if (!AgriCraft.instance.getConfig().isFertilityStatHidden()) {
                dy += renderer.drawText(transforms, AgriStatRegistry.getInstance().fertilityStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                        dx, dy, 0.65F);
                dy += renderer.drawText(transforms, PARAGRAPH_FERTILITY, dx, dy, 0.50F);
                dy += spacing;
            }
            // Mutativity
            if (!AgriCraft.instance.getConfig().isMutativityStatHidden()) {
                dy += renderer.drawText(transforms, AgriStatRegistry.getInstance().mutativityStat().getDescription().mergeStyle(TextFormatting.UNDERLINE),
                        dx, dy, 0.65F);
                renderer.drawText(transforms, PARAGRAPH_MUTATIVITY, dx, dy, 0.50F);
            }
        }
    }

    private static class GrowthReqs extends DocPage {

        @Override
        public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            //TODO
        }

        @Override
        public void drawRightSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            //TODO
        }
    }
}
