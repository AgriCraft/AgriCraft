package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.infinityraider.agricraft.api.v1.client.IAgriGrowableGuiRenderer;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.util.mimic.MimickedPlant;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class MysticalAgriculturePlantOverride extends MimickedPlant implements IAgriGrowableGuiRenderer.WithSeed {
    private int flowerColor = Integer.MIN_VALUE;
    private int seedColor = Integer.MIN_VALUE;

    protected MysticalAgriculturePlantOverride(IAgriPlant original) {
        super(original);
    }

    public int getFlowerColor() {
        if(this.flowerColor ==  Integer.MIN_VALUE) {
            this.flowerColor = MysticalAgricultureCompatClient.colorForFlower(this.getId());
        }
        return this.flowerColor;
    }

    public int getSeedColor() {
        if(this.seedColor ==  Integer.MIN_VALUE) {
            this.seedColor = MysticalAgricultureCompatClient.colorForSeed(this.getId());
        }
        return this.seedColor;
    }

    @Nonnull
    @Override
    public IAgriGrowableGuiRenderer.WithSeed getGuiRenderer() {
        return this;
    }

    @Override
    public void drawGrowthStage(IAgriGrowable plant, IAgriGrowthStage stage, RenderContext context, MatrixStack transforms,
                                float x, float y, float w, float h) {
        IAgriGrowableGuiRenderer.WithSeed.super.drawGrowthStage(plant, stage, context, transforms, x, y, w, h);
        if(stage.isFinal()) {
            TextureAtlasSprite sprite = context.getSprite(plant.getTexturesFor(stage).get(1));
            if (this.getFlowerColor() < 0) {
                context.draw(transforms, sprite, x, y, w, h, 255, 255, 255, 255);
            } else {
                context.draw(transforms, sprite, x, y, w, h, ((this.getFlowerColor() >> 16) & 0xFF), ((this.getFlowerColor() >> 8) & 0xFF), ((this.getFlowerColor()) & 0xFF), 255);
            }
        }
    }

    @Override
    public void drawSeed(IAgriGrowable.WithSeed plant, RenderContext context, MatrixStack transforms, float x, float y, float w, float h) {
        if (this.getSeedColor() < 0) {
            IAgriGrowableGuiRenderer.WithSeed.super.drawSeed(plant, context, transforms, x, y, w, h);
        } else {
            TextureAtlasSprite sprite =  context.getSprite(plant.getSeedTexture());
            context.draw(transforms, sprite, x, y, w, h, ((this.getSeedColor() >> 16) & 0xFF), ((this.getSeedColor() >> 8) & 0xFF), ((this.getSeedColor()) & 0xFF), 255);
        }
    }
}
