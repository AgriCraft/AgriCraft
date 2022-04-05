package com.infinityraider.agricraft.util;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.AgriTags;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Random;

public class GreenHouseHelper {
    public static boolean isGreenHouseGlass(BlockState state) {
        return state.is(Tags.Blocks.GLASS) || state.is(AgriTags.Blocks.GREENHOUSE_GLASS);
    }

    /**
     * Gets a random genome from a list of plants
     *
     * @param rand Random object to be used
     * @param plants List of plants to grab a random SEED from
     * @return an ItemStack containing a random SEED
     */
    public static IAgriGenome getRandomSeed(Random rand, List<IAgriPlant> plants) {
        return generateRandomGenome(plants.get(rand.nextInt(plants.size())), rand);
    }

    public static IAgriGenome generateRandomGenome(IAgriPlant plant, Random rand) {
        return AgriApi.getAgriGenomeBuilder(plant).randomStats(stat -> stat.getMax() / 2, rand).build();
    }
}
