package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.TagUtils;
import com.agricraft.agricraft.api.codecs.AgriParticleEffect;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriRequirement;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

// TODO: @Ketheroth convert nbt to component ?
// TODO: @Ketheroth add javadoc to all methods
public class AgriPlant {

	public static final ResourceKey<Registry<AgriPlant>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "plant"));

	public static final Codec<AgriPlant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("mods").forGetter(plant -> plant.mods),
			AgriSeed.CODEC.listOf().fieldOf("seeds").forGetter(plant -> plant.seeds),
			Codec.INT.listOf().fieldOf("stages").forGetter(plant -> plant.stages),
			Codec.INT.fieldOf("harvest_stage").forGetter(plant -> plant.harvestStage),
			Codec.DOUBLE.fieldOf("growth_chance").forGetter(plant -> plant.growthChance),
			Codec.DOUBLE.fieldOf("growth_bonus").forGetter(plant -> plant.growthBonus),
			Codec.BOOL.fieldOf("cloneable").forGetter(plant -> plant.cloneable),
			Codec.BOOL.optionalFieldOf("fireproof", false).forGetter(plant -> plant.fireproof),
			Codec.DOUBLE.fieldOf("spread_chance").forGetter(plant -> plant.spreadChance),
			Codec.DOUBLE.optionalFieldOf("seed_drop_chance", 1.0).forGetter(plant -> plant.seedDropChance),
			Codec.DOUBLE.optionalFieldOf("seed_drop_bonus", 0.0).forGetter(plant -> plant.seedDropBonus),
			AgriProduct.CODEC.listOf().optionalFieldOf("products", List.of()).forGetter(plant -> plant.products),
			AgriProduct.CODEC.listOf().optionalFieldOf("clipProducts", List.of()).forGetter(plant -> plant.clipProducts),
			AgriRequirement.CODEC.fieldOf("requirement").forGetter(plant -> plant.requirement),
			AgriPlantModifierInfo.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(plant -> plant.modifierInfos),
			AgriParticleEffect.CODEC.listOf().optionalFieldOf("particle_effects", List.of()).forGetter(plant -> plant.particleEffects)
	).apply(instance, AgriPlant::new));

	public static final AgriPlant NO_PLANT = new AgriPlant.Builder().harvest(0).chances(0, 0, 0).build();
	public static final ResourceLocation UNKNOWN = ResourceLocation.fromNamespaceAndPath("agricraft", "unknown");

	private final List<String> mods;
	private final List<AgriSeed> seeds;
	private final List<Integer> stages;
	private final int harvestStage;
	private final double growthChance;
	private final double growthBonus;
	private final boolean cloneable;
	private final boolean fireproof;
	private final double spreadChance;
	private final double seedDropChance;
	private final double seedDropBonus;
	private final List<AgriProduct> products;
	private final List<AgriProduct> clipProducts;
	private final AgriRequirement requirement;
	private final List<AgriPlantModifierInfo> modifierInfos;
	private final List<AgriParticleEffect> particleEffects;
	private List<IAgriPlantModifier> modifiers;

	public AgriPlant(List<String> mods, List<AgriSeed> seeds, List<Integer> stages,
	                 int harvestStage, double growthChance, double growthBonus, boolean cloneable, boolean fireproof,
	                 double spreadChance, double seedDropChance, double seedDropBonus,
	                 List<AgriProduct> products, List<AgriProduct> clipProducts, AgriRequirement requirement,
	                 List<AgriPlantModifierInfo> modifierInfos, List<AgriParticleEffect> particleEffects) {
		this.mods = mods;
		this.seeds = seeds;
		this.stages = stages;
		this.harvestStage = harvestStage;
		this.growthChance = growthChance;
		this.growthBonus = growthBonus;
		this.cloneable = cloneable;
		this.fireproof = fireproof;
		this.spreadChance = spreadChance;
		this.seedDropChance = seedDropChance;
		this.seedDropBonus = seedDropBonus;
		this.products = products;
		this.clipProducts = clipProducts;
		this.requirement = requirement;
		this.modifierInfos = modifierInfos;
		this.particleEffects = particleEffects;
	}

	public AgriPlant(List<String> mods, List<AgriSeed> seeds, List<Integer> stages, int harvestStage,
	                 double growthChance, double growthBonus, boolean cloneable, boolean fireproof, double spreadChance,
	                 Optional<Double> seedDropChance, Optional<Double> seedDropBonus,
	                 Optional<List<AgriProduct>> products, Optional<List<AgriProduct>> clipProducts, AgriRequirement requirement,
	                 Optional<List<AgriPlantModifierInfo>> modifierInfos, Optional<List<AgriParticleEffect>> particleEffects) {
		this(mods, seeds, stages, harvestStage, growthChance, growthBonus, cloneable, fireproof,
				spreadChance, seedDropChance.orElse(1.0), seedDropBonus.orElse(0.0),
				products.orElse(List.of()), clipProducts.orElse(List.of()), requirement,
				modifierInfos.orElse(List.of()), particleEffects.orElse(List.of()));
	}

	public double getSpreadChance(AgriGrowthStage growthStage) {
		return this.spreadChance;
	}

	public double getGrowthChance(AgriGrowthStage growthStage) {
		return this.growthChance;
	}

	public double getBonusGrowthChance(AgriGrowthStage growthStage) {
		return this.growthBonus;
	}

	public double getSeedDropChance(AgriGrowthStage growthStage) {
		return this.seedDropChance;
	}

	public double getBonusSeedDropChance(AgriGrowthStage growthStage) {
		return this.seedDropBonus;
	}

	public AgriGrowthStage getInitialGrowthStage() {
		return new AgriGrowthStage(0, this.stages.size());
	}

	public AgriGrowthStage getGrowthStageAfterHarvest() {
		return new AgriGrowthStage(this.harvestStage, this.stages.size());
	}

	public int getPlantHeight(AgriGrowthStage stage) {
		if (stage.index() < 0 || stage.index() >= this.stages.size()) {
			return 0;
		}
		return this.stages.get(stage.index());
	}

	public boolean isSeedItem(ItemStack stack) {
		return this.seeds.stream().anyMatch(seed -> seed.isVariant(stack));
	}

	public Stream<AgriSeed> seeds() {
		return this.seeds.stream();
	}

	public AgriRequirement getGrowthRequirements(/*AgriGrowthStage growthStage*/) {
		return this.requirement;
	}

	public void getAllPossibleProducts(Consumer<ItemStack> products) {
		this.products.forEach(product -> {
			TagUtils.items(product.item()).forEach(item -> {
				ItemStack itemStack = new ItemStack(item, product.min());
//				itemStack.getOrCreateTag().merge(product.nbt());
				products.accept(itemStack);
			});
		});
	}

	public void getHarvestProducts(Consumer<ItemStack> products, AgriGrowthStage growthStage, AgriGenome genome, RandomSource random) {
		if (growthStage.isMature()) {
			this.products.stream().filter(product -> product.shouldDrop(random))
					.forEach(product -> {
						List<Item> possible = TagUtils.items(product.item());
						Item item = possible.get(random.nextInt(possible.size()));
						ItemStack itemStack = new ItemStack(item, product.getAmount(random));
//						itemStack.getOrCreateTag().merge(product.nbt());
						products.accept(itemStack);
					});
		}

	}

	public void getAllPossibleClipProducts(Consumer<ItemStack> products) {
		this.clipProducts.forEach(product -> {
			TagUtils.items(product.item()).forEach(item -> products.accept(new ItemStack(item)));
		});
	}

	public void getClipProducts(Consumer<ItemStack> products, ItemStack clipper, AgriGrowthStage growthStage, AgriGenome genome, RandomSource random) {
		if (growthStage.isMature()) {
			this.clipProducts.stream().filter(product -> product.shouldDrop(random))
					.forEach(product -> {
						List<Item> possible = TagUtils.items(product.item());
						Item item = possible.get(random.nextInt(possible.size()));
						ItemStack itemStack = new ItemStack(item, product.getAmount(random));
//						itemStack.getOrCreateTag().merge(product.nbt());
						products.accept(itemStack);
					});
		}
	}

	public boolean allowsCloning(AgriGrowthStage growthStage) {
		return this.cloneable;
	}

	public boolean isFireproof() {
		return this.fireproof;
	}

	public void spawnParticles(AgriCrop crop, RandomSource random) {
		AgriGrowthStage stage = crop.getGrowthStage();
		Level world = crop.getLevel();
		if (stage.index() == -1 || world == null) {
			return;
		}
		this.particleEffects.stream()
				.filter(effect -> effect.allowParticles(stage.index()))
				.forEach(effect -> {
					ParticleType<?> particle = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(effect.particle()));
					if (!(particle instanceof ParticleOptions)) {
						return;
					}
					for (int amount = 0; amount < 3; ++amount) {
						if (random.nextDouble() < effect.probability()) {
							BlockPos pos = crop.getBlockPos();
							double x = pos.getX() + 0.5D + (random.nextBoolean() ? 1 : -1) * effect.deltaX() * random.nextDouble();
							double y = pos.getY() + 0.5D + effect.deltaY() * random.nextDouble();
							double z = pos.getZ() + 0.5D + (random.nextBoolean() ? 1 : -1) * effect.deltaZ() * random.nextDouble();
							world.addParticle((ParticleOptions) particle, x, y, z, 0.0D, 0.0D, 0.0D);
						}
					}
				});
	}

	public boolean allowHarvest(AgriGrowthStage growthStage, @Nullable LivingEntity entity) {
		return growthStage.isMature();
	}

	public boolean allowsClipping(AgriGrowthStage growthStage, ItemStack clipper, @Nullable LivingEntity entity) {
		return growthStage.isMature();
	}

	public Stream<IAgriPlantModifier> getModifiers() {
		if (this.modifiers == null) {
			this.modifiers = new ArrayList<>();
			this.modifierInfos.stream().map(info -> AgriApi.get().getPlantModifierFactoryRegistry().getOptional(info.id()).flatMap(factory -> factory.construct(info)))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.forEach(modifier -> this.modifiers.add(modifier));
		}
		return this.modifiers.stream();
	}

	public int getBrightness(AgriCrop crop) {
		return this.getModifiers().mapToInt(modifier -> modifier.getBrightness(crop)).max().orElse(0);
	}

	public int getRedstonePower(AgriCrop crop) {
		return this.getModifiers().mapToInt(modifier -> modifier.getRedstonePower(crop)).max().orElse(0);
	}

	public void onPlanted(AgriCrop crop, @Nullable LivingEntity entity) {
		this.getModifiers().forEach(modifier -> modifier.onPlanted(crop, entity));
	}

	public void onSpawned(AgriCrop crop) {
		this.getModifiers().forEach(modifier -> modifier.onSpawned(crop));
	}

	public void onRandomTick(AgriCrop crop, RandomSource random) {
		this.getModifiers().forEach(modifier -> modifier.onRandomTick(crop, random));
	}

	public void onGrowth(AgriCrop crop) {
		this.getModifiers().forEach(modifier -> modifier.onGrowth(crop));
	}

	public void onRemoved(AgriCrop crop) {
		this.getModifiers().forEach(modifier -> modifier.onRemoved(crop));
	}

	public void onHarvest(AgriCrop crop, @Nullable LivingEntity entity) {
		this.getModifiers().forEach(modifier -> modifier.onHarvest(crop, entity));
	}

	public void onClipped(AgriCrop crop, ItemStack clipper, @Nullable LivingEntity entity) {
		this.getModifiers().forEach(modifier -> modifier.onClipped(crop, clipper, entity));
	}

	public void onFertilized(AgriCrop crop, ItemStack fertilizer, RandomSource random) {
		this.getModifiers().forEach(modifier -> modifier.onFertilized(crop, fertilizer, random));
	}

	public void onBroken(AgriCrop crop, @Nullable LivingEntity entity) {
		this.getModifiers().forEach(modifier -> modifier.onBroken(crop, entity));
	}

	public void onEntityCollision(AgriCrop crop, Entity entity) {
		this.getModifiers().forEach(modifier -> modifier.onEntityCollision(crop, entity));
	}

	public Optional<ItemInteractionResult> onRightClickPre(AgriCrop crop, ItemStack stack, @Nullable Entity entity) {
		return this.getModifiers()
				.map(modifier -> modifier.onRightClickPre(crop, stack, entity))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	public Optional<ItemInteractionResult> onRightClickPost(AgriCrop crop, ItemStack stack, @Nullable Entity entity) {
		return this.getModifiers()
				.map(modifier -> modifier.onRightClickPost(crop, stack, entity))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	public Optional<ResourceLocation> getId() {
		return AgriApi.get().getPlantRegistry().flatMap(registry -> Optional.ofNullable(registry.getKey(this)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (AgriPlant) obj;
		return Objects.equals(this.mods, that.mods) &&
				Objects.equals(this.seeds, that.seeds) &&
				Objects.equals(this.stages, that.stages) &&
				this.harvestStage == that.harvestStage &&
				Double.doubleToLongBits(this.growthChance) == Double.doubleToLongBits(that.growthChance) &&
				Double.doubleToLongBits(this.growthBonus) == Double.doubleToLongBits(that.growthBonus) &&
				this.cloneable == that.cloneable &&
				this.fireproof == that.fireproof &&
				Double.doubleToLongBits(this.spreadChance) == Double.doubleToLongBits(that.spreadChance) &&
				Objects.equals(this.products, that.products) &&
				Objects.equals(this.clipProducts, that.clipProducts) &&
				Objects.equals(this.requirement, that.requirement) &&
				Objects.equals(this.modifierInfos, that.modifierInfos) &&
				Objects.equals(this.particleEffects, that.particleEffects);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mods, seeds, stages, harvestStage, growthChance, growthBonus, cloneable, fireproof, spreadChance, products, clipProducts, requirement, modifierInfos, particleEffects);
	}

	@Override
	public String toString() {
		return "AgriPlant[" +
				"mods=" + mods + ", " +
				"seeds=" + seeds + ", " +
				"stages=" + stages + ", " +
				"harvestStage=" + harvestStage + ", " +
				"growthChance=" + growthChance + ", " +
				"growthBonus=" + growthBonus + ", " +
				"cloneable=" + cloneable + ", " +
				"fireproof=" + fireproof + ", " +
				"spreadChance=" + spreadChance + ", " +
				"products=" + products + ", " +
				"clipProducts=" + clipProducts + ", " +
				"requirement=" + requirement + ", " +
				"modifiers=" + modifierInfos + ", " +
				"particleEffects=" + particleEffects + ']';
	}


	public static class Builder {

		List<String> mods = new ArrayList<>();
		List<AgriSeed> seeds = new ArrayList<>();
		List<Integer> stages = new ArrayList<>();
		int harvestStage = 4;
		double growthChance = 0.75;
		double growthBonus = 0.025;
		double spreadChance = 0.1;
		double seedDropChance = 1.0;
		double seedDropBonus = 0.0;
		boolean cloneable = true;
		boolean fireproof = false;
		List<AgriProduct> products = new ArrayList<>();
		List<AgriProduct> clipProducts = new ArrayList<>();
		AgriRequirement requirement = AgriRequirement.NO_REQUIREMENT;
		List<AgriPlantModifierInfo> modifiers = new ArrayList<>();
		List<AgriParticleEffect> particleEffects = new ArrayList<>();

		public static Builder from(AgriPlant plant) {
			return new Builder().mods(plant.mods.toArray(new String[0]))
					.seeds(plant.seeds.toArray(new AgriSeed[0]))
					.stages(plant.stages.toArray(new Integer[0]))
					.harvest(plant.harvestStage)
					.chances(plant.growthChance, plant.growthBonus, plant.spreadChance)
					.cloneable(plant.cloneable)
					.fireproof(plant.fireproof)
					.products(plant.products.toArray(new AgriProduct[0]))
					.clips(plant.clipProducts.toArray(new AgriProduct[0]))
					.requirement(plant.requirement)
					.modifiers(plant.modifierInfos.toArray(new AgriPlantModifierInfo[0]))
					.particles(plant.particleEffects.toArray(new AgriParticleEffect[0]));
		}

		public AgriPlant build() {
			return new AgriPlant(mods, seeds, stages, harvestStage, growthChance, growthBonus, cloneable, fireproof, spreadChance, seedDropChance, seedDropBonus, products, clipProducts, requirement, modifiers, particleEffects);
		}

		public Builder mods(String... mods) {
			Collections.addAll(this.mods, mods);
			return this;
		}

		public Builder seeds(AgriSeed... seeds) {
			Collections.addAll(this.seeds, seeds);
			return this;
		}

		public Builder clearSeeds() {
			this.seeds.clear();
			return this;
		}

		public Builder stages(Integer... stages) {
			this.stages.clear();
			Collections.addAll(this.stages, stages);
			return this;
		}

		public Builder stages16() {
			this.stages.clear();
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

		public Builder seedChances(double drop, double dropBonus) {
			this.seedDropChance = drop;
			this.seedDropBonus = dropBonus;
			return this;
		}

		public Builder cloneable(boolean cloneable) {
			this.cloneable = cloneable;
			return this;
		}

		public Builder fireproof(boolean fireproof) {
			this.fireproof = fireproof;
			return this;
		}

		public Builder products(AgriProduct... products) {
			Collections.addAll(this.products, products);
			return this;
		}

		public Builder clearProducts() {
			this.products.clear();
			return this;
		}

		public Builder clips(AgriProduct... clips) {
			Collections.addAll(this.clipProducts, clips);
			return this;
		}

		public Builder clearClips() {
			this.clipProducts.clear();
			return this;
		}

		public Builder requirement(AgriRequirement requirement) {
			this.requirement = requirement;
			return this;
		}

		public Builder modifiers(AgriPlantModifierInfo... modifiers) {
			Collections.addAll(this.modifiers, modifiers);
			return this;
		}

		public Builder clearModifiers() {
			this.modifiers.clear();
			return this;
		}

		public Builder particles(AgriParticleEffect... particles) {
			Collections.addAll(this.particleEffects, particles);
			return this;
		}

		public Builder clearParticles() {
			this.particleEffects.clear();
			return this;
		}

	}

}
