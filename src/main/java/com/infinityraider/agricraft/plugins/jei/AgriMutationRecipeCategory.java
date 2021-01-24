package com.infinityraider.agricraft.plugins.jei;

import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
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

public class AgriMutationRecipeCategory implements IRecipeCategory<IAgriMutation> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "gui/mutation");

    public final IAgriDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AgriApi.getMutationRegistry().all(), ID);
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_wood), AgriMutationRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_iron), AgriMutationRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.getInstance().crop_sticks_obsidian), AgriMutationRecipeCategory.ID);
    }

    public AgriMutationRecipeCategory() {
        this.icon = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/item/crop_sticks_wood.png"), 0, 0, 16, 16, 16, 16);
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/crop_mutation.png"), 0, 0, 128, 128, 128, 128);
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<IAgriMutation> getRecipeClass() {
        return IAgriMutation.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("agricraft.gui.mutation");
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
    public void setIngredients(IAgriMutation mutation, IIngredients ingredients) {
        // Parents as plants
        ingredients.setInputLists(AgriPlantIngredient.TYPE,
                mutation.getParents().stream().map(ImmutableList::of).collect(Collectors.toList()));
        // Parents as seeds
        ingredients.setInputLists(VanillaTypes.ITEM,
                mutation.getParents().stream().map(IAgriPlant::toItemStack).map(ImmutableList::of).collect(Collectors.toList()));
        // Child as plant
        ingredients.setOutputLists(AgriPlantIngredient.TYPE, ImmutableList.of(ImmutableList.of(mutation.getChild())));
        // Child as seed
        ingredients.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(ImmutableList.of(mutation.getChild().toItemStack())));
        // TODO: soils and requirements
    }

    @Override
    public void setRecipe(IRecipeLayout layout, @Nonnull IAgriMutation mutation, @Nonnull IIngredients ingredients) {
        // Denote that this is a shapeless recipe.
        layout.setShapeless();

        // Setup Recipe Parents
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(0, true, 25, 40);
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(1, true, 87, 40);
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(0, true, 15, 5);
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(1, true, 95, 5);

        // Setup Recipe Child
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).init(2, false, 56, 40);
        layout.getIngredientsGroup(VanillaTypes.ITEM).init(2, false, 55, 1);

        // TODO: soils and requirements

        // Register Recipe Elements
        layout.getItemStacks().set(ingredients);
        layout.getIngredientsGroup(AgriPlantIngredient.TYPE).set(ingredients);
    }

}
