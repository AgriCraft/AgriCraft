/*
 * JEI Integration Category.
 */
package com.infinityraider.agricraft.compat.jei.produce;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;

import com.agricraft.agricore.core.AgriCore;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

/**
 *
 *
 */
public class ProduceRecipeCategory implements IRecipeCategory {

    private final IDrawableStatic background;
    private final String localizedName;
    private final IDrawableStatic overlay;

    public ProduceRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(128, 128);
        localizedName = AgriCore.getTranslator().translate("agricraft_nei.cropProduct.title");
        overlay = guiHelper.createDrawable(
                new ResourceLocation("agricraft", "textures/gui/jei/crop_produce.png"),
                0, 0, 128, 128
        );
    }

    @Nonnull
    @Override
    public String getUid() {
        return AgriCraftJEIPlugin.CATEGORY_PRODUCE;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        overlay.draw(minecraft);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
        if (!(recipeWrapper instanceof ProduceRecipeWrapper)) {
            return;
        }

        ProduceRecipeWrapper wrapper = ((ProduceRecipeWrapper) recipeWrapper);

        if (wrapper.getInputs().size() < 1) {
            return;
        }

        if (wrapper.getInputs().get(0) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(0, false, 15, 28);
            recipeLayout.getItemStacks().set(0, (ItemStack) wrapper.getInputs().get(0));
        }

        if (wrapper.getInputs().size() > 1 && wrapper.getInputs().get(1) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(1, false, 15, 54);
            recipeLayout.getItemStacks().set(1, (ItemStack) wrapper.getInputs().get(1));
        }

        if (wrapper.getInputs().size() > 2 && wrapper.getInputs().get(2) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(2, false, 15, 75);
            recipeLayout.getItemStacks().set(2, (ItemStack) wrapper.getInputs().get(2));
        }

        int index = 2;
        final int ax = 74;
        final int ay = 12;
        final int dim = 18;
        final int wid = 54;

        int dx = -dim;
        int dy = 0;

        for (Object e : recipeWrapper.getOutputs()) {
            index++;
            dx += dim;
            if (dx >= wid) {
                dx = 0;
                dy += dim;
            }
            if (e instanceof ItemStack) {
                recipeLayout.getItemStacks().init(index, false, ax + dx, ay + dy);
                recipeLayout.getItemStacks().set(index, (ItemStack) e);
            }
        }
    }

}
