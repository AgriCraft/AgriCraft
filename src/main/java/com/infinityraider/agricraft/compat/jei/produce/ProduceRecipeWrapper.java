/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compat.jei.produce;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 *
 *
 */
public class ProduceRecipeWrapper extends BlankRecipeWrapper {

    private final List<ItemStack> input;
    private final List<ItemStack> output;

    public ProduceRecipeWrapper(IAgriPlant recipe) {
        // Allocate Lists
        input = new ArrayList<>();
        output = new ArrayList<>();

        // Fill Output List
        recipe.getPossibleProducts(output::add);

        // Add Seed to Input List
        input.add(recipe.getSeed());

        // Add Required Soil to Input List
        input.add(recipe.getGrowthRequirement().getSoils().stream()
                .flatMap(s -> s.getVarients().stream())
                .findFirst()
                .map(s -> s.toStack())
                .orElse(new ItemStack(Blocks.FARMLAND))
        );

        // Add Representative Growth Requirement Stack to Input List
        recipe.getGrowthRequirement().getConditionStack()
                .map(b -> b.toStack())
                .ifPresent(input::add);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // Set Inputs
        ingredients.setInputs(ItemStack.class, input);

        // Set Outputs
        ingredients.setOutputs(ItemStack.class, output);
    }

}
