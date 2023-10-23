package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record AgriPlant(boolean enabled, List<String> mods, List<AgriSeed> seeds, List<Integer> stages,
                        int harvestStage,
                        double growthChance, double growthBonus, boolean cloneable, double spreadChance,
                        List<AgriProduct> products, List<AgriProduct> clipProducts, AgriRequirement requirement,
                        List<AgriPlantCallback> callbacks, List<AgriParticleEffect> particleEffects) {

	public static final Codec<AgriPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("enabled").forGetter(plant -> plant.enabled),
			Codec.STRING.listOf().fieldOf("mods").forGetter(plant -> plant.mods),
			AgriSeed.CODEC.listOf().fieldOf("seeds").forGetter(plant -> plant.seeds),
			Codec.INT.listOf().fieldOf("stages").forGetter(plant -> plant.stages),
			Codec.INT.fieldOf("harvest_stage").forGetter(plant -> plant.harvestStage),
			Codec.DOUBLE.fieldOf("growth_chance").forGetter(plant -> plant.growthChance),
			Codec.DOUBLE.fieldOf("growth_bonus").forGetter(plant -> plant.growthBonus),
			Codec.BOOL.fieldOf("cloneable").forGetter(plant -> plant.cloneable),
			Codec.DOUBLE.fieldOf("spread_chance").forGetter(plant -> plant.spreadChance),
			AgriProduct.CODEC.listOf().optionalFieldOf("products").forGetter(plant -> plant.products.isEmpty() ? Optional.empty() : Optional.of(plant.products)),
			AgriProduct.CODEC.listOf().optionalFieldOf("clipProducts").forGetter(plant -> plant.clipProducts.isEmpty() ? Optional.empty() : Optional.of(plant.clipProducts)),
			AgriRequirement.CODEC.fieldOf("requirement").forGetter(plant -> plant.requirement),
			AgriPlantCallback.CODEC.listOf().optionalFieldOf("callbacks").forGetter(plant -> plant.callbacks.isEmpty() ? Optional.empty() : Optional.of(plant.callbacks)),
			AgriParticleEffect.CODEC.listOf().optionalFieldOf("particle_effects").forGetter(plant -> plant.particleEffects.isEmpty() ? Optional.empty() : Optional.of(plant.particleEffects))
	).apply(instance, AgriPlant::new));

	public static final AgriPlant NO_PLANT = AgriPlant.builder()
			.harvest(0).chances(0, 0, 0)
			.build();
	public static final ResourceLocation UNKNOWN = new ResourceLocation("agricraft:unknown");

	public AgriPlant(boolean enabled, List<String> mods, List<AgriSeed> seeds, List<Integer> stages, int harvestStage,
	                 double growthChance, double growthBonus, boolean cloneable, double spreadChance,
	                 Optional<List<AgriProduct>> products, Optional<List<AgriProduct>> clipProducts, AgriRequirement requirement,
	                 Optional<List<AgriPlantCallback>> callbacks, Optional<List<AgriParticleEffect>> particleEffects) {
		this(enabled, mods, seeds, stages, harvestStage, growthChance, growthBonus, cloneable, spreadChance,
				products.orElse(List.of()), clipProducts.orElse(List.of()), requirement,
				callbacks.orElse(List.of()), particleEffects.orElse(List.of()));
	}

//    private String path;
//    private final String version;
//    private final String json_documentation = "https://agridocs.readthedocs.io/en/master/agri_plant/";
//    private final String id;
//
//    private final String plant_lang_key;
//    private final String seed_lang_key;
//    private final String desc_lang_key;
//

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		List<String> mods = new ArrayList<>();
		List<AgriSeed> seeds = new ArrayList<>();
		List<Integer> stages = new ArrayList<>();
		int harvestStage = 4;
		double growthChance = 0.75;
		double growthBonus = 0.025;
		double spreadChance = 0.1;
		boolean cloneable = true;
		List<AgriProduct> products = new ArrayList<>();
		List<AgriProduct> clipProducts = List.of();
		AgriRequirement requirement = AgriRequirement.NO_REQUIREMENT;
		List<AgriPlantCallback> callbacks = List.of();
		List<AgriParticleEffect> particleEffects = List.of();

		public AgriPlant build() {
			return new AgriPlant(true, mods, seeds, stages, harvestStage, growthChance, growthBonus, cloneable, spreadChance, products, clipProducts, requirement, callbacks, particleEffects);
		}

		public Builder mods(String... mods) {
			Collections.addAll(this.mods, mods);
			return this;
		}

		public Builder seeds(AgriSeed... seeds) {
			Collections.addAll(this.seeds, seeds);
			return this;
		}

		public Builder stages(Integer... stages) {
			Collections.addAll(this.stages, stages);
			return this;
		}

		public Builder stages16() {
			for (int i = 2; i < 17; i += 2) {
				this.stages.add(i);
			}
			return this;
		}

		public Builder harvest(int stage) {
			this.harvestStage = stage;
			return this;
		}

		public Builder chances(double growth, double growthBonus, double spread) {
			this.growthChance = growth;
			this.growthBonus = growthBonus;
			this.spreadChance = spread;
			return this;
		}

		public Builder cloneable(boolean cloneable) {
			this.cloneable = cloneable;
			return this;
		}

		public Builder products(AgriProduct... products) {
			Collections.addAll(this.products, products);
			return this;
		}

		public Builder clips(AgriProduct... clips) {
			this.clipProducts = List.of(clips);
			return this;
		}

		public Builder requirement(AgriRequirement requirement) {
			this.requirement = requirement;
			return this;
		}

		public Builder callbacks(AgriPlantCallback... callbacks) {
			this.callbacks = List.of(callbacks);
			return this;
		}

		public Builder particles(AgriParticleEffect... particles) {
			this.particleEffects = List.of(particles);
			return this;
		}

	}

}
