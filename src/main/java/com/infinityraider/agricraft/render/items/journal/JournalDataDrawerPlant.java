package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.impl.v1.journal.PlantPage;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class JournalDataDrawerPlant extends JournalDataDrawerBase<PlantPage> {
    private static final ITextComponent GROWTH_STAGES = new TranslationTextComponent("agricraft.tooltip.growth_stages");
    private static final ITextComponent GROWTH_REQUIREMENTS = new TranslationTextComponent("agricraft.tooltip.growth_requirements");
    private static final ITextComponent PRODUCTS = new TranslationTextComponent("agricraft.tooltip.products");
    private static final ITextComponent MUTATIONS = new TranslationTextComponent("agricraft.tooltip.mutations");

    @Override
    public ResourceLocation getId() {
        return PlantPage.ID;
    }

    @Override
    public void drawLeftSheet(PlantPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        // Title
        context.draw(transforms, JournalDataDrawerBase.Textures.TITLE, 0, 2, 128, 20);
        context.drawText(transforms, page.getPlant().getSeedName(), 30, 10);
        // Description
        float offset = 0.0F;
        if (!page.getPlant().getInformation().getString().isEmpty()) {
            offset = context.drawText(transforms, page.getPlant().getInformation(), 10, 30, 0.70F);
        }
        // Growth requirements
        this.drawGrowthRequirements(page, context, transforms,35 + offset);
        // Seed
        page.getPlant().getGuiRenderer().drawSeed(page.getPlant(), context, transforms, 4, 5, 16, 16);
        // Growth stages
        this.drawGrowthStages(page, context, transforms);
    }

    @Override
    public void drawRightSheet(PlantPage page, IPageRenderContext context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        // Products
        this.drawProducts(page, context, transforms);
        // Mutations
        this.drawMutations(page, context, transforms);
    }

    protected void drawGrowthRequirements(PlantPage page, IPageRenderContext context, MatrixStack transforms, float offset) {
        float dy = Math.max(offset, 60);
        dy += context.drawText(transforms, GROWTH_REQUIREMENTS, 10, dy, 0.80F) + 1;
        // Light level
        context.draw(transforms, JournalDataDrawerBase.Textures.BRIGHTNESS_BAR, 6, dy, 66, 8);
        transforms.push();
        transforms.translate(0, 0, -0.001F);
        for (int i = 0; i < page.brightnessMask().length; i++) {
            boolean current = page.brightnessMask()[i];
            if(current) {
                boolean prev = i > 0 && page.brightnessMask()[i - 1];
                boolean next = i < (page.brightnessMask().length - 1) && page.brightnessMask()[i + 1];
                context.draw(transforms, JournalDataDrawerBase.Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4*i + 1, dy, 4, 8,
                        0.25F, 0, 0.75F, 1);
                if(!prev) {
                    context.draw(transforms, JournalDataDrawerBase.Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4 * i, dy, 1, 8,
                            0, 0, 0.25F, 1);
                }
                if(!next) {
                    context.draw(transforms, JournalDataDrawerBase.Textures.BRIGHTNESS_HIGHLIGHT, 6 + 4*i + 5, dy, 1, 8,
                            0.75F, 0, 1, 1);
                }
            }
        }
        dy += 9;
        transforms.pop();
        // Seasons
        if(AgriApi.getSeasonLogic().isActive()) {
            for(int i = 0; i < page.seasonMask().length; i++) {
                int dx = 70;
                int w = 10;
                int h = 12;
                int x = (i%2)*(w + 2) + 5;
                int y = (i/2)*(h + 2) + 6;
                float v1 = (0.0F + i*h)/48;
                float v2 = (0.0F + (i + 1)*h)/48;
                if(page.seasonMask()[i]) {
                    context.draw(transforms, JournalDataDrawerBase.Textures.SEASONS_FILLED, x + dx, y + dy, w, h, 0, v1, 1, v2);
                } else {
                    context.draw(transforms, JournalDataDrawerBase.Textures.SEASONS_EMPTY, x + dx, y + dy, w, h, 0, v1, 1, v2);
                }
            }
        }
        // Humidity
        for(int i = 0; i < page.humidityMask().length; i++) {
            int dx = JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS[i];
            int w = JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS[i + 1] - JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS[i];
            float u1 = (dx + 0.0F)/53.0F;
            float u2 = (dx + w + 0.0F)/53.0F;
            if(page.humidityMask()[i]) {
                context.draw(transforms, JournalDataDrawerBase.Textures.HUMIDITY_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            } else {
                context.draw(transforms, JournalDataDrawerBase.Textures.HUMIDITY_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            }
        }
        dy += 13;
        // Acidity
        for(int i = 0; i < page.acidityMask().length; i++) {
            int dx = JournalDataDrawerBase.Textures.ACIDITY_OFFSETS[i];
            int w = JournalDataDrawerBase.Textures.ACIDITY_OFFSETS[i + 1] - JournalDataDrawerBase.Textures.ACIDITY_OFFSETS[i];
            float u1 = (dx + 0.0F)/53.0F;
            float u2 = (dx + w + 0.0F)/53.0F;
            if(page.acidityMask()[i]) {
                context.draw(transforms, JournalDataDrawerBase.Textures.ACIDITY_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            } else {
                context.draw(transforms, JournalDataDrawerBase.Textures.ACIDITY_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            }
        }
        dy += 13;
        // Nutrients
        for(int i = 0; i < page.nutrientsMask().length; i++) {
            int dx = JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS[i];
            int w = JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS[i + 1] - JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS[i];
            float u1 = (dx + 0.0F)/53.0F;
            float u2 = (dx + w + 0.0F)/53.0F;
            if(page.nutrientsMask()[i]) {
                context.draw(transforms, JournalDataDrawerBase.Textures.NUTRIENTS_FILLED, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            } else {
                context.draw(transforms, JournalDataDrawerBase.Textures.NUTRIENTS_EMPTY, 10 + dx, dy, w, 12, u1, 0, u2, 1);
            }
        }
    }

    protected void drawGrowthStages(PlantPage page, IPageRenderContext context, MatrixStack transforms) {
        // Position data
        int y0 = 170;
        int delta = 20;
        int rows = page.getStages().size()/6 + (page.getStages().size() % 6 > 0 ? 1 : 0);
        int columns = page.getStages().size()/rows + (page.getStages().size() % rows > 0 ? 1 : 0);
        // draw stages
        int row = 0;
        int dx = (context.getPageWidth() - (16*(columns)))/(columns + 1);
        for(int i = 0; i < page.getStages().size(); i++) {
            int column = i % columns;
            if(i > 0 && column == 0) {
                row += 1;
            }
            context.draw(transforms, JournalDataDrawerBase.Textures.GROWTH_STAGE, dx*(column + 1) + 16*column - 1, y0 - delta*(rows - row - 1) - 1, 18, 18);
            transforms.push();
            transforms.translate(0,0, -0.001F);
            page.getPlant().getGuiRenderer().drawGrowthStage(page.getPlant(), page.getStages().get(i), context, transforms, dx*(column + 1) + 16*column, y0 - delta*(rows - row - 1), 16, 16);
            transforms.pop();
        }
        // draw text
        context.drawText(transforms, GROWTH_STAGES, 10, y0 - delta*rows + 4, 0.90F);
    }

    protected void drawProducts(PlantPage page, IPageRenderContext context, MatrixStack transforms) {
        context.drawText(transforms, PRODUCTS, 10, 10, 0.80F);
        for(int i = 0; i < page.getDrops().size(); i++) {
            context.draw(transforms, JournalDataDrawerBase.Textures.MUTATION, 10 + i*20, 19, 18, 18, 0, 0, 18.0F/86.0F, 1);
            context.drawItem(transforms, page.getDrops().get(i), 11+i*20, 20);
        }
    }

    protected void drawMutations(PlantPage page, IPageRenderContext context, MatrixStack transforms) {
        context.drawText(transforms, MUTATIONS, 10, 45, 0.80F);
        int posX = 10;
        int posY = 54;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getOnPageMutations()) {
            this.drawMutation(context, transforms, posX, posY, plants);
            posY += dy;
        }
    }

    @Override
    public void drawTooltipLeft(PlantPage page, IPageRenderContext context, MatrixStack transforms, int mouseX, int mouseY) {
        // seed item
        if (4 <= mouseX && mouseX <= 20 && 5 <= mouseY && mouseY <= 21) {
            context.drawTooltip(transforms, Collections.singletonList(page.getPlant().getTooltip()), mouseX, mouseY);
            return;
        }

        // Growth requirements
        float offset = context.drawText(transforms, page.getPlant().getInformation(), -1000, -1000, 0.70F); // draw offscreen to get offset
        float dy = Math.max(offset, 60);
        dy += context.drawText(transforms, GROWTH_REQUIREMENTS, -1000, -1000, 0.80F) + 1; // draw offscreen to get offset
        // Light level
        for (int i = 0; i < page.brightnessMask().length; i++) {
            if (6 + 4 * i <= mouseX && mouseX <= 6 + 4 * i + 4 && dy + 1 <= mouseY && mouseY <= dy + 9) {
                context.drawTooltip(transforms, Collections.singletonList(new TranslationTextComponent("agricraft.tooltip.light").appendString(" " + i)), mouseX, mouseY);
                return;
            }
        }
        dy += 9;
        // Seasons
        if (AgriApi.getSeasonLogic().isActive()) {
            for (int i = 0; i < page.seasonMask().length; i++) {
                int w = 10;
                int h = 12;
                int x = (i % 2) * (w + 2) + 5 + 70;
                int y = (i / 2) * (h + 2) + 6 + (int) dy;
                if (x <= mouseX && mouseX <= x + w && y <= mouseY && mouseY <= y + h) {
                    context.drawTooltip(transforms, Collections.singletonList(AgriSeason.values()[i].getDisplayName()), mouseX, mouseY);
                    return;
                }
            }
        }
        // Humidity
        for (int i = 0; i < page.humidityMask().length; i++) {
            int dx = JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS[i] + 10;
            int w = JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS[i + 1] - JournalDataDrawerBase.Textures.HUMIDITY_OFFSETS[i];
            if (dx <= mouseX && mouseX <= dx + w && dy <= mouseY && mouseY <= dy + 12) {
                context.drawTooltip(transforms, Collections.singletonList(IAgriSoil.Humidity.values()[i].getDescription()), mouseX, mouseY);
                return;
            }
        }
        dy += 13;
        // Acidity
        for (int i = 0; i < page.acidityMask().length; i++) {
            int dx = JournalDataDrawerBase.Textures.ACIDITY_OFFSETS[i] + 10;
            int w = JournalDataDrawerBase.Textures.ACIDITY_OFFSETS[i + 1] - JournalDataDrawerBase.Textures.ACIDITY_OFFSETS[i];
            if (dx <= mouseX && mouseX <= dx + w && dy <= mouseY && mouseY <= dy + 12) {
                context.drawTooltip(transforms, Collections.singletonList(IAgriSoil.Acidity.values()[i].getDescription()), mouseX, mouseY);
                return;
            }
        }
        dy += 13;
        // Nutrients
        for (int i = 0; i < page.nutrientsMask().length; i++) {
            int dx = JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS[i] + 10;
            int w = JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS[i + 1] - JournalDataDrawerBase.Textures.NUTRIENTS_OFFSETS[i];
            if (dx <= mouseX && mouseX <= dx + w && dy <= mouseY && mouseY <= dy + 12) {
                context.drawTooltip(transforms, Collections.singletonList(IAgriSoil.Nutrients.values()[i].getDescription()), mouseX, mouseY);
                return;
            }
        }

    }

    @Override
    public void drawTooltipRight(PlantPage page, IPageRenderContext context, MatrixStack transforms, int x, int y) {
        // products tooltips
        for (int i = 0; i < page.getDrops().size(); i++) {
            if (11 + i * 20 <= x && x <= 11 + i * 20 + 16 && 20 <= y && y <= 20 + 16) {
                context.drawTooltip(transforms, page.getDrops().get(i).getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), x, y);
            }
        }
        // mutation tooltips
        int posX = 10;
        int posY = 54;
        int dy = 20;
        for (List<IAgriPlant> plants : page.getOnPageMutations()) {
            if (posX + 1 <= x && x <= posX + 17 && posY + 1 <= y && y <= posY + 17) {
                context.drawTooltip(transforms, Collections.singletonList(plants.get(0).getTooltip()), x, y);
            } else if (posX + 35 <= x && x <= posX + 51 && posY + 1 <= y && y <= posY + 17) {
                context.drawTooltip(transforms, Collections.singletonList(plants.get(1).getTooltip()), x, y);
            } else if (posX + 69 <= x && x <= posX + 85 && posY + 1 <= y && y <= posY + 17) {
                context.drawTooltip(transforms, Collections.singletonList(plants.get(2).getTooltip()), x, y);
            }
            posY += dy;
        }
    }

}
