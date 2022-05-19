package com.infinityraider.agricraft.api.v1.client;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Client side class to draw contents on journal pages.
 * Use the static register method to register a drawer.
 *
 * Existing AgriCraft data drawers are:
 *  - "agricraft:front_page":           for the front page
 *  - "agricraft:genetics_page":        for the genetics documentation page
 *  - "agricraft:growth_reqs_page":     for the growth requirements documentation page
 *  - "agricraft:introduction_page":    for the introduction page
 *  - "agricraft:mutation_page":        for mutations pages
 *  - "agricraft:plant_page":           for plant pages
 *
 * @param <P> The type of the page this drawer is for
 */
@OnlyIn(Dist.CLIENT)
public interface IJournalDataDrawer<P extends IAgriJournalItem.IPage> {
    /**
     * Method to register a journal data drawer
     * @param drawer the drawer
     * @param <P> the type of page
     */
    static <P extends IAgriJournalItem.IPage> void register(IJournalDataDrawer<P> drawer) {
        AgriApi.registerJournalDataDrawer(drawer);
    }

    /**
     * @return a unique ID to represent this journal data drawer object
     */
    ResourceLocation getId();

    /**
     * Called to draw the content on the left sheet of the page
     * @param page the page
     * @param context the render context, use this to draw things
     * @param transforms the transformation matrix, to be passed to the render context
     * @param stack the ItemStack representing the journal which is being read
     * @param journal the IAgriJournalItem representing the journal which is being read
     */
    void drawLeftSheet(P page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal);

    /**
     * Called to draw the content on the right sheet of the page
     * @param page the page
     * @param context the render context, use this to draw things
     * @param transforms the transformation matrix, to be passed to the render context
     * @param stack the ItemStack representing the journal which is being read
     * @param journal the IAgriJournalItem representing the journal which is being read
     */
    void drawRightSheet(P page, IPageRenderContext context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal);

    /**
     * Called to draw the tooltips of the left sheet of the page according to the position of the mouse
     * @param page the page
     * @param context the render context, use this to draw things
     * @param transforms the transformation matrix, to be passed to the render context
     * @param x the x position of the mouse in the page
     * @param y the y position of the mouse in the page
     */
    default void drawTooltipLeft(P page, IPageRenderContext context, PoseStack transforms, int x, int y) { }


    /**
     * Called to draw the tooltips of the right sheet of the page according to the position of the mouse
     * @param page the page
     * @param context the render context, use this to draw things
     * @param transforms the transformation matrix, to be passed to the render context
     * @param x the x position of the mouse in the page
     * @param y the y position of the mouse in the page
     */
    default void drawTooltipRight(P page, IPageRenderContext context, PoseStack transforms, int x, int y) { }

    /**
     * Render context passed to the IJournalDataDrawer to render page contents.
     * Use this object to draw things in the journal.
     * Different contexts exists for different ways of reading the journal (from hand, in GUI, from the analyzer, etc)
     */
    interface IPageRenderContext extends IAgriGrowableGuiRenderer.RenderContext {
        /**
         * @return value indicating the current page width, use to scale what you draw
         */
        int getPageWidth();

        /**
         * @return value indicating the current page height, use to scale what you draw
         */
        int getPageHeight();

        /**
         * Draws a texture covering the entire page
         * @param transforms the transformation matrix
         * @param texture the texture to draw
         */
        default void drawFullPageTexture(PoseStack transforms, ResourceLocation texture) {
            this.draw(transforms, texture, 0, 0, this.getPageWidth(), this.getPageHeight());
        }

        /**
         * Draws a texture
         * @param transforms the transformation matrix
         * @param texture the texture to draw
         * @param x the x position of the texture
         * @param y the y position of the texture
         * @param w the width of the texture
         * @param h the height of the texture
         */
        default void draw(PoseStack transforms, ResourceLocation texture, float x, float y, float w, float h) {
            this.draw(transforms, texture, x, y, w, h, 0, 0, 1, 1);
        }

        /**
         * Draws a texture between UV's
         * @param transforms the transformation matrix
         * @param texture the texture to draw
         * @param x the x position of the texture
         * @param y the y position of the texture
         * @param w the width of the texture
         * @param h the height of the texture
         * @param u1 texture u to draw from
         * @param v1 texture v to draw from
         * @param u2 texture u to draw to
         * @param v2 texture v to draw to
         */
        void draw(PoseStack transforms, ResourceLocation texture, float x, float y, float w, float h, float u1, float v1, float u2, float v2) ;

        /**
         * Draws a TextureAtlasSprite
         * @param transforms the transformation matrix
         * @param texture the texture
         * @param x the x position of the sprite
         * @param y the y position of the sprite
         * @param w the width of the sprite
         * @param h the height of the sprite
         */
        default void draw(PoseStack transforms, TextureAtlasSprite texture, float x, float y, float w, float h) {
            this.draw(transforms, texture, x, y, w, h, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        /**
         * Generic draw method
         * @param transforms the transformation matrix
         * @param x the x position
         * @param y the y position
         * @param w the width
         * @param h the height
         * @param u1 u to draw from
         * @param v1 v to draw from
         * @param u2 u to draw to
         * @param v2 v to draw to
         * @param r the color red value
         * @param g the color green value
         * @param b the color blue value
         * @param a the color alpha value
         */
        void draw(PoseStack transforms, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a);

        /**
         * Draws text at the default scale
         * @param transforms the transformation matrix
         * @param text the text to draw
         * @param x the text x position
         * @param y the text y position
         * @return the height of the drawn text
         */
        default float drawText(PoseStack transforms, Component text, float x, float y) {
            return this.drawText(transforms, text, x, y, 1.0F);
        }

        /**
         * Draws text with a custom scale
         * @param transforms the transformation matrix
         * @param text the text to draw
         * @param x the text x position
         * @param y the text y position
         * @param scale the height of the drawn text
         * @return the height of the drawn text
         */
        float drawText(PoseStack transforms, Component text, float x, float y, float scale);

        /**
         * Draws an item
         * @param transforms the transformation matrix
         * @param item the item to draw
         * @param x the x position to draw the item at
         * @param y the y position to draw the item at
         */
        void drawItem(PoseStack transforms, ItemStack item, float x, float y);

        /**
         * Draws a tooltip
         * @param transforms the transformation matrix
         * @param textLines the text lines to draw
         * @param x the x position to draw the item at
         * @param y the y position to draw the item at
         */
        default void drawTooltip(PoseStack transforms, List<Component> textLines, float x, float y) {}

    }
}
