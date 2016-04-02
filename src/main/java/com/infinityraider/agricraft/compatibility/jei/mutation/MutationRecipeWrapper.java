/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compatibility.jei.mutation;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author RlonRyan
 */
public class MutationRecipeWrapper implements IRecipeWrapper {

	private final List input;
	private final ItemStack output;

	public MutationRecipeWrapper(Mutation recipe) {
		ImmutableList.Builder builder = ImmutableList.builder();
		for(Object o : recipe.getParents()) {
			if(o instanceof ItemStack) {
				builder.add(o);
			}
			if(o instanceof String) {
				builder.add(OreDictionary.getOres(((String) o)));
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
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {
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
