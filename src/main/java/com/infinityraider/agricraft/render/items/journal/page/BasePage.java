package com.infinityraider.agricraft.render.items.journal.page;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.render.items.journal.PageRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class BasePage extends Page {
    protected void drawMutation(PageRenderer renderer, MatrixStack transforms, int posX, int posY, List<IAgriPlant> plants) {
        renderer.drawTexture(transforms, Textures.MUTATION, posX, posY, 86, 18);
        transforms.push();
        transforms.translate(0, 0, -0.001F);
        plants.get(0).getGuiRenderer().drawGrowthStage( plants.get(0), plants.get(0).getFinalStage(), renderer,
                transforms, posX + 1, posY + 1, 16, 16);
        plants.get(1).getGuiRenderer().drawGrowthStage( plants.get(1), plants.get(1).getFinalStage(), renderer,
                transforms, posX + 35, posY + 1, 16, 16);
        plants.get(2).getGuiRenderer().drawGrowthStage( plants.get(2), plants.get(2).getFinalStage(), renderer,
                transforms, posX + 69, posY + 1, 16, 16);
        transforms.pop();
    }

    public static final class Textures {
        public static final ResourceLocation TITLE = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_title.png"
        );

        public static final ResourceLocation GROWTH_STAGE = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_growth_stage.png"
        );

        public static final ResourceLocation BRIGHTNESS_BAR = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_brightness_bar.png"
        );

        public static final ResourceLocation BRIGHTNESS_HIGHLIGHT = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_brightness_highlight.png"
        );

        public static final ResourceLocation HUMIDITY_EMPTY = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_humidity_empty.png"
        );

        public static final ResourceLocation HUMIDITY_FILLED = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_humidity_filled.png"
        );

        public static final ResourceLocation ACIDITY_EMPTY = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_acidity_empty.png"
        );

        public static final ResourceLocation ACIDITY_FILLED = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_acidity_filled.png"
        );

        public static final ResourceLocation NUTRIENTS_FILLED = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_nutrients_filled.png"
        );

        public static final ResourceLocation NUTRIENTS_EMPTY = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_nutrients_empty.png"
        );

        public static final ResourceLocation SEASONS_FILLED = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_seasons_filled.png"
        );

        public static final ResourceLocation SEASONS_EMPTY = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_seasons_empty.png"
        );

        public static final ResourceLocation MUTATION = new ResourceLocation(
                AgriCraft.instance.getModId().toLowerCase(),
                "textures/journal/template_mutation.png"
        );

        public static final int[] HUMIDITY_OFFSETS = new int[]{0, 8, 16, 26, 36, 46, 53};
        public static final int[] ACIDITY_OFFSETS = new int[]{0, 7, 15, 22, 30, 38, 46, 53};
        public static final int[] NUTRIENTS_OFFSETS = new int[]{0, 6, 14, 23, 32, 43, 53};

        private Textures() {}
    }
}
