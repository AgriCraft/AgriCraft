package com.agricraft.agricraft.api.codecs;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record AgriMutation(ResourceLocation child, ResourceLocation parent1, ResourceLocation parent2, double chance) {

	// TODO: @Ketheroth mutation condition (check what this is in the old agricraft code)

	public static final Codec<AgriMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("child").forGetter(mutation -> mutation.child),
			ResourceLocation.CODEC.fieldOf("parent1").forGetter(mutation -> mutation.parent1),
			ResourceLocation.CODEC.fieldOf("parent2").forGetter(mutation -> mutation.parent2),
			Codec.DOUBLE.fieldOf("chance").forGetter(mutation -> mutation.chance)
	).apply(instance, AgriMutation::new));

	public AgriMutation(String child, String parent1, String parent2, double chance) {
		this(new ResourceLocation(child), new ResourceLocation(parent1), new ResourceLocation(parent2), chance);
	}

	public Optional<AgriPlant> getParent1() {
		return AgriApi.getPlant(this.parent1);
	}

	public Optional<AgriPlant> getParent2() {
		return AgriApi.getPlant(this.parent2);
	}

	public Optional<AgriPlant> getChild() {
		return AgriApi.getPlant(this.child);
	}

	public boolean isValid() {
		return getChild().isPresent() && getParent1().isPresent() && getParent2().isPresent();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		double chance = 0;
		ResourceLocation child;
		ResourceLocation parent1;
		ResourceLocation parent2;

		public AgriMutation build() {
			return new AgriMutation(child, parent1, parent2, chance);
		}

		public Builder chance(double chance) {
			this.chance = chance;
			return this;
		}

		public Builder child(String child) {
			this.child = new ResourceLocation(child);
			return this;
		}

		public Builder child(ResourceLocation child) {
			this.child = child;
			return this;
		}

		public Builder parent1(String parent1) {
			this.parent1 = new ResourceLocation(parent1);
			return this;
		}

		public Builder parent1(ResourceLocation parent1) {
			this.parent1 = parent1;
			return this;
		}

		public Builder parent2(String parent2) {
			this.parent2 = new ResourceLocation(parent2);
			return this;
		}

		public Builder parent2(ResourceLocation parent2) {
			this.parent2 = parent2;
			return this;
		}

		public Builder parents(String parent1, String parent2) {
			this.parent1 = new ResourceLocation(parent1);
			this.parent2 = new ResourceLocation(parent2);
			return this;
		}

		public Builder parents(ResourceLocation parent1, ResourceLocation parent2) {
			this.parent1 = parent1;
			this.parent2 = parent2;
			return this;
		}

	}

}
