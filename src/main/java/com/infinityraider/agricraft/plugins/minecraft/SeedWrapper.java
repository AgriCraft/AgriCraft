package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

import java.util.Objects;
import java.util.Optional;

public class SeedWrapper implements IAgriAdapter<AgriSeed> {
    @Override
    public boolean accepts(Object obj) {
        if(obj instanceof IItemProvider) {
            return accepts(new ItemStack((IItemProvider) obj));
        }
        return (obj instanceof ItemStack)  && resolve((ItemStack) obj).isPresent();
    }

    @Override
    public Optional<AgriSeed> valueOf(Object obj) {
        if (obj instanceof IItemProvider) {
            return valueOf(new ItemStack((IItemProvider) obj));
        }
        if (obj instanceof ItemStack) {
            return Objects.requireNonNull(resolve((ItemStack) obj));
        } else {
            return Optional.empty();
        }
    }

    private Optional<AgriSeed> resolve(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        if(stack.getItem() instanceof ItemDynamicAgriSeed) {
            return ((ItemDynamicAgriSeed) stack.getItem()).getSeed(stack);
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
                })
                .map(AgriSeed::new);
    }

    private boolean isSeedItem(IAgriPlant plant, ItemStack seed) {
        return plant.getSeedItems().stream().anyMatch(stack ->
                ItemStack.areItemsEqual(seed, stack) && doTagsMatch(seed, stack)
        );
    }


    private boolean doTagsMatch(ItemStack seed, ItemStack test) {
        // TODO
        return true;
    }
}
