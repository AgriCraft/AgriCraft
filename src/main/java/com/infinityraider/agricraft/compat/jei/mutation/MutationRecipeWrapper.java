/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compat.jei.mutation;

import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 *
 *
 */
public class MutationRecipeWrapper extends BlankRecipeWrapper {

    private final String chance;
    private final List<List<ItemStack>> input;
    private final ItemStack output;

    public MutationRecipeWrapper(IAgriMutation recipe) {
        // Setup lists.
        chance = (int) (recipe.getChance() * 100) + "%";
        input = new ArrayList<>();
        output = recipe.getChild().getSeed();

        // Add Parents
        recipe.getParents().stream()
                .map(p -> p.getSeed())
                .map(Arrays::asList)
                .forEach(input::add);

        // Setup Soil List
        final List<ItemStack> soils = new ArrayList<>();

        // Add Soils to List
        recipe.getChild().getGrowthRequirement().getSoils().stream()
                .flatMap(s -> s.getVarients().stream())
                .map(s -> s.toStack())
                .forEach(soils::add);

        // Add Farmland if no Soils
        if (soils.isEmpty()) {
            soils.add(new ItemStack(Blocks.FARMLAND));
        }

        // Add Soil List to Master List
        input.add(soils);

        // Add Condition to List
        recipe.getChild().getGrowthRequirement().getConditionStack()
                .map(b -> b.toStack())
                .map(Arrays::asList)
                .ifPresent(input::add);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // Add Inputs
        ingredients.setInputLists(ItemStack.class, input);

        // Add Outputs
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(chance, 56, 14, Color.GRAY.getRGB(), false);
    }

}
