package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraft.item.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class SeedSubstituteWrapper implements IAgriAdapter<IAgriPlant> {

    @Override
    public boolean accepts(Object obj) {
        return (obj instanceof ItemStack) && resolve((ItemStack) obj).isPresent();
    }

    @Override
    public Optional<IAgriPlant> valueOf(Object obj) {
        if (obj instanceof ItemStack) {
            return Objects.requireNonNull(resolve((ItemStack) obj));
        } else {
            return Optional.empty();
        }
    }

    private Optional<IAgriPlant> resolve(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return AgriApi.getPlantRegistry().stream()
                .filter(plant -> plant.getSeedSubstitutes().contains(stack))
                .findFirst();
    }
}
