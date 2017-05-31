package com.infinityraider.agricraft.farming.mutation;

import com.agricraft.agricore.util.MathHelper;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Mutation implements IAgriMutation {

    private final double chance;

    @Nonnull
    private final String id;
    @Nonnull
    private final IAgriPlant child;
    @Nonnull
    private final List<IAgriPlant> parents;

    @Override
    public String getId() {
        return id;
    }

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
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.id).append(": ");
        for (IAgriPlant p : this.parents) {
            sb.append(p.getPlantName()).append(" + ");
        }
        sb.replace(sb.length() - 3, sb.length(), " = ");
        sb.append(this.child.getPlantName());
        return sb.toString();
    }

    public Mutation(@Nonnull String id, double chance, @Nonnull IAgriPlant child, @Nonnull IAgriPlant... parents) {
        this(id, chance, child, Arrays.asList(parents));
    }

    public Mutation(@Nonnull String id, double chance, @Nonnull IAgriPlant child, @Nonnull List<IAgriPlant> parents) {
        this.id = Objects.requireNonNull(id);
        this.chance = MathHelper.inRange(chance, 0, 1);
        this.child = Objects.requireNonNull(child);
        this.parents = Objects.requireNonNull(parents);
    }

}
