/*
 */
package com.infinityraider.agricraft.compat.vanilla;

import java.util.Optional;

import net.minecraft.item.ItemStack;

import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.farming.PlantStats;

/**
 *
 *
 */
public class SeedWrapper implements IAgriAdapter<AgriSeed> {

    @Override
    public boolean accepts(Object obj) {
        return (obj instanceof ItemStack) && resolve((ItemStack) obj) != null;
    }

    @Override
    public Optional<AgriSeed> valueOf(Object obj) {
        if (obj instanceof ItemStack) {
            return Optional.ofNullable(resolve((ItemStack) obj));
        } else {
            return Optional.empty();
        }
    }

    private AgriSeed resolve(ItemStack stack) {
        Optional<IAgriPlant> plant = PlantRegistry.getInstance().getPlants().stream()
                .filter(p -> p.getSeedItems().contains(stack.getItem()))
                .findFirst();
        if (plant.isPresent()) {
            Optional<IAgriStat> stats = StatRegistry.getInstance().valueOf(stack.getTagCompound());
            return new AgriSeed(plant.get(), stats.orElseGet(PlantStats::new));
        } else {
            return null;
        }
    }

}
