package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.IAgriSeedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    public ItemStack[] getItems() {
        if(this.stacks == null) {
            if(this.isValid()) {
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

    @Nonnull
    @Override
    public IIngredientSerializer<AgriPlantIngredient> getSerializer() {
        return AgriApi.getPlantIngredientSerializer();
    }
}
