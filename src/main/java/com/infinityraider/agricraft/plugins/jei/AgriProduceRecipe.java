package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public class AgriProduceRecipe {
    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "produce");

    public static final IRecipeCategory<IAgriPlant> CATEGORY = new IRecipeCategory<IAgriPlant>() {
        @Override
        public ResourceLocation getUid() {
            return ID;
        }

        @Override
        public Class<IAgriPlant> getRecipeClass() {
            return IAgriPlant.class;
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
        public void setIngredients(IAgriPlant plant, IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, plant.getSeedSubstitutes().stream().map(ImmutableList::of).collect(Collectors.toList()));
            ingredients.setInputLists(AgriPlantIngredient.TYPE, ImmutableList.of(ImmutableList.of(plant)));
            List<List<ItemStack>> products = Lists.newArrayList();
            plant.getAllPossibleProducts(product -> products.add(ImmutableList.of(product)));
            ingredients.setOutputLists(VanillaTypes.ITEM, products);
        }

        @Override
        public void setRecipe(IRecipeLayout layout, IAgriPlant plant, IIngredients ingredients) {
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
        registration.addRecipes(AgriApi.getPlantRegistry().all(), ID);
    }
}
