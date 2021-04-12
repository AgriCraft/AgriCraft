package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AgriClippingRecipeCategory implements IRecipeCategory<IAgriPlant> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/clipping");

    public final IAgriDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getPlantRegistry().all(), AgriClippingRecipeCategory.ID);
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().clipper), AgriClippingRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_wood), AgriClippingRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_iron), AgriClippingRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_obsidian), AgriClippingRecipeCategory.ID);
    }

    public AgriClippingRecipeCategory() {
        this.icon = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/item/clipper.png"), 0, 0, 16, 16, 16, 16);
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/crop_produce.png"), 0, 0, 128, 128, 128, 128);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<IAgriPlant> getRecipeClass() {
        return IAgriPlant.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("agricraft.gui.clipping");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(IAgriPlant plant, IIngredients ingredients) {
        // Seed input
        ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(ImmutableList.of(plant.toItemStack())));
        // Plant input
        ingredients.setInputLists(AgriPlantIngredient.TYPE, ImmutableList.of(ImmutableList.of(plant)));
        // Outputs
        List<List<ItemStack>> products = new ArrayList<>();
        plant.getAllPossibleClipProducts(product -> products.add(ImmutableList.of(product)));
        ingredients.setOutputLists(VanillaTypes.ITEM, products);
        // TODO: soils and requirements
    }

    @Override
    public void setRecipe(IRecipeLayout layout, @Nonnull IAgriPlant plant, @Nonnull IIngredients ingredients) {
        // Clear the focus as this sometimes causes display bugs
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).setOverrideDisplayFocus(null);
        layout.getIngredientsGroup(VanillaTypes.ITEM).setOverrideDisplayFocus(null);

        // Denote that this is a shapeless recipe.
        layout.setShapeless();

        // Setup Inputs
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(0, true, 16, 49);
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 15, 8);

        // Setup Outputs
        int index = 2;
        for (int y = 32; y < 82; y += 18) {
            for (int x = 74; x < 128; x += 18) {
                layout.getItemStacks().init(++index, false, x, y);
            }
        }

        // TODO: soils and requirements

        // Register Recipe Elements
        layout.getItemStacks().set(ingredients);
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).set(ingredients);
    }

}
