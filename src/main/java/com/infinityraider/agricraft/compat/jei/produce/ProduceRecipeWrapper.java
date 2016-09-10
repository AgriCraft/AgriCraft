/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compat.jei.produce;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import com.infinityraider.agricraft.api.plant.IAgriPlant;

/**
 *
 * @author RlonRyan
 */
public class ProduceRecipeWrapper implements IRecipeWrapper {

    private final List input;
    private final List<ItemStack> output;

    public ProduceRecipeWrapper(IAgriPlant recipe) {
        ImmutableList.Builder builder = ImmutableList.builder();

        builder.add(recipe.getSeed());
        
        builder.add(recipe.getGrowthRequirement().getSoils().stream()
                .flatMap(s -> s.getVarients().stream())
                .findFirst()
                .map(s -> s.toStack())
                .orElse(new ItemStack(Blocks.FARMLAND))
        );
        recipe.getGrowthRequirement().getConditionStack()
                .map(b -> b.toStack())
                .ifPresent(builder::add);

        input = builder.build();
        output = recipe.getAllFruits();
    }

    @Override
    public List getInputs() {
        return input;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return output;
    }

    @Override
    public List<FluidStack> getFluidInputs() {
        return ImmutableList.of();
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return ImmutableList.of();
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    @Override
    public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return ImmutableList.of();
    }

    @Override
    public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }

}
