/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import java.util.Arrays;
import java.util.List;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;

/**
 *
 * @author RlonRyan
 */
public class MutationRegistry implements IAgriMutationRegistry {
	
	public static IAgriMutationRegistry getInstance() {
		return APIimplv1.getInstance().getMutationRegistry();
	}

	@Override
	public List<IAgriMutation> getRegisteredMutations() {
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
	public boolean removeMutation(IAgriPlant result) {
		MutationHandler.removeMutationsByResult(result.getSeed());
        return true;
	}
	
	
	
}
