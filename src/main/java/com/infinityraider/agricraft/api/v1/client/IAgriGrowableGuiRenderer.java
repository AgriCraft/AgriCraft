package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Class to draw AgriCraft Growables (plants and weeds) in GUIs
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
    default void drawGrowthStage(IAgriGrowable plant, IAgriGrowthStage stage, RenderContext context, PoseStack transforms,
                                 float x, float y, float w, float h) {
        plant.getTexturesFor(stage).stream().findFirst().map(context::getSprite).ifPresent(sprite ->
                context.draw(transforms, sprite, x, y, w, h, 1.0F, 1.0F, 1.0F, 1.0F));
    }

    /**
     * Subtype of growables which also have a seed
     */
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
        default void drawSeed(IAgriGrowable.WithSeed plant, RenderContext context, PoseStack transforms,
                              float x, float y, float w, float h) {
            context.draw(transforms, context.getSprite(plant.getSeedTexture()), x, y, w, h, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Render context interface
     */
    interface RenderContext {
        /**
         * Draws a TextureAtlasSprite with modified color
         * @param transforms the transformation matrix
         * @param texture the texture
         * @param x the x position of the sprite
         * @param y the y position of the sprite
         * @param w the width of the sprite
         * @param h the height of the sprite
         * @param r the color red value
         * @param g the color green value
         * @param b the color blue value
         * @param a the color alpha value
         */
        void draw(PoseStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, float r, float g, float b, float a);

        /**
         * Draws a TextureAtlasSprite with modified color
         * @param transforms the transformation matrix
         * @param texture the texture
         * @param x the x position of the sprite
         * @param y the y position of the sprite
         * @param w the width of the sprite
         * @param h the height of the sprite
         * @param r the color red value
         * @param g the color green value
         * @param b the color blue value
         * @param a the color alpha value
         */
        default void draw(PoseStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h, int r, int g, int b, int a) {
            this.draw(transforms, texture, x, y, w, h, r/255.0F, g/255.0F, b/255.0F, a/255.0F);
        }

        /**
         * Gets a TextureAtlasSprite from a resource location pointing to a texture
         * @param texture the texture path
         * @return the sprite
         */
        TextureAtlasSprite getSprite(ResourceLocation texture);
    }
}
