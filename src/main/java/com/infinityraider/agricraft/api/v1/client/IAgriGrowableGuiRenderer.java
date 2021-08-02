package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Class to draw
 */
@OnlyIn(Dist.CLIENT)
public interface IAgriGrowableGuiRenderer {
    IAgriGrowableGuiRenderer DEFAULT = new IAgriGrowableGuiRenderer() {};

    /**
     * Draws a growth stage in a gui at the given position
     *
     * @param plant the plant
     * @param stage the growth stage
     * @param context the rendering context, use to make drawing calls
     * @param transforms the matrix stack
     * @param x position to render at (x-coordinate)
     * @param y position to render at (y-coordinate)
     * @param w the width to render
     * @param h the height to render
     */
    default void drawGrowthStage(IAgriGrowable plant, IAgriGrowthStage stage, RenderContext context, MatrixStack transforms,
                                 float x, float y, float w, float h) {
        plant.getTexturesFor(stage).stream().findFirst().map(context::getSprite).ifPresent(sprite ->
                context.draw(transforms, sprite, x, y, w, h, 1.0F, 1.0F, 1.0F, 1.0F));
    }

    interface WithSeed extends IAgriGrowableGuiRenderer {
        WithSeed DEFAULT = new WithSeed() {};

        /**
         * Renders the seed in a GUI
         *
         * @param plant the plant for which to draw the seed
         * @param context the render context, use to make draw calls
         * @param transforms the matrix stack
         * @param x x-coordinate
         * @param y y-coordinate
         * @param w the width
         * @param h the height
         */
        default void drawSeed(IAgriGrowable.WithSeed plant, RenderContext context, MatrixStack transforms,
                              float x, float y, float w, float h) {
            context.draw(transforms, context.getSprite(plant.getSeedTexture()), x, y, w, h, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    interface RenderContext {
        void draw(MatrixStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a);

        default void draw(MatrixStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, int r, int g, int b, int a) {
            this.draw(transforms, texture, x, y, w, h, r/255.0F, g/255.0F, b/255.0F, a/255.0F);
        }

        TextureAtlasSprite getSprite(ResourceLocation texture);
    }
}
