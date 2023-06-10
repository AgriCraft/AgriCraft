package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.content.items.IAgriSeedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class AgriPlantIngredient extends Ingredient {

	private final IAgriPlant plant;
	private ItemStack[] stacks;

	public AgriPlantIngredient(IAgriPlant plant) {
		super(Stream.of(new ItemValue(plant.toItemStack())));
		this.plant = plant;
	}

	public String getPlantId() {
		return this.getPlant().getId();
	}

	public IAgriPlant getPlant() {
		return this.plant;
	}

	public boolean isValid() {
		return this.getPlant().isPlant();
	}

	@Override
	@NotNull
	public ItemStack[] getItems() {
		if (this.stacks == null) {
			if (this.isValid()) {
				this.stacks = new ItemStack[]{this.getPlant().toItemStack()};
			} else {
				return new ItemStack[]{};
			}
		}
		return this.stacks;
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		return stack != null
				&& stack.getItem() instanceof IAgriSeedItem
				&& this.isValid()
				&& ((IAgriSeedItem) stack.getItem()).getPlant(stack).equals(this.getPlant());
	}

// FIXME: update
//	@Override
//	@NotNull
//	public IIngredientSerializer<AgriPlantIngredient> getSerializer() {
//		return AgriApi.getPlantIngredientSerializer();
//	}

}
