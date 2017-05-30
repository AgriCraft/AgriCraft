/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.AgriApi;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author The AgriCraft Team
 */
public class MutationRegistry implements IAgriMutationRegistry {

    private final List<IAgriMutation> mutations;

    public MutationRegistry() {
        this.mutations = new ArrayList<>();
    }

    @Override
    public Collection<IAgriMutation> getMutations() {
        return Collections.unmodifiableCollection(this.mutations);
    }

    @Override
    public Stream<IAgriMutation> streamMutations() {
        return this.mutations.stream();
    }

    @Override
    public boolean addMutation(double chance, String childId, String... parentIds) {
        IAgriPlant child = AgriApi.PlantRegistry().get().getPlant(childId);
        if (child == null) {
            return false;
        }
        IAgriPlant[] parents = new IAgriPlant[parentIds.length];
        for (int i = 0; i < parentIds.length; i++) {
            parents[i] = AgriApi.PlantRegistry().get().getPlant(parentIds[i]);
            if (parents[i] == null) {
                return false;
            }
        }
        return this.mutations.add(new Mutation(chance, child, parents));
    }

    @Override
    public boolean addMutation(IAgriMutation mutation) {
        return this.mutations.contains(mutation) ? false : this.mutations.add(mutation);
    }

}
