package com.infinityraider.agricraft.render.items.journal;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class JournalDataDrawerBase<P extends IAgriJournalItem.IPage> implements IJournalDataDrawer<P> {
    protected void drawMutation(IPageRenderContext context, PoseStack transforms, int posX, int posY, List<IAgriPlant> plants) {
        context.draw(transforms, Textures.MUTATION, posX, posY, 86, 18);
        transforms.pushPose();
        transforms.translate(0, 0, -0.001F);
        plants.get(0).getGuiRenderer().drawGrowthStage( plants.get(0), plants.get(0).getFinalStage(), context,
                transforms, posX + 1, posY + 1, 16, 16);
        plants.get(1).getGuiRenderer().drawGrowthStage( plants.get(1), plants.get(1).getFinalStage(), context,
                transforms, posX + 35, posY + 1, 16, 16);
        plants.get(2).getGuiRenderer().drawGrowthStage( plants.get(2), plants.get(2).getFinalStage(), context,
                transforms, posX + 69, posY + 1, 16, 16);
        transforms.popPose();
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
