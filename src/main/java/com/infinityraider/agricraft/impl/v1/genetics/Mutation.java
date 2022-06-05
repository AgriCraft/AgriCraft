package com.infinityraider.agricraft.impl.v1.genetics;

import com.agricraft.agricore.util.MathHelper;
import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.genetics.IAgriMutation;
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

    private final List<Condition> conditions;

    public Mutation(@Nonnull String id, double chance, @Nonnull IAgriPlant child, @Nonnull IAgriPlant... parents) {
        this(id, chance, child, Arrays.asList(parents));
    }

    public Mutation(@Nonnull String id, double chance, @Nonnull IAgriPlant child, @Nonnull List<IAgriPlant> parents) {
        this.id = Objects.requireNonNull(id);
        this.chance = MathHelper.inRange(chance, 0, 1);
        this.child = Objects.requireNonNull(child);
        this.parents = Objects.requireNonNull(parents);
        this.conditions = Lists.newArrayList();
    }

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

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    @Nonnull
    @Override
    public List<Condition> getConditions() {
        return this.conditions;
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
            sb.append(p.getId()).append(" + ");
        }
        sb.replace(sb.length() - 3, sb.length(), " = ");
        sb.append(this.child.getId());
        return sb.toString();
    }
}
