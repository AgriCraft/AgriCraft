/*
 */
package com.infinityraider.agricraft.vanilla;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.farming.PlantStats;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author RlonRyan
 */
public class SeedWrapper implements IAgriAdapter<AgriSeed> {

    @Override
    public boolean accepts(Object obj) {
        return (obj instanceof ItemStack) && resolve((ItemStack) obj) != null;
    }

    @Override
    public AgriSeed getValue(Object obj) {
        return (obj instanceof ItemStack) ? resolve((ItemStack) obj) : null;
    }

    private AgriSeed resolve(ItemStack stack) {
        IAgriPlant plant = resolveSeedItem(stack);
        plant = plant == null ? resolveOreDict(stack) : plant;
        Optional<IAgriStat> stats = Optional.empty();
        if (stack.hasTagCompound()) {
            stats = Optional.ofNullable(StatRegistry.getInstance().getValue(stack.getTagCompound()));
        }
        return plant == null ? null : new AgriSeed(plant, stats.orElseGet(PlantStats::new));
    }

    private IAgriPlant resolveOreDict(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int i : ids) {
            String id = OreDictionary.getOreName(i).replace("_seed", "_plant");
            IAgriPlant plant = PlantRegistry.getInstance().getPlant(id);
            if (plant != null) {
                AgriCore.getLogger("AgriCraft").debug("Resolved OreDict Seed: \"{0}\"!", id);
                return plant;
            }
        }
        return null;
    }

    private IAgriPlant resolveSeedItem(ItemStack stack) {
        return PlantRegistry.getInstance().getPlants().stream()
                .filter(p -> p.getSeedItem().equals(stack.getItem()))
                .findFirst()
                .orElse(null);
    }

}
