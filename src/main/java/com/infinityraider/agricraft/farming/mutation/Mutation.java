package com.infinityraider.agricraft.farming.mutation;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.plant.IAgriPlant;

import com.agricraft.agricore.util.MathHelper;

public class Mutation implements IAgriMutation {

    private final double chance;

    @Nonnull
    private final IAgriPlant child;
    @Nonnull
    private final List<IAgriPlant> parents;

    @Override
    public double getChance() {
        return chance;
    }

    @Override
    public IAgriPlant getChild() {
        return child;
    }

    @Override
    public List<IAgriPlant> getParents() {
        return parents;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof IAgriMutation) {
            IAgriMutation other = (IAgriMutation) object;
            return other.hasChild(this.child) && other.hasParent(this.parents);
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (IAgriPlant p : this.parents) {
            sb.append(p.getPlantName()).append(" + ");
        }
        sb.replace(sb.length() - 3, sb.length(), " = ");
        sb.append(this.child.getPlantName());
        return sb.toString();
    }

    public Mutation(double chance, @Nonnull IAgriPlant child, @Nonnull IAgriPlant... parents) {
        this(chance, child, Arrays.asList(parents));
    }

    public Mutation(double chance, IAgriPlant child, List<IAgriPlant> parents) {
        this.chance = MathHelper.inRange(chance, 0, 1);
        this.child = child;
        this.parents = parents;
    }

}
