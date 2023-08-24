package com.agricraft.agricraft.compat.jei;

import com.agricraft.agricraft.common.item.crafting.MagnifyingHelmetRecipe;
import com.agricraft.agricraft.common.registry.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Add the magnifying helmet custom crafting recipe to the crafting recipes in JEI
 */
public class MagnifyingHelmetExtension implements ICraftingCategoryExtension {

	private static final List<ItemStack> HELMETS = List.of(Items.LEATHER_HELMET.getDefaultInstance(), Items.CHAINMAIL_HELMET.getDefaultInstance(), Items.IRON_HELMET.getDefaultInstance(), Items.GOLDEN_HELMET.getDefaultInstance(), Items.DIAMOND_HELMET.getDefaultInstance(), Items.NETHERITE_HELMET.getDefaultInstance(), Items.TURTLE_HELMET.getDefaultInstance());
	private final MagnifyingHelmetRecipe recipe;

	public MagnifyingHelmetExtension(MagnifyingHelmetRecipe recipe) {
		this.recipe = recipe;
	}


	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
		List<ItemStack> helmetsOutput = HELMETS.stream().map(ItemStack::copy).toList();
		helmetsOutput.forEach(h -> h.getOrCreateTag().putBoolean("magnifying", true));
		int width = getWidth();
		int height = getHeight();
		craftingGridHelper.createAndSetInputs(builder, List.of(HELMETS, List.of(ModItems.MAGNIFYING_GLASS.get().getDefaultInstance())), width, height);
		craftingGridHelper.createAndSetOutputs(builder, helmetsOutput);
	}

	@Override
	public @Nullable ResourceLocation getRegistryName() {
		return recipe.getId();
	}

	@Override
	public int getWidth() {
		return 2;
	}

	@Override
	public int getHeight() {
		return 1;
	}

}
