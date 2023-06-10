package com.agricraft.agricraft.api.content.world;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.genetics.IAgriGenome;
import com.agricraft.agricraft.api.plant.IAgriPlant;
import com.agricraft.agricraft.api.stat.IAgriStat;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Interface to communicate to the AgriCraft plant worldgen manager
 *
 * The implementation of this class handles the generation of random plants in structures generated in the world
 */
public interface IWorldGenPlantManager {

	/**
	 * @return the static instance of this class
	 */
	static IWorldGenPlantManager getInstance() {
		return AgriApi.getWorldGenPlantManager();
	}

	/**
	 * Generates a random genome to spawn on a crop for a given structure, along the registered rules
	 *
	 * @param structure the structure
	 * @param rand      random number generator
	 * @return optional containing a randomly generated genome, or empty if there are no valid rules for this structure
	 */
	Optional<IAgriGenome> generateGenomeFor(ResourceLocation structure, Random rand);

	/**
	 * Registers a new genome generator to generate plants randomly in the given structure
	 *
	 * @param weight    the weight for this generator to be selected randomly
	 * @param generator function to create new genomes randomly
	 * @param structure the structure where this generator may function
	 */
	void registerGenerator(int weight, Function<Random, IAgriGenome> generator, ResourceLocation structure);

	/**
	 * Registers a new genome generator to generate plants randomly in the given structures
	 *
	 * @param weight     the weight for this generator to be selected randomly
	 * @param generator  function to create new genomes randomly
	 * @param structures the structures where this generator may function
	 */
	default void registerGenerators(int weight, Function<Random, IAgriGenome> generator, ResourceLocation... structures) {
		Arrays.stream(structures).forEach(structure -> this.registerGenerator(weight, generator, structure));
	}

	/**
	 * Registers a new genome generator to generate plants randomly in the given structures
	 *
	 * @param weight     the weight for this generator to be selected randomly
	 * @param generator  function to create new genomes randomly
	 * @param structures the structures where this generator may function
	 */
	default void registerGenerators(int weight, Function<Random, IAgriGenome> generator, List<ResourceLocation> structures) {
		structures.forEach(structure -> this.registerGenerator(weight, generator, structure));
	}

	/**
	 * Registers a new genome generator to generate the given plant randomly in the given structure,
	 * with stats according to the min and max functions
	 *
	 * @param weight    the weight for this generator to be selected randomly
	 * @param min       function determining the allowed minimum stats
	 * @param max       function determining the allowed maximum stats
	 * @param structure the structure where this generator may function
	 */
	default void registerGenerator(int weight, IAgriPlant plant, ToIntFunction<IAgriStat> min, ToIntFunction<IAgriStat> max, ResourceLocation structure) {
		this.registerGenerator(weight, rand -> IAgriGenome.builder(plant).randomStats(min, max, rand).build(), structure);
	}

	/**
	 * Registers a new genome generator to generate the given plant randomly in the given structures,
	 * with stats according to the min and max functions
	 *
	 * @param weight     the weight for this generator to be selected randomly
	 * @param min        function determining the allowed minimum stats
	 * @param max        function determining the allowed maximum stats
	 * @param structures the structures where this generator may function
	 */
	default void registerGenerators(int weight, IAgriPlant plant, ToIntFunction<IAgriStat> min, ToIntFunction<IAgriStat> max, ResourceLocation... structures) {
		Arrays.stream(structures).forEach(structure -> this.registerGenerator(weight, plant, min, max, structure));
	}

	/**
	 * Registers a new genome generator to generate the given plant randomly in the given structures,
	 * with stats according to the min and max functions
	 *
	 * @param weight     the weight for this generator to be selected randomly
	 * @param min        function determining the allowed minimum stats
	 * @param max        function determining the allowed maximum stats
	 * @param structures the structures where this generator may function
	 */
	default void registerGenerators(int weight, IAgriPlant plant, ToIntFunction<IAgriStat> min, ToIntFunction<IAgriStat> max, List<ResourceLocation> structures) {
		structures.forEach(structure -> this.registerGenerator(weight, plant, min, max, structure));
	}

}
