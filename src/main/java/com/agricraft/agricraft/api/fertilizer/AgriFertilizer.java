package com.agricraft.agricraft.api.fertilizer;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.TagUtils;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AgriFertilizer {

	public static final ResourceKey<Registry<AgriFertilizer>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AgriApi.MOD_ID, "fertilizer"));

	public static final Codec<AgriFertilizer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("mods").forGetter(agriFertilizer -> agriFertilizer.mods),
			AgriFertilizerVariant.CODEC.listOf().fieldOf("variants").forGetter(agriFertilizer -> agriFertilizer.variants),
			Codec.BOOL.fieldOf("trigger_mutation").forGetter(agriFertilizer -> agriFertilizer.triggerMutation),
			Codec.BOOL.fieldOf("trigger_weeds").forGetter(agriFertilizer -> agriFertilizer.triggerWeeds),
			Codec.INT.fieldOf("potency").forGetter(agriFertilizer -> agriFertilizer.potency),
			Codec.BOOL.fieldOf("reduce_growth").forGetter(agriFertilizer -> agriFertilizer.reduceGrowth),
			Codec.BOOL.fieldOf("kill_plant").forGetter(agriFertilizer -> agriFertilizer.killPlant),
			ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("neutral_on").forGetter(agriFertilizer -> agriFertilizer.neutralOn),
			ExtraCodecs.TAG_OR_ELEMENT_ID.listOf().fieldOf("negative_on").forGetter(agriFertilizer -> agriFertilizer.negativeOn),
			AgriFertilizerParticle.CODEC.listOf().fieldOf("particles").forGetter(agriFertilizer -> agriFertilizer.particles)
	).apply(instance, AgriFertilizer::new));

	private final List<String> mods;
	private final List<AgriFertilizerVariant> variants;
	private final boolean triggerMutation;
	private final boolean triggerWeeds;
	private final int potency;
	private final boolean reduceGrowth;
	private final boolean killPlant;
	private final List<ExtraCodecs.TagOrElementLocation> neutralOn;
	private final List<ExtraCodecs.TagOrElementLocation> negativeOn;
	private final List<AgriFertilizerParticle> particles;

	public AgriFertilizer(List<String> mods, List<AgriFertilizerVariant> variants, boolean triggerMutation,
	                      boolean triggerWeeds, int potency, boolean reduceGrowth, boolean killPlant, List<ExtraCodecs.TagOrElementLocation> neutralOn,
	                      List<ExtraCodecs.TagOrElementLocation> negativeOn, List<AgriFertilizerParticle> particles) {
		this.mods = mods;
		this.variants = variants;
		this.triggerMutation = triggerMutation;
		this.triggerWeeds = triggerWeeds;
		this.potency = potency;
		this.reduceGrowth = reduceGrowth;
		this.killPlant = killPlant;
		this.neutralOn = neutralOn;
		this.negativeOn = negativeOn;
		this.particles = particles;
	}

	public List<AgriFertilizerVariant> variants() {
		return this.variants;
	}

	/**
	 * Whether this fertilizer can be used on a cross crop to trigger a mutation (does not override configuration option).
	 *
	 * @return If the fertilizer can trigger a mutation event.
	 */
	public boolean canTriggerMutation() {
		return this.triggerMutation;
	}

	/**
	 * Whether this fertilizer can spawn weeds.
	 *
	 * @return If the fertilizer can trigger a mutation event.
	 */
	public boolean canTriggerWeeds() {
		return this.triggerWeeds;
	}

	public boolean canReduceGrowth() {
		return this.reduceGrowth;
	}

	public boolean canKillPlant() {
		return this.killPlant;
	}

	public boolean affects(ResourceLocation plantId) {
		return !isNeutralOn(plantId);
	}

	public boolean isNeutralOn(ResourceLocation plantId) {
		return neutralOn.stream().flatMap(tag -> TagUtils.plants(tag)).anyMatch(rl -> rl.equals(plantId));
	}

	public boolean isNegativeOn(ResourceLocation plantId) {
		return negativeOn.stream().flatMap(tag -> TagUtils.plants(tag)).anyMatch(rl -> rl.equals(plantId));
	}

	/**
	 * Retrieve the particles to spawn when applying the effect of the fertilizer on plant.
	 *
	 * @param type the type of particles, either "positive", "negative", or "neutral"
	 * @return the list of particles to spawn for the plant.
	 */
	public List<AgriFertilizerParticle> getParticles(String type) {
		return particles.stream().filter(particle -> particle.when().contains(type)).collect(Collectors.toList());

	}

	/**
	 * This is called when the fertilizer is used on a crop
	 *
	 * @param level        The world in which the fertilizable is placed
	 * @param pos          The position of the fertilizable in the world
	 * @param fertilizable The fertilizable object to which the fertilizer was applied.
	 * @param stack        The stack that the player was holding that triggered the fertilizer to be applied.
	 * @param random       A random for use in generating probabilities.
	 * @param entity       The entity applying the fertilizer (can be null if fertilized through automation)
	 * @return the result to handle the item use call chain
	 */
	public ItemInteractionResult applyFertilizer(Level level, BlockPos pos, IAgriFertilizable fertilizable, ItemStack stack, RandomSource random, @Nullable LivingEntity entity) {
		if (fertilizable instanceof AgriCrop crop) {
			String type = "neutral";
			for (int i = 0; i < this.potency; i++) {
				if (crop.hasPlant()) {
					if (this.isNegativeOn(crop.getPlantId())) {
						if (this.canReduceGrowth() && random.nextBoolean()) {
							type = "negative";
							if (!level.isClientSide()) {
								AgriGrowthStage current = crop.getGrowthStage();
								AgriGrowthStage previous = current.getPrevious(crop, random);
								if (current.equals(previous)) {
									if (this.canKillPlant()) {
										crop.removeGenome();
									}
								} else {
									crop.setGrowthStage(previous);
								}
							}
						}
					} else if (this.canFertilize(crop)) {
						fertilizable.applyGrowthTick();
						type = "positive";
					}
				} else if (crop.isCrossCropSticks() && this.canTriggerMutation()) {
					fertilizable.applyGrowthTick();
					type = "positive";
				} else if (this.canTriggerWeeds()) {
					fertilizable.applyGrowthTick();
					type = "positive";
				}
			}
			this.spawnParticles(level, pos, type, random);
			if ((entity instanceof Player) && !(((Player) entity).isCreative())) {
				stack.shrink(1);
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.FAIL;
	}

	protected void spawnParticles(Level world, BlockPos pos, String type, RandomSource rand) {
		this.getParticles(type)
				.forEach(effect -> {
					ParticleType<?> particle = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(effect.particle()));
					if (!(particle instanceof ParticleOptions)) {
						return;
					}
					for (int amount = 0; amount < effect.amount(); ++amount) {
						double x = pos.getX() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.deltaX() * rand.nextDouble();
						double y = pos.getY() + 0.5D + effect.deltaY() * rand.nextDouble();
						double z = pos.getZ() + 0.5D + (rand.nextBoolean() ? 1 : -1) * effect.deltaZ() * rand.nextDouble();
						world.addParticle((ParticleOptions) particle, x, y, z, 0.0D, 0.0D, 0.0D);
					}
				});
	}

	/**
	 * Check if the target can be fertilized by the fertilizer
	 *
	 * @param target the fertilizable object
	 * @return true if the target can be fertilized
	 */
	public boolean canFertilize(IAgriFertilizable target) {
		if (!(target instanceof AgriCrop crop)) {
			return false;
		}
		return crop.hasPlant() && this.affects(crop.getPlantId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (AgriFertilizer) obj;
		return Objects.equals(this.mods, that.mods) &&
				Objects.equals(this.variants, that.variants) &&
				this.triggerMutation == that.triggerMutation &&
				this.triggerWeeds == that.triggerWeeds &&
				this.potency == that.potency &&
				this.reduceGrowth == that.reduceGrowth &&
				this.killPlant == that.killPlant &&
				Objects.equals(this.neutralOn, that.neutralOn) &&
				Objects.equals(this.negativeOn, that.negativeOn) &&
				Objects.equals(this.particles, that.particles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mods, variants, triggerMutation, triggerWeeds, potency, reduceGrowth, killPlant, neutralOn, negativeOn, particles);
	}

	@Override
	public String toString() {
		return "AgriFertilizer[" +
				"mods=" + mods + ", " +
				"variants=" + variants + ", " +
				"triggerMutation=" + triggerMutation + ", " +
				"triggerWeeds=" + triggerWeeds + ", " +
				"potency=" + potency + ", " +
				"reduceGrowth=" + reduceGrowth + ", " +
				"killPlant=" + killPlant + ", " +
				"negativelyAffected=" + neutralOn + ", " +
				"positivelyAffected=" + negativeOn + ", " +
				"particles=" + particles + ']';
	}

	public static class Builder {

		private List<String> mods = new ArrayList<>();
		private List<AgriFertilizerVariant> variants = new ArrayList<>();
		private boolean triggerMutation = true;
		private boolean triggerWeeds = true;
		private int potency = 1;
		private boolean reduceGrowth = true;
		private boolean killPlant = true;
		private List<ExtraCodecs.TagOrElementLocation> neutralOn = new ArrayList<>();
		private List<ExtraCodecs.TagOrElementLocation> negativeOn = new ArrayList<>();
		private List<AgriFertilizerParticle> particles = new ArrayList<>();

		public AgriFertilizer build() {
			return new AgriFertilizer(mods, variants, triggerMutation, triggerWeeds, potency, reduceGrowth, killPlant, neutralOn, negativeOn, particles);
		}

		public Builder mods(String... mods) {
			Collections.addAll(this.mods, mods);
			return this;
		}

		public Builder variants(AgriFertilizerVariant... variants) {
			Collections.addAll(this.variants, variants);
			return this;
		}

		public Builder triggerMutation(boolean triggerMutation) {
			this.triggerMutation = triggerMutation;
			return this;
		}

		public Builder triggerWeeds(boolean triggerWeeds) {
			this.triggerWeeds = triggerWeeds;
			return this;
		}

		public Builder potency(int potency) {
			this.potency = potency;
			return this;
		}

		public Builder reduceGrowth(boolean reduceGrowth) {
			this.reduceGrowth = reduceGrowth;
			return this;
		}

		public Builder killPlant(boolean killPlant) {
			this.killPlant = killPlant;
			return this;
		}

		public Builder neutralOn(String... neutralOn) {
			for (String str : neutralOn) {
				this.neutralOn.add(new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(str), false));
			}
			return this;
		}
		public Builder neutralOnTag(String... neutralOn) {
			for (String str : neutralOn) {
				this.neutralOn.add(new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(str), true));
			}
			return this;
		}

		public Builder negativeOn(String... negativeOn) {
			for (String str : negativeOn) {
				this.negativeOn.add(new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(str), false));
			}
			return this;
		}
		public Builder negativeOnTag(String... negativeOn) {
			for (String str : negativeOn) {
				this.negativeOn.add(new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(str), true));
			}
			return this;
		}

		public Builder particles(AgriFertilizerParticle... particles) {
			Collections.addAll(this.particles, particles);
			return this;
		}

	}

}
