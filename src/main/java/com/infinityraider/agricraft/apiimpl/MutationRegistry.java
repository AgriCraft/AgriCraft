/*
 */
package com.infinityraider.agricraft.apiimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.farming.mutation.Mutation;

/**
 *
 * @author The AgriCraft Team
 */
public class MutationRegistry implements IAgriMutationRegistry {

    private static final IAgriMutationRegistry INSTANCE = new MutationRegistry();

    private final List<IAgriMutation> mutations = new ArrayList<>();

    public static IAgriMutationRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public List<IAgriMutation> getMutations() {
        return new ArrayList<>(this.mutations);
    }

    @Override
    public List<IAgriMutation> getMutationsForParent(IAgriPlant parent) {
        return this.mutations.stream()
                .filter(m -> m.hasParent(parent))
                .collect(Collectors.toList());
    }

    @Override
    public List<IAgriMutation> getMutationsForParent(Collection<IAgriPlant> parents) {
        return this.mutations.stream()
                .filter(m -> m.hasParent(parents))
                .collect(Collectors.toList());
    }

    @Override
    public List<IAgriMutation> getMutationsForChild(IAgriPlant child) {
        return this.mutations.stream()
                .filter(m -> m.hasChild(child))
                .collect(Collectors.toList());
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
        return this.mutations.add(new Mutation(chance, child, parents));
    }

    @Override
    public boolean addMutation(IAgriMutation mutation) {
        return this.mutations.contains(mutation) ? false : this.mutations.add(mutation);
    }

    @Override
    public boolean removeMutation(IAgriPlant result) {
        return this.mutations.removeIf(m -> m.getChild().equals(result));
    }

    @Override
    public List<IAgriMutation> getPossibleMutations(Collection<IAgriPlant> parents) {
        return this.mutations.stream()
                .filter(m -> m.areParentsIn(parents))
                .collect(Collectors.toList());
    }

}
