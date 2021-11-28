package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class IntroductionPages extends BasePage {
    public static final Page INTRODUCTION = new Introduction();
    public static final Page GENETICS = new Genetics();
    public static final Page GROWTH_REQS = new GrowthReqs();

    public static final Page[] PAGES = {INTRODUCTION, GENETICS, GROWTH_REQS};

    private static final class Introduction extends IntroductionPages {
        private final ITextComponent INTRODUCTION = new TranslationTextComponent("agricraft.journal.introduction");
        private final ITextComponent PARAGRAPH_1 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_1");
        private final ITextComponent PARAGRAPH_2 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_2");
        private final ITextComponent PARAGRAPH_3 = new TranslationTextComponent("agricraft.journal.introduction.paragraph_3");
        private final ITextComponent DISCOVERED = new TranslationTextComponent("agricraft.journal.introduction.discovered");

        private Introduction() {
        }

        @Override
        public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            // Draw Nothing
        }

        @Override
        public void drawRightSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
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
            dy += renderer.drawText(transforms, PARAGRAPH_3, dx, dy, 0.70F);
            dy += spacing;
            dy += spacing;
            // Final paragraph:
            ITextComponent discovered = new StringTextComponent("")
                    .appendSibling(DISCOVERED)
                    .appendString(": " + journal.getDiscoveredSeeds(stack).size() + " / " + AgriApi.getPlantRegistry().count());
            renderer.drawText(transforms, discovered, dx, dy, 0.70F);
        }
    }

    private static final class Genetics extends IntroductionPages {
        private final ITextComponent CROP_BREEDING = new TranslationTextComponent("agricraft.journal.crop_breeding");
        private final ITextComponent PARAGRAPH_L_1 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_1");
        private final ITextComponent PARAGRAPH_L_2 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_2");
        private final ITextComponent PARAGRAPH_L_3 = new TranslationTextComponent("agricraft.journal.crop_breeding.paragraph_3");

        private final ResourceLocation DNA_SCHEMATIC = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/dna_schematic.png");

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
            // Second paragraph
            dy += renderer.drawText(transforms, PARAGRAPH_L_2, dx, dy, 0.65F);
            dy += spacing;
            // Illustration
            renderer.drawTexture(transforms, DNA_SCHEMATIC, dx, dy, 96, 32);
            dy += (spacing + 32);
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
            dy += renderer.drawText(transforms, PARAGRAPH_R_1, dx, dy, 0.65F);
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

    private static final class GrowthReqs extends IntroductionPages {
        private final ITextComponent GROWTH_REQS = new TranslationTextComponent("agricraft.journal.growth_reqs");
        private final ITextComponent PARAGRAPH_L_1 = new TranslationTextComponent("agricraft.journal.growth_reqs.paragraph_1");
        private final ITextComponent BRIGHTNESS = new TranslationTextComponent("agricraft.journal.growth_reqs.brightness");
        private final ITextComponent PARAGRAPH_BRIGHTNESS = new TranslationTextComponent("agricraft.journal.growth_reqs.brightness.desc");
        private final ITextComponent HUMIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.humidity");
        private final ITextComponent PARAGRAPH_HUMIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.humidity.desc");
        private final ITextComponent ACIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.acidity");
        private final ITextComponent PARAGRAPH_ACIDITY = new TranslationTextComponent("agricraft.journal.growth_reqs.acidity.desc");
        private final ITextComponent NUTRIENTS = new TranslationTextComponent("agricraft.journal.growth_reqs.nutrients");
        private final ITextComponent PARAGRAPH_NUTRIENTS = new TranslationTextComponent("agricraft.journal.growth_reqs.nutrients.desc");
        private final ITextComponent SEASONS = new TranslationTextComponent("agricraft.journal.growth_reqs.seasons");
        private final ITextComponent PARAGRAPH_SEASONS = new TranslationTextComponent("agricraft.journal.growth_reqs.seasons.desc");

        @Override
        public void drawLeftSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            float dy = 10;
            float dx = 6;
            float spacing = 4;

            // Title
            dy += renderer.drawText(transforms, GROWTH_REQS, dx, dy);
            dy += spacing;
            // First paragraph
            dy += renderer.drawText(transforms, PARAGRAPH_L_1, dx, dy, 0.65F);
            dy += spacing;

            // Brightness
            dy += renderer.drawText(transforms, BRIGHTNESS, dx, dy, 0.65F);
            renderer.drawTexture(transforms, Textures.BRIGHTNESS_BAR, 6, dy, 66, 8);
            dy += (6 + spacing);
            dy += renderer.drawText(transforms, PARAGRAPH_BRIGHTNESS, dx, dy, 0.50F);
            dy += spacing;

            // Humidity
            dy += renderer.drawText(transforms, HUMIDITY, dx, dy, 0.65F);
            dy += renderer.drawText(transforms, PARAGRAPH_HUMIDITY, dx, dy, 0.50F);
            this.drawSoilProperties(renderer, transforms, dx, dy, spacing, IAgriSoil.Humidity.values(),
                    Textures.HUMIDITY_OFFSETS, Textures.HUMIDITY_FILLED);
        }

        @Override
        public void drawRightSheet(PageRenderer renderer, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
            float dy = 10;
            float dx = 6;
            float spacing = 4;

            // Acidity
            dy += renderer.drawText(transforms, ACIDITY, dx, dy, 0.65F);
            dy += renderer.drawText(transforms, PARAGRAPH_ACIDITY, dx, dy, 0.50F);
            dy = this.drawSoilProperties(renderer, transforms, dx, dy, spacing, IAgriSoil.Acidity.values(),
                    Textures.ACIDITY_OFFSETS, Textures.ACIDITY_FILLED);

            // Nutrients
            dy += renderer.drawText(transforms, NUTRIENTS, dx, dy, 0.65F);
            dy += renderer.drawText(transforms, PARAGRAPH_NUTRIENTS, dx, dy, 0.50F);
            dy = this.drawSoilProperties(renderer, transforms, dx, dy, spacing, IAgriSoil.Nutrients.values(),
                    Textures.NUTRIENTS_OFFSETS, Textures.NUTRIENTS_FILLED);

            // Seasons
            if(AgriApi.getSeasonLogic().isActive()) {
                dy += renderer.drawText(transforms, SEASONS, dx, dy, 0.65F);
                dy += renderer.drawText(transforms, PARAGRAPH_SEASONS, dx, dy, 0.50F);
                float scale = 0.5F;
                dy += spacing*scale;
                for(int i = 0; i < AgriSeason.values().length - 1; i++) {
                    int w = 10;
                    int h = 12;
                    float v1 = (0.0F + i*h)/48;
                    float v2 = (0.0F + (i + 1)*h)/48;
                    renderer.drawTexture(transforms, Textures.SEASONS_FILLED, dx, dy - spacing/2, scale*w, scale*h, 0, v1, 1, v2);
                    dy += renderer.drawText(transforms, AgriSeason.values()[i].getDisplayName(), dx + 6, dy, 0.50F);
                    dy += spacing/2;
                }
            }
        }

        protected float drawSoilProperties(PageRenderer renderer, MatrixStack transforms, float dx, float dy, float spacing,
                                           IAgriSoil.SoilProperty[] property, int[] offsets, ResourceLocation texture) {
            float scale = 0.5F;
            dy += spacing*scale;
            for(int i = 0; i < property.length - 1; i++) {
                int w = offsets[i + 1] - offsets[i];
                float u1 = (offsets[i] + 0.0F)/53.0F;
                float u2 = (offsets[i] + w + 0.0F)/53.0F;
                renderer.drawTexture(transforms, texture, dx, dy - spacing/2, scale*w, scale*12, u1, 0, u2, 1);
                dy += renderer.drawText(transforms, property[i].getDescription(), dx + 6, dy, 0.50F);
                dy += spacing/2;
            }
            return dy + spacing;
        }
    }
}
