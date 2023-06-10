package com.agricraft.agricraft.api.requirement;

import com.agricraft.agricraft.api.AgriApi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public final class AnySoilIngredient extends Ingredient {

	private static final AnySoilIngredient INSTANCE = new AnySoilIngredient();

	public static AnySoilIngredient getInstance() {
		return INSTANCE;
	}

	private ItemStack[] matchingStacks;

	private AnySoilIngredient() {
		super(Stream.of(new ItemValue(new ItemStack(Blocks.FARMLAND))));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return AgriApi.getSoilRegistry().valueOf(stack).map(IAgriSoil::isSoil).orElse(false);
	}

	@NotNull
	@Override
	public ItemStack[] getItems() {
		this.determineMatchingStacks();
		return this.matchingStacks;
	}

	private void determineMatchingStacks() {
		if (this.matchingStacks == null) {
			this.matchingStacks = AgriApi.getSoilRegistry().stream()
					.flatMap(soil -> soil.getVariants().stream())
					.map(BlockState::getBlock)
					.map(ItemStack::new)
					.distinct().toArray(ItemStack[]::new);
		}
	}

// FIXME: update
//	@NotNull
//	@Override
//	public IIngredientSerializer<AnySoilIngredient> getSerializer() {
//		return AgriApi.getAnySoilIngredientSerializer();
//	}

}
