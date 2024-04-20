package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.common.util.Platform;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AgriWeed {

	public static final Codec<AgriWeed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("mods").forGetter(weed -> weed.mods),
			Codec.INT.listOf().fieldOf("stages").forGetter(weed -> weed.stages),
			Codec.DOUBLE.fieldOf("spawn_chance").forGetter(weed -> weed.spawnChance),
			Codec.DOUBLE.fieldOf("growth_chance").forGetter(weed -> weed.growthChance),
			Codec.BOOL.fieldOf("aggressive").forGetter(weed -> weed.aggressive),
			Codec.BOOL.fieldOf("lethal").forGetter(weed -> weed.lethal),
			AgriProduct.CODEC.listOf().optionalFieldOf("rake_products").forGetter(weed -> weed.rakeProducts.isEmpty() ? Optional.empty() : Optional.of(weed.rakeProducts)),
			AgriRequirement.CODEC.fieldOf("requirement").forGetter(weed -> weed.requirement)
	).apply(instance, AgriWeed::new));

	private final List<String> mods;
	private final List<Integer> stages;
	private final double spawnChance;
	private final double growthChance;
	private final boolean aggressive;
	private final boolean lethal;
	private final List<AgriProduct> rakeProducts;
	private final AgriRequirement requirement;

	public AgriWeed(List<String> mods, List<Integer> stages, double spawnChance, double growthChance, boolean aggressive, boolean lethal, List<AgriProduct> rakeProducts, AgriRequirement requirement) {
		this.mods = mods;
		this.stages = stages;
		this.spawnChance = spawnChance;
		this.growthChance = growthChance;
		this.aggressive = aggressive;
		this.lethal = lethal;
		this.rakeProducts = rakeProducts;
		this.requirement = requirement;
	}

	public AgriWeed(List<String> mods, List<Integer> stages, double spawnChance, double growthChance, boolean aggressive, boolean lethal, Optional<List<AgriProduct>> rakeProducts, AgriRequirement requirement) {
		this(mods, stages, spawnChance, growthChance, aggressive, lethal, rakeProducts.orElse(List.of()), requirement);
	}

	public double getSpawnChance(AgriCrop crop) {
		return this.spawnChance;
	}

	public double getGrowthChance(AgriGrowthStage growthStage) {
		return this.growthChance;
	}

	public boolean isAggressive() {
		return this.aggressive;
	}

	public boolean isLethal() {
		return this.lethal;
	}

	/**
	 * Called when the weed is raked
	 *
	 * @param growthStage the growth stage of the weed
	 * @param consumer    calls this to add items to the drops list
	 * @param random      random source
	 * @param entity      the entity who raked the weed (can be null in case it is raked trough automation)
	 */
	public void onRake(AgriGrowthStage growthStage, Consumer<ItemStack> consumer, RandomSource random, @Nullable LivingEntity entity) {
		if (growthStage.index() < 0) {
			return;
		}
		if (CoreConfig.rakingDropsItems) {
			double probability = growthStage.index() / (this.stages.size() - 1.0);
			if (random.nextDouble() < probability) {
				this.rakeProducts.stream().filter(product -> product.shouldDrop(random))
						.forEach(product -> {
							List<Item> possible = Platform.get().getItemsFromLocation(product.item());
							Item item = possible.get(random.nextInt(possible.size()));
							ItemStack itemStack = new ItemStack(item, product.getAmount(random));
							itemStack.getOrCreateTag().merge(product.nbt());
							consumer.accept(itemStack);
						});
			}
		}
	}

	/**
	 * @return the amount of growth stages this weed has
	 */
	public int getGrowthStages() {
		return this.stages.size();
	}

	public AgriGrowthStage getInitialGrowthStage() {
		return new AgriGrowthStage(0, this.stages.size());
	}

	public int getWeedHeight(AgriGrowthStage stage) {
		if (stage.index() < 0 || stage.index() >= this.stages.size()) {
			return 0;
		}
		return this.stages.get(stage.index());
	}

	public static class Builder {

		private List<String> mods = new ArrayList<>();
		private List<Integer> stages = new ArrayList<>();
		private double spawnChance = 0.25;
		private double growthChance = 0.9;
		private boolean aggressive = true;
		private boolean lethal = true;
		private List<AgriProduct> rakeProducts = new ArrayList<>();
		private AgriRequirement requirement = AgriRequirement.NO_REQUIREMENT;

		public AgriWeed build() {
			return new AgriWeed(mods, stages, spawnChance, growthChance, aggressive, lethal, rakeProducts, requirement);
		}

		public Builder mods(String... mods) {
			Collections.addAll(this.mods, mods);
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

		public Builder spawnChance(double spawnChance) {
			this.spawnChance = spawnChance;
			return this;
		}

		public Builder growthChance(double growthChance) {
			this.growthChance = growthChance;
			return this;
		}

		public Builder aggressive(boolean aggressive) {
			this.aggressive = aggressive;
			return this;
		}

		public Builder lethal(boolean lethal) {
			this.lethal = lethal;
			return this;
		}

		public Builder rakeProducts(AgriProduct... rakeProducts) {
			Collections.addAll(this.rakeProducts, rakeProducts);
			return this;
		}

		public Builder requirement(AgriRequirement requirement) {
			this.requirement = requirement;
			return this;
		}

	}

}
