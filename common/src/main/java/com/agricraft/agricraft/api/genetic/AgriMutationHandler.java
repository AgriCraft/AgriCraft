package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.stat.AgriStat;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.google.common.collect.Sets;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Central class which controls the mutation logic of AgriCraft, which is controlled by three sub-classes:
 * <ul>
 *     <li>The first is the {@link AgriCrossBreedEngine}, which handles the spreading / cross breeding logic between crops in the world
 *     <li>The other two are {@link AgriGeneMutator} objects for the plant species and stats respectively
 * </ul>
 * Any of these implementations can be obtained, replaced, overridden, etc... via the accessors provided in this class
 * The instance of this class can be obtained via {@link AgriApi#getMutationHandler()}
 */
public class AgriMutationHandler {

	private static final AgriMutationHandler INSTANCE = new AgriMutationHandler();

	private AgriCrossBreedEngine engine;
	private AgriGeneMutator<String> plantMutator;
	private AgriGeneMutator<Integer> statMutator;
	private final Map<String, Integer> complexities;
	private boolean computed = false;

	public static AgriMutationHandler getInstance() {
		return INSTANCE;
	}

	private AgriMutationHandler() {
		this.engine = new AgriCrossBreedEngine();
		this.plantMutator = new DefaultPlantMutator();
		this.statMutator = new DefaultStatMutator();
		this.complexities = new HashMap<>();
	}

	public AgriCrossBreedEngine getActiveCrossBreedEngine() {
		return this.engine;
	}

	public void setCrossBreedEngine(AgriCrossBreedEngine engine) {
		this.engine = engine;
	}

	public AgriGeneMutator<String> getActivePlantMutator() {
		return this.plantMutator;
	}

	public void setActivePlantMutator(AgriGeneMutator<String> mutator) {
		this.plantMutator = mutator;
	}

	public AgriGeneMutator<Integer> getActiveStatMutator() {
		return this.statMutator;
	}

	public void setActiveStatMutator(AgriGeneMutator<Integer> mutator) {
		this.statMutator = mutator;
	}

	/**
	 * Calculates the complexity for a plant, the deeper down the mutation tree, the complexer a plant is.
	 * By default, plants with higher complexity are implemented to be dominant, from a genetics point of view,
	 * relative to plants with lower complexities.
	 *
	 * @param plant the plant
	 * @return integer value representing the complexity
	 */
	public int complexity(String plant) {
		// the first time a complexity is wanted, we compute the complexity of each plant
		if (!computed) {
			computed = true;
			setupComplexities();
		}
		return complexities.getOrDefault(plant, 0);
	}

	protected void setupComplexities() {
		Optional<Registry<AgriMutation>> op = AgriApi.getMutationRegistry();
		if (op.isPresent()) {
			Registry<AgriMutation> registry = op.get();
			for (AgriMutation mutation : registry) {
				if (mutation.isValid()) {
					if (this.updateComplexity(mutation.child().toString())) {
						this.updateComplexityForChildren(mutation.child().toString());
					}
				}
			}
		}
	}

	protected boolean updateComplexity(String plant) {
		// Calculate the new complexity as it would appear from the mutation tree
		int newComplexity = this.calculateComplexity(plant);
		// Fetch the old complexity as it existed before the plant was updated
		int oldComplexity = this.complexity(plant);
		// If the new complexity is different, modifications must be made
		if (newComplexity != oldComplexity) {
			// If the nex complexity is zero, that means no parents exist for this plant
			if (newComplexity == 0) {
				// Remove the complexity
				this.complexities.remove(plant);
			} else {
				// Put the new complexity
				this.complexities.put(plant, newComplexity);
			}
			return true;
		}
		return false;
	}

	protected void updateComplexityForChildren(String plant) {
		Set<String> visited = Sets.newIdentityHashSet();
		visited.add(plant);
		this.updateComplexityForChildren(plant, visited);
	}

	protected void updateComplexityForChildren(String plant, Set<String> visited) {
		AgriApi.getMutationRegistry().ifPresent(registry -> registry.stream()
				// Iterate over all mutations which have the given plant as a parent
				.filter(mutation -> mutation.parent1().toString().equals(plant) || mutation.parent2().toString().equals(plant))
				// Map to the child of each mutation which needs updating (the others are mapped to null)
				.map(mutation -> {
					String child = mutation.child().toString();
					if (visited.contains(child)) {
						// Prevent infinite loops
						return null;
					}
					if (this.updateComplexity(child)) {
						// Complexity has been updated, add it to the visited plants and return it
						visited.add(child);
						return child;
					}
					// No changes, return null
					return null;
				})
				// Filter out null plants
				.filter(Objects::nonNull)
				// Iterate recursively
				.forEach(child -> this.updateComplexityForChildren(child, visited)));
	}

	/**
	 * Calculates the complexity for a plant by returning the minimum of all mutations which produce the given plant as child
	 *
	 * @param plant the plant
	 * @return the calculated complexity, if it returns 0, no mutations result in this plant
	 */
	protected int calculateComplexity(String plant) {
		return AgriApi.getMutationRegistry()
				.map(registry -> registry.stream()
						.filter(mutation -> mutation.child().toString().equals(plant))
						.mapToInt(this::calculateComplexity)
						.min().orElse(0))
				.orElse(0);
	}

	/**
	 * Calculates the complexity for a mutation by summing the complexities (as they are currently defined) of the parents
	 *
	 * @param mutation the mutation
	 * @return the calculated complexity, its minimum is 1
	 */
	protected int calculateComplexity(AgriMutation mutation) {
		return this.complexity(mutation.parent1().toString()) + this.complexity(mutation.parent2().toString()) + 1;
	}

	public static class DefaultPlantMutator implements AgriGeneMutator<String> {

		@Override
		public AgriGenePair<String> pickOrMutate(AgriCrop crop, AgriGene<String> gene, AgriAllele<String> first, AgriAllele<String> second, AgriGenome parent1, AgriGenome parent2, RandomSource random) {
			// Search for matching mutations
			// order them randomly
			// fetch the first
			return AgriApi.getMutationsFromParents(first.trait(), second.trait())
					// Find the first result, sorted by trigger result, but shuffled by mutation
					.min((a, b) -> this.sortAndShuffle(a, b, random))
					// map it to its child, or to nothing based on the mutation success rate
					.flatMap(m -> this.evaluate(m, random))
					// map the result to a new gene pair with either of its parents as second gene
					.map(plant -> new AgriGenePair<>(gene, gene.getAllele(plant), random.nextBoolean() ? first : second))
					// if no mutation was found or if the mutation was unsuccessful, return a gene pair of the parents
					.orElse(new AgriGenePair<>(gene, first, second));
		}


		protected int sortAndShuffle(AgriMutation a, AgriMutation b, RandomSource random) {
			// shuffle the mutations randomly
			return a == b ? 0 : random.nextBoolean() ? -1 : 1;
		}

		protected Optional<String> evaluate(AgriMutation mutation, RandomSource random) {
			// Evaluate mutation probability
			if (mutation.chance() > random.nextDouble()) {
				return Optional.of(mutation.child().toString());
			}
			// mutation failed
			return Optional.empty();
		}

	}

	public static class DefaultStatMutator implements AgriGeneMutator<Integer> {

		@Override
		public AgriGenePair<Integer> pickOrMutate(AgriCrop crop, AgriGene<Integer> gene, AgriAllele<Integer> first, AgriAllele<Integer> second, AgriGenome parent1, AgriGenome parent2, RandomSource random) {
			// return new gene pair with or without mutations, based on mutativity stat
			AgriStat mutativity = AgriStatRegistry.getInstance().mutativityStat();
			return new AgriGenePair<>(
					gene,
					this.rollAndExecuteMutation(gene, first, mutativity, parent1.getMutativity(), random),
					this.rollAndExecuteMutation(gene, second, mutativity, parent2.getMutativity(), random)
			);
		}

		protected AgriAllele<Integer> rollAndExecuteMutation(AgriGene<Integer> gene, AgriAllele<Integer> allele, AgriStat mutativity, int statValue, RandomSource random) {
			// Mutativity stat of 1 results in 30.25/45/24.75 probability of positive/no/negative mutation
			// Mutativity stat of 10 results in 100/0/0 probability of positive/no/negative mutation
			int max = mutativity.getMax();
			if (random.nextFloat() >= (1.0 - (double) statValue /max) / 2.0) {
				int delta = random.nextInt(max) < (max + statValue) / 2 ? 1 : -1;
				return gene.getAllele(allele.trait() + delta);
			} else {
				return allele;
			}
		}

	}

}
