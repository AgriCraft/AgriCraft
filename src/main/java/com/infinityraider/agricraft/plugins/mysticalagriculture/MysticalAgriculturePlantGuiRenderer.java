package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class MysticalAgriculturePlantGuiRenderer implements IAgriGrowableGuiRenderer.WithSeed {
    private final Map<String, Integer> flowerColors;
    private final Map<String, Integer> seedColors;

    public MysticalAgriculturePlantGuiRenderer() {
        this.flowerColors = Maps.newHashMap();
        this.seedColors = Maps.newHashMap();
    }

    @Override
    public void drawGrowthStage(IAgriGrowable plant, IAgriGrowthStage stage, RenderContext context, MatrixStack transforms,
                                float x, float y, float w, float h) {
        IAgriGrowableGuiRenderer.WithSeed.super.drawGrowthStage(plant, stage, context, transforms, x, y, w, h);
        TextureAtlasSprite sprite = context.getSprite(plant.getTexturesFor(stage).get(1));
        int flowerColor = this.getFlowerColor(((IAgriPlant) plant).getId());
        if (flowerColor < 0) {
            context.draw(transforms, sprite, x, y, w, h, 255, 255, 255, 255);
        } else {
            context.draw(transforms, sprite, x, y, w, h, ((flowerColor >> 16) & 0xFF), ((flowerColor >> 8) & 0xFF), ((flowerColor) & 0xFF), 255);
        }
    }

    @Override
    public void drawSeed(IAgriGrowable.WithSeed plant, RenderContext context, MatrixStack transforms, float x, float y, float w, float h) {
        int seedColor = this.getSeedColor(((IAgriPlant) plant).getId());
        if (seedColor < 0) {
            IAgriGrowableGuiRenderer.WithSeed.super.drawSeed(plant, context, transforms, x, y, w, h);
        } else {
            TextureAtlasSprite sprite =  context.getSprite(plant.getSeedTexture());
            context.draw(transforms, sprite, x, y, w, h, ((seedColor >> 16) & 0xFF), ((seedColor >> 8) & 0xFF), ((seedColor) & 0xFF), 255);
        }

    }

    protected int getFlowerColor(String plantId) {
        return this.flowerColors.computeIfAbsent(plantId, MysticalAgricultureCompatClient::colorForFlower);
    }

    protected int getSeedColor(String plantId) {
        return this.seedColors.computeIfAbsent(plantId, MysticalAgricultureCompatClient::colorForSeed);
    }
}
