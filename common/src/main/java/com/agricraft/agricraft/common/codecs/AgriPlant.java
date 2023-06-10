package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record AgriPlant(boolean enabled, List<String> mods, List<AgriSeed> seeds, List<Integer> stages,
                        int harvestStage,
                        double growthChance, double growthBonus, int tier, boolean cloneable, double spreadChance,
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
			Codec.INT.fieldOf("tier").forGetter(plant -> plant.tier),
			Codec.BOOL.fieldOf("cloneable").forGetter(plant -> plant.cloneable),
			Codec.DOUBLE.fieldOf("spread_chance").forGetter(plant -> plant.spreadChance),
			AgriProduct.CODEC.listOf().optionalFieldOf("products").forGetter(plant -> plant.products.isEmpty() ? Optional.empty() : Optional.of(plant.products)),
			AgriProduct.CODEC.listOf().optionalFieldOf("clipProducts").forGetter(plant -> plant.clipProducts.isEmpty() ? Optional.empty() : Optional.of(plant.clipProducts)),
			AgriRequirement.CODEC.fieldOf("requirement").forGetter(plant -> plant.requirement),
			AgriPlantCallback.CODEC.listOf().optionalFieldOf("callbacks").forGetter(plant -> plant.callbacks.isEmpty() ? Optional.empty() : Optional.of(plant.callbacks)),
			AgriParticleEffect.CODEC.listOf().optionalFieldOf("particle_effects").forGetter(plant -> plant.particleEffects.isEmpty() ? Optional.empty() : Optional.of(plant.particleEffects))
	).apply(instance, AgriPlant::new));

	public AgriPlant(boolean enabled, List<String> mods, List<AgriSeed> seeds, List<Integer> stages, int harvestStage,
	                 double growthChance, double growthBonus, int tier, boolean cloneable, double spreadChance,
	                 Optional<List<AgriProduct>> products, Optional<List<AgriProduct>> clipProducts, AgriRequirement requirement,
	                 Optional<List<AgriPlantCallback>> callbacks, Optional<List<AgriParticleEffect>> particleEffects) {
		this(enabled, mods, seeds, stages, harvestStage, growthChance, growthBonus, tier, cloneable, spreadChance,
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


	public int getGrowthStages() {
		return this.stages.size();
	}

	public int getGrowthStageHeight(int stage) {
		return this.stages.get(stage);
	}

	public boolean checkMods() {
		// FIXME: datapack
//		return this.mods.stream().allMatch(mod -> AgriCore.getValidator().isValidMod(mod));
		return true;
	}

}
