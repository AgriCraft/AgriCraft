/*
 * JEI Integration Category.
 */
package com.infinityraider.agricraft.compat.jei.mutation;

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
public class MutationRecipeCategory implements IRecipeCategory {

    private final IDrawableStatic background;
    private final String localizedName;
    private final IDrawableStatic overlay;

    public MutationRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(128, 128);
        localizedName = AgriCore.getTranslator().translate("agricraft_nei.mutation.title");
        overlay = guiHelper.createDrawable(
                new ResourceLocation("agricraft", "textures/gui/jei/crop_mutation.png"),
                0, 0, 128, 128
        );
    }

    @Nonnull
    @Override
    public String getUid() {
        return AgriCraftJEIPlugin.CATEGORY_MUTATION;
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
        if (!(recipeWrapper instanceof MutationRecipeWrapper)) {
            return;
        }

        MutationRecipeWrapper wrapper = ((MutationRecipeWrapper) recipeWrapper);

        if (wrapper.getInputs().size() < 1) {
            return;
        }

        if (wrapper.getInputs().get(0) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(0, false, 24, 39);
            recipeLayout.getItemStacks().set(0, (ItemStack)wrapper.getInputs().get(0));
        }

        if (wrapper.getInputs().size() > 1 && wrapper.getInputs().get(1) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(1, false, 86, 39);
            recipeLayout.getItemStacks().set(1, (ItemStack)wrapper.getInputs().get(1));
        }

        if (wrapper.getInputs().size() > 2 && wrapper.getInputs().get(2) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(2, false, 55, 65);
            recipeLayout.getItemStacks().set(2, (ItemStack) wrapper.getInputs().get(2));
        }

        if (wrapper.getInputs().size() > 3 && wrapper.getInputs().get(3) instanceof ItemStack) {
            recipeLayout.getItemStacks().init(3, false, 55, 86);
            recipeLayout.getItemStacks().set(3, (ItemStack) wrapper.getInputs().get(3));
        }

        recipeLayout.getItemStacks().init(4, false, 55, 39);
        recipeLayout.getItemStacks().set(4, wrapper.getOutputs().get(0));
    }

}
