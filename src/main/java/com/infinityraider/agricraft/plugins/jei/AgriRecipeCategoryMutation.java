package com.infinityraider.agricraft.plugins.jei;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
import com.infinityraider.agricraft.content.AgriItemRegistry;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AgriRecipeCategoryMutation implements IRecipeCategory<IAgriMutation> {

    public static final ResourceLocation ID = new ResourceLocation(AgriCraft.instance.getModId(), "jei/mutation");
    public static final RecipeType<IAgriMutation> TYPE = new RecipeType<>(ID, IAgriMutation.class);

    private static final TranslatableComponent TITLE = new TranslatableComponent("agricraft.gui.mutation");

    public final IAgriDrawable icon;
    public final IAgriDrawable background;

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(TYPE, ImmutableList.copyOf(AgriApi.getMutationRegistry().all()));
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.CROP_STICKS_WOOD), TYPE);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.CROP_STICKS_IRON), TYPE);
        registration.addRecipeCatalyst(new ItemStack(AgriItemRegistry.CROP_STICKS_OBSIDIAN), TYPE);
    }

    public AgriRecipeCategoryMutation() {
        this.icon = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/item/crop_sticks_wood.png"), 0, 0, 16, 16, 16, 16);
        this.background = JeiPlugin.createAgriDrawable(new ResourceLocation(AgriCraft.instance.getModId(), "textures/gui/jei/crop_mutation.png"), 0, 0, 128, 128, 128, 128);
    }

    @Override
    public RecipeType<IAgriMutation> getRecipeType() {
        return TYPE;
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public Class<IAgriMutation> getRecipeClass() {
        return IAgriMutation.class;
    }

    @Nonnull
    @Override
    public TranslatableComponent getTitle() {
        return TITLE;
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

    private static final String INPUT_SEED_1 = "input_seed_1";
    private static final String INPUT_SEED_2 = "input_seed_2";
    private static final String INPUT_PLANT_1 = "input_plant_1";
    private static final String INPUT_PLANT_2 = "input_plant_1";
    private static final String OUTPUT_SEED = "output_seed";
    private static final String OUTPUT_PLANT = "output_plant";

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IAgriMutation mutation, IFocusGroup focuses) {
        // Clear the focus as this sometimes causes display bugs
        //layout.getIngredientsGroup(AgriIngredientPlant.TYPE).setOverrideDisplayFocus(null);
        //layout.getIngredientsGroup(VanillaTypes.ITEM).setOverrideDisplayFocus(null);

        // Set shapeless
        builder.setShapeless();

        // First input
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 5)
                .setSlotName(INPUT_SEED_1)
                .addIngredient(VanillaTypes.ITEM, mutation.getParents().get(0).toItemStack());
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 40)
                .setSlotName(INPUT_PLANT_1)
                .setCustomRenderer(AgriIngredientPlant.TYPE, AgriIngredientPlant.RENDERER)
                .addIngredient(AgriIngredientPlant.TYPE, mutation.getParents().get(0));

        // Second input
        builder.addSlot(RecipeIngredientRole.INPUT, 95, 5)
                .setSlotName(INPUT_SEED_2)
                .addIngredient(VanillaTypes.ITEM, mutation.getParents().get(1).toItemStack());
        builder.addSlot(RecipeIngredientRole.INPUT, 87, 40)
                .setSlotName(INPUT_PLANT_2)
                .setCustomRenderer(AgriIngredientPlant.TYPE, AgriIngredientPlant.RENDERER)
                .addIngredient(AgriIngredientPlant.TYPE, mutation.getParents().get(1));

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 1)
                .setSlotName(OUTPUT_SEED)
                .addIngredient(VanillaTypes.ITEM, mutation.getParents().get(1).toItemStack());
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 40)
                .setSlotName(OUTPUT_PLANT)
                .setCustomRenderer(AgriIngredientPlant.TYPE, AgriIngredientPlant.RENDERER)
                .addIngredient(AgriIngredientPlant.TYPE, mutation.getParents().get(1));

        // Register Recipe Elements
        //layout.getItemStacks().set(ingredients);
        //layout.getIngredientsGroup(AgriIngredientPlant.TYPE).set(ingredients);

    }
}
