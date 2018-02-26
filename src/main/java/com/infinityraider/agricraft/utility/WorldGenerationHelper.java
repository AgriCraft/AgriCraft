package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
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
     * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG containing random
     * stats
     * @param plants List of plants to grab a random SEED from
     * @return an ItemStack containing a random SEED
     */
    public static AgriSeed getRandomSeed(Random rand, boolean setTag, List<IAgriPlant> plants) {
        return new AgriSeed(
                plants.get(rand.nextInt(plants.size())),
                getRandomStat(rand)
        );
    }

    public static IAgriStat getRandomStat(Random rand) {
        return new PlantStats(
                getRandomStatCode(rand),
                getRandomStatCode(rand),
                getRandomStatCode(rand)
        );
    }

    public static int getRandomStatCode(Random rand) {
        return rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1;
    }

}
