/*
 * AgriCraft Crop Mutation JEI Recipe Wrapper
 */
package com.infinityraider.agricraft.compat.jei.mutation;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.IMutation;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;

/**
 *
 * @author RlonRyan
 */
public class MutationRecipeWrapper implements IRecipeWrapper {

	private final List input;
	private final ItemStack output;

	public MutationRecipeWrapper(IMutation recipe) {
		ImmutableList.Builder builder = ImmutableList.builder();
		for (IAgriCraftPlant p : recipe.getParents()) {
			builder.add(p.getSeed());
		}
		IAgriCraftPlant plant = recipe.getChild();
		IGrowthRequirement rec = recipe.getRequirement() == null ? plant.getGrowthRequirement() : recipe.getRequirement();
		if (rec != null) {
			if (rec.getSoil() != null) {
				builder.add(rec.getSoil().toStack());
			} else {
				builder.add(new ItemStack(Blocks.dirt));
			}
			if (rec.getRequiredBlock() != null) {
				builder.add(rec.getRequiredBlock().toStack());
			}
		}
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
