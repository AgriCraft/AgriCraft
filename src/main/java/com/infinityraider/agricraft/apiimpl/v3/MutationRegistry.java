/*
 */
package com.infinityraider.agricraft.apiimpl.v3;

import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v3.IGrowthRequirement;
import com.infinityraider.agricraft.api.v3.IMutation;
import com.infinityraider.agricraft.api.v3.registry.IMutationRegistry;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author RlonRyan
 */
public class MutationRegistry implements IMutationRegistry {

	@Override
	public List<IMutation> getRegisteredMutations() {
		return Arrays.asList(MutationHandler.getMutations());
	}

	@Override
	public List<IMutation> getMutationsForParent(IAgriCraftPlant parent) {
		return Arrays.asList(MutationHandler.getMutationsFromParent(parent));
	}

	@Override
	public List<IMutation> getMutationsForChild(IAgriCraftPlant child) {
		return Arrays.asList(MutationHandler.getMutationsFromChild(child));
	}

	@Override
	public boolean addMutation(double chance, IGrowthRequirement requirement, IAgriCraftPlant child, IAgriCraftPlant... parents) {
		return MutationHandler.add(new Mutation(chance, requirement, child, parents));
	}

	@Override
	public boolean removeMutation(IAgriCraftPlant result) {
		MutationHandler.removeMutationsByResult(result.getSeed());
        return true;
	}
	
	
	
}
