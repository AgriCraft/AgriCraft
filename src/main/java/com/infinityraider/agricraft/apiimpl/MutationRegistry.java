/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import java.util.Arrays;
import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;

/**
 * Todo: Rework!
 * @author RlonRyan
 */
public class MutationRegistry implements IAgriMutationRegistry {
	
	private static final IAgriMutationRegistry INSTANCE = new MutationRegistry();
	
	public static IAgriMutationRegistry getInstance() {
		return INSTANCE;
	}

	@Override
	public List<IAgriMutation> getMutations() {
		return Arrays.asList(MutationHandler.getMutations());
	}

	@Override
	public List<IAgriMutation> getMutationsForParent(IAgriPlant parent) {
		return Arrays.asList(MutationHandler.getMutationsFromParent(parent));
	}

	@Override
	public List<IAgriMutation> getMutationsForChild(IAgriPlant child) {
		return Arrays.asList(MutationHandler.getMutationsFromChild(child));
	}

	@Override
	public boolean addMutation(double chance, String childId, String... parentIds) {
		IAgriPlant child = PlantRegistry.getInstance().getPlant(childId);
		if (child == null) {
			return false;
		}
		IAgriPlant[] parents = new IAgriPlant[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			parents[i] = PlantRegistry.getInstance().getPlant(parentIds[i]);
			if (parents[i] == null) {
				return false;
			}
		}
		return MutationHandler.add(new Mutation(chance, child, parents));
	}

	@Override
	public boolean addMutation(IAgriMutation mutation) {
		return MutationHandler.add(mutation);
	}

	@Override
	public boolean removeMutation(IAgriPlant result) {
		MutationHandler.removeMutationsByResult(result.getSeed());
        return true;
	}
	
	
	
}
