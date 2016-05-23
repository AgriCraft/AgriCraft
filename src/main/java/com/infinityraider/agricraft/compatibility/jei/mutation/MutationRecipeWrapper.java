/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compatibility.jei.mutation;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author RlonRyan
 */
public class MutationRecipeWrapper implements IRecipeWrapper {

	private final List input;
	private final ItemStack output;

	public MutationRecipeWrapper(IMutation recipe) {
		ImmutableList.Builder builder = ImmutableList.builder();
		for (Object o : recipe.getParents()) {
			if (o instanceof ItemStack) {
				builder.add(o);
			}
		}
		if (recipe.getResult() != null) {
			ICropPlant plant = CropPlantHandler.getPlantFromStack(recipe.getResult());
			if (plant.getGrowthRequirement() != null) {
				if (plant.getGrowthRequirement().getSoil() != null) {
					builder.add(plant.getGrowthRequirement().getSoil().toStack());
				} else {
					builder.add(new ItemStack(Blocks.dirt));
				}
				if (plant.getGrowthRequirement().getRequiredBlock() != null) {
					builder.add(plant.getGrowthRequirement().getRequiredBlock().toStack());
				}
			}
		}
		input = builder.build();
		output = recipe.getResult();
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
