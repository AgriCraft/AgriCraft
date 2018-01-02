/*
 * JEI Integration Category.
 */
package com.infinityraider.agricraft.compat.jei.mutation;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import javax.annotation.Nonnull;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 *
 *
 */
public class MutationRecipeCategory extends BlankRecipeCategory<MutationRecipeWrapper> {

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

    @Override
    public String getModName() {
        return AgriCraft.instance.getModId();
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        overlay.draw(minecraft);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MutationRecipeWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 24, 39);
        recipeLayout.getItemStacks().init(1, true, 86, 39);
        recipeLayout.getItemStacks().init(2, true, 55, 65);
        recipeLayout.getItemStacks().init(3, true, 55, 86);
        recipeLayout.getItemStacks().init(4, false, 55, 39);
        recipeLayout.getItemStacks().set(ingredients);
    }

}
