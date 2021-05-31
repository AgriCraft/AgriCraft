package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public final class AnySoilIngredient extends Ingredient {
    private static final AnySoilIngredient INSTANCE = new AnySoilIngredient();

    public static AnySoilIngredient getInstance() {
        return INSTANCE;
    }

    private ItemStack[] matchingStacks;

    private AnySoilIngredient() {
        super(Stream.of(new SingleItemList(new ItemStack(Blocks.FARMLAND))));
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return AgriApi.getSoilRegistry().valueOf(stack).map(IAgriSoil::isSoil).orElse(false);
    }

    @Nonnull
    @Override
    public ItemStack[] getMatchingStacks() {
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

    @Nonnull
    @Override
    public IIngredientSerializer<AnySoilIngredient> getSerializer() {
        return AgriApi.getAnySoilIngredientSerializer();
    }
}
