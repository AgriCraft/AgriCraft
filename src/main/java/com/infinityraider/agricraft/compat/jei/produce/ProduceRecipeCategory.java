/*
 * JEI Integration Category.
 */
package com.infinityraider.agricraft.compat.jei.produce;

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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 *
 *
 */
public class ProduceRecipeCategory extends BlankRecipeCategory<ProduceRecipeWrapper> {

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
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        overlay.draw(minecraft);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ProduceRecipeWrapper recipeWrapper, IIngredients ingredients) {
        // Setup Inputs
        recipeLayout.getItemStacks().init(0, true, 15, 28);
        recipeLayout.getItemStacks().init(1, true, 15, 54);
        recipeLayout.getItemStacks().init(2, true, 15, 75);

        // Setup Outputs
        int index = 2;

        for (int y = 12; y < 62; y += 18) {
            for (int x = 74; x < 128; x += 18) {
                recipeLayout.getItemStacks().init(++index, false, x, y);
            }
        }

        // Set ItemStacks
        recipeLayout.getItemStacks().set(ingredients);
    }

}
