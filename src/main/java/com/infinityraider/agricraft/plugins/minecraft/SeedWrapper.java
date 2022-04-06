package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;
import java.util.Optional;

public class SeedWrapper implements IAgriAdapter<IAgriGenome> {
    @Override
    public boolean accepts(Object obj) {
        if(obj instanceof ItemLike) {
            return accepts(new ItemStack((ItemLike) obj));
        }
        return (obj instanceof ItemStack)  && resolve((ItemStack) obj).isPresent();
    }

    @Override
    public Optional<IAgriGenome> valueOf(Object obj) {
        if (obj instanceof ItemLike) {
            return valueOf(new ItemStack((ItemLike) obj));
        }
        if (obj instanceof ItemStack) {
            return Objects.requireNonNull(resolve((ItemStack) obj));
        } else {
            return Optional.empty();
        }
    }

    private Optional<IAgriGenome> resolve(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        if(stack.getItem() instanceof ItemDynamicAgriSeed) {
            return ((ItemDynamicAgriSeed) stack.getItem()).getGenome(stack);
        }
        return AgriApi.getPlantRegistry().stream()
                .filter(plant -> this.isSeedItem(plant, stack))
                .findFirst()
                .map(plant -> {
                    IAgriGenome genome = AgriApi.getAgriGenomeBuilder(plant).build();
                    if(stack.hasTag()) {
                        assert stack.getTag() != null;
                        genome.readFromNBT(stack.getTag());
                    }
                    return genome;
                });
    }

    private boolean isSeedItem(IAgriPlant plant, ItemStack seed) {
        return plant.getSeedItems().stream().anyMatch(stack ->
                ItemStack.matches(seed, stack) && doTagsMatch(seed, stack)
        );
    }


    private boolean doTagsMatch(ItemStack seed, ItemStack test) {
        // TODO
        return true;
    }
}
