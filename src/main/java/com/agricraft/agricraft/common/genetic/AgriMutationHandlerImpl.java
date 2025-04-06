package com.agricraft.agricraft.common.genetic;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.genetic.AgriCrossBreedEngine;
import com.agricraft.agricraft.api.genetic.AgriGeneMutator;
import com.agricraft.agricraft.api.genetic.AgriMutationHandler;
import com.google.common.collect.Sets;
import net.minecraft.core.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class AgriMutationHandlerImpl implements AgriMutationHandler {

	private final AgriCrossBreedEngine engine;
	private final AgriGeneMutator<String> plantMutator;
	private final AgriGeneMutator<Integer> statMutator;
	private final Map<String, Integer> complexities;
	private boolean computed = false;

	public AgriMutationHandlerImpl() {
		this.engine = new AgriCrossBreedEngineImpl();
		this.plantMutator = new DefaultPlantMutator();
		this.statMutator = new DefaultStatMutator();
		this.complexities = new HashMap<>();
	}

	@Override
	public AgriCrossBreedEngine getCrossBreedEngine() {
		return this.engine;
	}

	@Override
	public AgriGeneMutator<String> getPlantMutator() {
		return this.plantMutator;
	}

	@Override
	public AgriGeneMutator<Integer> getStatMutator() {
		return this.statMutator;
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
		Optional<Registry<AgriMutation>> op = AgriApi.get().getMutationRegistry();
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
		AgriApi.get().getMutationRegistry().ifPresent(registry -> registry.stream()
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
		return AgriApi.get().getMutationRegistry()
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

}
