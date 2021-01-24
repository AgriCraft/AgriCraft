package com.infinityraider.agricraft.util;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;

import java.util.List;
import java.util.Random;

/**
 * A helper class for working with generation.
 *
 *
 */
public class WorldGenerationHelper {

    /**
     * Gets a random SEED from a list of plants
     *
     * @param rand Random object to be used
     * @param plants List of plants to grab a random SEED from
     * @return an ItemStack containing a random SEED
     */
    public static AgriSeed getRandomSeed(Random rand, List<IAgriPlant> plants) {
        IAgriPlant plant = plants.get(rand.nextInt(plants.size()));
        return new AgriSeed(generateRandomGenome(plant, rand));
    }

    public static IAgriGenome generateRandomGenome(IAgriPlant plant, Random rand) {
        return AgriApi.getAgriGenomeBuilder(plant).randomStats(stat -> stat.getMax() / 2, rand).build();
    }
}
