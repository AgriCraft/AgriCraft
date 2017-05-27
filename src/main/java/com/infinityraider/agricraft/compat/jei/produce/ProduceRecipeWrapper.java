/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compat.jei.produce;

import com.infinityraider.agricraft.api.misc.IAgriHarvestProduct;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        input = new ArrayList();
        output = recipe.getProducts().stream()
                .map(IAgriHarvestProduct::toLabeledStack)
                .collect(Collectors.toList());

        input.add(recipe.getSeed());

        input.add(recipe.getGrowthRequirement().getSoils().stream()
                .flatMap(s -> s.getVarients().stream())
                .findFirst()
                .map(s -> s.toStack())
                .orElse(new ItemStack(Blocks.FARMLAND))
        );
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
