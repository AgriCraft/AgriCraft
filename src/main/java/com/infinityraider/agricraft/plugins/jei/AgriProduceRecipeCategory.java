package com.infinityraider.agricraft.plugins.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AgriProduceRecipeCategory implements IRecipeCategory<IAgriPlant> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "gui/produce");

    public final IAgriDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getPlantRegistry().all(), AgriProduceRecipeCategory.ID);
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_wood), AgriProduceRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_iron), AgriProduceRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_obsidian), AgriProduceRecipeCategory.ID);
    }

    public AgriProduceRecipeCategory(IRecipeCategoryRegistration registration) {
        this.icon = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/item/debugger.png"), 0, 0, 16, 16, 16, 16);
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/crop_produce.png"), 0, 0, 128, 128, 128, 128);
    }
    
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
        return I18n.format("agricraft.gui.produce");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(IAgriPlant plant, IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, plant.getSeedSubstitutes().stream().map(ImmutableList::of).collect(Collectors.toList()));
        ingredients.setInputLists(AgriPlantIngredient.TYPE, ImmutableList.of(ImmutableList.of(plant)));
        List<List<ItemStack>> products = new ArrayList<>();
        plant.getAllPossibleProducts(product -> products.add(ImmutableList.of(product)));
        ingredients.setOutputLists(VanillaTypes.ITEM, products);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, IAgriPlant plant, IIngredients ingredients) {
        layout.setShapeless();

        // Setup Inputs
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(0, true, 15, 28);
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(1, true, 15, 54);
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(2, true, 15, 75);

        // Setup Outputs
        int index = 2;

        for (int y = 12; y < 62; y += 18) {
            for (int x = 74; x < 128; x += 18) {
                layout.getItemStacks().init(++index, false, x, y);
            }
        }

        // Register Recipe Elements
        layout.getItemStacks().set(ingredients);
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).set(ingredients);
    }

}
