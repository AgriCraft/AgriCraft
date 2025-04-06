package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.Chromosome;
import com.agricraft.agricraft.api.plant.AgriPlantModifierFactory;
import com.agricraft.agricraft.api.plant.IAgriPlantModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ModPlantModifiers {

	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> BRIGHTNESS = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("brightness", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public int getBrightness(AgriCrop crop) {
			return (int) (16 * crop.getGrowthPercent());
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> BURN = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("burn", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			entity.igniteForSeconds(((int) crop.getGenome().getStatChromosomes().stream().map(Chromosome::trait).mapToInt(i -> i).average().orElse(0.0)));
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> BUSHY = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("bushy", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			entity.makeStuckInBlock(crop.getBlockState(), new Vec3(0.8, 0.75, 0.8));
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> EXPERIENCE = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("experience", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public void onHarvest(AgriCrop crop, @Nullable LivingEntity entity) {
			if (crop.getLevel() != null && !crop.getLevel().isClientSide) {
				for (int i = 0; i < crop.getGenome().gain().trait(); i++) {
					if (i == 0 || crop.getLevel().getRandom().nextDouble() < 0.5) {
						BlockPos pos = crop.getBlockPos();
						crop.getLevel().addFreshEntity(new ExperienceOrb(crop.getLevel(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4));
					}
				}
			}
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> FUNGUS = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("fungus", () -> info -> {
		Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(info.value()));
		if (block instanceof FungusBlock fungus) {
			return Optional.of(new IAgriPlantModifier() {
				@Override
				public Optional<ItemInteractionResult> onRightClickPre(AgriCrop crop, @NotNull ItemStack stack, @Nullable Entity entity) {
					Level level = crop.getLevel();
					if (stack.isEmpty()
							|| stack.getItem() != Items.BONE_MEAL
							|| crop.getLevel() == null
							|| crop.canBeHarvested()
							|| !fungus.isValidBonemealTarget(level, crop.getBlockPos(), crop.getBlockState())
							|| !fungus.isBonemealSuccess(level, level.random, crop.getBlockPos(), crop.getBlockState())) {
						return Optional.empty();
					}
					fungus.performBonemeal(((ServerLevel) level), level.random, crop.getBlockPos(), crop.getBlockState());
					level.levelEvent(2005, crop.getBlockPos(), 0);
					return Optional.of(ItemInteractionResult.SUCCESS);
				}
			});
		}
		return Optional.empty();
	});
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> POISON = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("poison", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			if (entity instanceof LivingEntity livingEntity && !entity.isDiscrete() && !entity.level().isClientSide) {
				if (!livingEntity.hasEffect(MobEffects.POISON)) {
					livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, (int) (20 * crop.getGenome().getStatChromosomes().stream().map(Chromosome::trait).mapToInt(i -> i).average().orElse(0.0))));
				}
			}
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> REDSTONE = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("redstone", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public int getRedstonePower(AgriCrop crop) {
			return (int) (15 * crop.getGrowthPercent());
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> SUMMON = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("summon", () -> info -> {
		if (BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceLocation.parse(info.value()))) {
			EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(info.value()));
			return Optional.of(new IAgriPlantModifier() {
				@Override
				public void onHarvest(AgriCrop crop, @Nullable LivingEntity entity) {
					if (entity instanceof Player player && crop.getLevel() instanceof ServerLevel serverLevel) {
						if (entityType.spawn(serverLevel, null, player, crop.getBlockPos(), MobSpawnType.MOB_SUMMONED, true, false) != null) {
							serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, crop.getBlockPos());
						}
					}
				}
			});
		}
		return Optional.empty();
	});
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> THORNS = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("thorns", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			if (entity instanceof ItemEntity itemEntity && itemEntity.getAge() < 100) {
				return;
			}
			double damage = crop.getGrowthPercent() * crop.getGenome().getStatChromosomes().stream().map(Chromosome::trait).mapToInt(i -> i).average().orElse(0.0);
			entity.hurt(crop.getLevel().damageSources().cactus(), (float) damage);
		}
	}));
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> TREE = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("tree", () -> info -> {
		Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(info.value()));
		if (block instanceof BonemealableBlock sapling) {
			return Optional.of(new IAgriPlantModifier() {
				@Override
				public Optional<ItemInteractionResult> onRightClickPre(AgriCrop crop, @NotNull ItemStack stack, @Nullable Entity entity) {
					Level level = crop.getLevel();
					if (stack.isEmpty()
							|| stack.getItem() != Items.BONE_MEAL
							|| crop.getLevel() == null
							|| crop.canBeHarvested()
							|| !(level instanceof ServerLevel serverLevel)
							|| !sapling.isValidBonemealTarget(level, crop.getBlockPos(), crop.getBlockState())
							|| !sapling.isBonemealSuccess(level, level.random, crop.getBlockPos(), crop.getBlockState())) {
						return Optional.empty();
					}
					BlockState state = ((Block) sapling).defaultBlockState();
					if (state.hasProperty(SaplingBlock.STAGE)) { // for trees
						state = state.setValue(SaplingBlock.STAGE, 1);
					}
					CompoundTag before = crop.asBlockEntity().saveWithoutMetadata(level.registryAccess());
					sapling.performBonemeal(serverLevel, serverLevel.getRandom(), crop.getBlockPos(), state);
					if (serverLevel.getBlockState(crop.getBlockPos()).getBlock().equals(sapling)) {
						// if we couldn't grow the tree, put back the crop instead of the sapling
						serverLevel.setBlockAndUpdate(crop.getBlockPos(), crop.getBlockState());
						serverLevel.getBlockEntity(crop.getBlockPos()).loadWithComponents(before, level.registryAccess());
						return Optional.of(ItemInteractionResult.FAIL);
					}
					serverLevel.levelEvent(2005, crop.getBlockPos(), 0);
					return Optional.of(ItemInteractionResult.SUCCESS);
				}
			});
		}
		return Optional.empty();
	});
	DeferredHolder<AgriPlantModifierFactory, AgriPlantModifierFactory> WITHER = AgriRegistries.PLANT_MODIFIER_FACTORIES.register("wither", () -> info -> Optional.of(new IAgriPlantModifier() {
		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			if (entity instanceof LivingEntity) {
				MobEffectInstance wither = new MobEffectInstance(MobEffects.WITHER, (int) (10 * crop.getGenome().getStatChromosomes().stream().map(Chromosome::trait).mapToInt(i -> i).average().orElse(0.0)));
				((LivingEntity) entity).addEffect(wither);
			}
		}
	}));

	@ApiStatus.Internal
	static void register() {
	}

}
