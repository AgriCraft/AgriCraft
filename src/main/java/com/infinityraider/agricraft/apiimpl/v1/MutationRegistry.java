/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.requirment.IGrowthRequirement;
import com.infinityraider.agricraft.api.v1.mutation.IMutationRegistry;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import java.util.Arrays;
import java.util.List;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;

/**
 *
 * @author RlonRyan
 */
public class MutationRegistry implements IMutationRegistry {
	
	public static IMutationRegistry getInstance() {
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
	public boolean addMutation(double chance, IAgriPlant child, IAgriPlant... parents) {
		return MutationHandler.add(new Mutation(chance, child, parents));
	}

	@Override
	public boolean removeMutation(IAgriPlant result) {
		MutationHandler.removeMutationsByResult(result.getSeed());
        return true;
	}
	
	
	
}
