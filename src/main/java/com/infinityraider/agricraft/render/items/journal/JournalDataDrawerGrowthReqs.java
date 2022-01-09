package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.journal.GrowthReqsPage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerGrowthReqs extends JournalDataDrawerBase<GrowthReqsPage> {
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
    public ResourceLocation getId() {
        return GrowthReqsPage.INSTANCE.getDataDrawerId();
    }

    @Override
    public void drawLeftSheet(GrowthReqsPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        float dy = 10;
        float dx = 6;
        float spacing = 4;

        // Title
        dy += context.drawText(transforms, GROWTH_REQS, dx, dy);
        dy += spacing;
        // First paragraph
        dy += context.drawText(transforms, PARAGRAPH_L_1, dx, dy, 0.65F);
        dy += spacing;

        // Brightness
        dy += context.drawText(transforms, BRIGHTNESS, dx, dy, 0.65F);
        context.draw(transforms, Textures.BRIGHTNESS_BAR, 6, dy, 66, 8);
        dy += (6 + spacing);
        dy += context.drawText(transforms, PARAGRAPH_BRIGHTNESS, dx, dy, 0.50F);
        dy += spacing;

        // Humidity
        dy += context.drawText(transforms, HUMIDITY, dx, dy, 0.65F);
        dy += context.drawText(transforms, PARAGRAPH_HUMIDITY, dx, dy, 0.50F);
        this.drawSoilProperties(context, transforms, dx, dy, spacing, IAgriSoil.Humidity.values(),
                Textures.HUMIDITY_OFFSETS, Textures.HUMIDITY_FILLED);
    }

    @Override
    public void drawRightSheet(GrowthReqsPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        float dy = 10;
        float dx = 6;
        float spacing = 4;

        // Acidity
        dy += context.drawText(transforms, ACIDITY, dx, dy, 0.65F);
        dy += context.drawText(transforms, PARAGRAPH_ACIDITY, dx, dy, 0.50F);
        dy = this.drawSoilProperties(context, transforms, dx, dy, spacing, IAgriSoil.Acidity.values(),
                Textures.ACIDITY_OFFSETS, Textures.ACIDITY_FILLED);

        // Nutrients
        dy += context.drawText(transforms, NUTRIENTS, dx, dy, 0.65F);
        dy += context.drawText(transforms, PARAGRAPH_NUTRIENTS, dx, dy, 0.50F);
        dy = this.drawSoilProperties(context, transforms, dx, dy, spacing, IAgriSoil.Nutrients.values(),
                Textures.NUTRIENTS_OFFSETS, Textures.NUTRIENTS_FILLED);

        // Seasons
        if(AgriApi.getSeasonLogic().isActive()) {
            dy += context.drawText(transforms, SEASONS, dx, dy, 0.65F);
            dy += context.drawText(transforms, PARAGRAPH_SEASONS, dx, dy, 0.50F);
            float scale = 0.5F;
            dy += spacing*scale;
            for(int i = 0; i < AgriSeason.values().length - 1; i++) {
                int w = 10;
                int h = 12;
                float v1 = (0.0F + i*h)/48;
                float v2 = (0.0F + (i + 1)*h)/48;
                context.draw(transforms, Textures.SEASONS_FILLED, dx, dy - spacing/2, scale*w, scale*h, 0, v1, 1, v2);
                dy += context.drawText(transforms, AgriSeason.values()[i].getDisplayName(), dx + 6, dy, 0.50F);
                dy += spacing/2;
            }
        }
    }

    protected float drawSoilProperties(IPageRenderContext context, MatrixStack transforms, float dx, float dy, float spacing,
                                       IAgriSoil.SoilProperty[] property, int[] offsets, ResourceLocation texture) {
        float scale = 0.5F;
        dy += spacing*scale;
        for(int i = 0; i < property.length - 1; i++) {
            int w = offsets[i + 1] - offsets[i];
            float u1 = (offsets[i] + 0.0F)/53.0F;
            float u2 = (offsets[i] + w + 0.0F)/53.0F;
            context.draw(transforms, texture, dx, dy - spacing/2, scale*w, scale*12, u1, 0, u2, 1);
            dy += context.drawText(transforms, property[i].getDescription(), dx + 6, dy, 0.50F);
            dy += spacing/2;
        }
        return dy + spacing;
    }
}
