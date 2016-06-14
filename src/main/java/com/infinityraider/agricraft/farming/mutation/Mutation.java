package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v1.IMutation;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.utility.MathHelper;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class Mutation implements IMutation {

	private final double chance;

	@Nonnull
	private final IAgriCraftPlant child;
	@Nonnull
	private final IAgriCraftPlant[] parents;

	private final IGrowthRequirement requirement;

	@Override
	public double getChance() {
		return chance;
	}

	@Override
	public IAgriCraftPlant getChild() {
		return child;
	}

	@Override
	public IAgriCraftPlant[] getParents() {
		return parents;
	}

	@Override
	public IGrowthRequirement getRequirement() {
		return requirement;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Mutation) {
			Mutation other = (Mutation) object;
			if (this.chance == other.chance && this.child.equals(other.child) && this.parents.length == other.parents.length) {
				for (int i = 0; i < this.parents.length; i++) {
					if (!this.parents[i].equals(other.parents[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.parents.length; i++) {
			sb.append(this.parents[i].getPlantName());
			if (i < this.parents.length - 1) {
				sb.append(" + ");
			}
		}
		sb.append(" = ").append(this.child.getPlantName());
		return sb.toString();
	}

	public Mutation(double chance, @Nonnull IGrowthRequirement requirement, @Nonnull IAgriCraftPlant child, @Nonnull IAgriCraftPlant... parents) {
		this.chance = MathHelper.inRange(chance, 0, 1);
		this.requirement = requirement;
		this.child = child;
		Arrays.sort(parents);
		this.parents = parents;
	}

}
