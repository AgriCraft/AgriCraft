/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compat.jei.mutation;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;

/**
 *
 * @author RlonRyan
 */
public class MutationRecipeWrapper implements IRecipeWrapper {

    private final List input;
    private final ItemStack output;

    public MutationRecipeWrapper(IAgriMutation recipe) {
        ImmutableList.Builder builder = ImmutableList.builder();
        for (IAgriPlant p : recipe.getParents()) {
            builder.add(p.getSeed());
        }
        
        builder.add(recipe.getChild().getGrowthRequirement().getSoils().stream()
                .flatMap(s -> s.getVarients().stream())
                .findFirst()
                .orElse(new ItemStack(Blocks.FARMLAND))
        );
        recipe.getChild().getGrowthRequirement().getRequiredBlock()
                .map(b -> b.toStack())
                .ifPresent(builder::add);

        input = builder.build();
        output = recipe.getChild().getSeed();
    }

    @Override
    public List getInputs() {
        return input;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return ImmutableList.of(output);
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
