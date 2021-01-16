package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AgriMutationRecipe {
    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "mutation");

    public static final IRecipeCategory<IAgriMutation> CATEGORY = new IRecipeCategory<IAgriMutation>() {
        @Override
        public ResourceLocation getUid() {
            return ID;
        }

        @Override
        public Class<IAgriMutation> getRecipeClass() {
            return IAgriMutation.class;
        }

        @Override
        public String getTitle() {
            return I18n.format(ID.toString());
        }

        @Override
        public IDrawable getBackground() {
            return BACKGROUND;
        }

        @Override
        public IDrawable getIcon() {
            return ICON;
        }

        @Override
        public void setIngredients(IAgriMutation mutation, IIngredients ingredients) {
            ingredients.setInputLists(AgriPlantIngredient.TYPE, mutation.getParents().stream().map(ImmutableList::of).collect(Collectors.toList()));
            ingredients.setOutputLists(AgriPlantIngredient.TYPE, ImmutableList.of(ImmutableList.of(mutation.getChild())));
        }

        @Override
        public void setRecipe(IRecipeLayout layout, IAgriMutation mutation, IIngredients ingredients) {
            layout.setShapeless();

        }
    };

    // TODO
    private static final IDrawable BACKGROUND = new IDrawable() {
        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public void draw(MatrixStack matrixStack, int x, int y) {

        }
    };

    // TODO
    private static final IDrawable ICON = new IDrawable() {
        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void draw(MatrixStack matrixStack, int i, int i1) {

        }
    };

    public static void registerCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(CATEGORY);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getMutationRegistry().all(), ID);
    }
}
