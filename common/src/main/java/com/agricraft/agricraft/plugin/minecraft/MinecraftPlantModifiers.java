package com.agricraft.agricraft.plugin.minecraft;

import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.plant.IAgriPlantModifier;
import com.agricraft.agricraft.api.plant.AgriPlantModifierFactoryRegistry;
import com.agricraft.agricraft.api.genetic.AgriGenePair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MinecraftPlantModifiers {

	public static void register() {
		AgriPlantModifierFactoryRegistry.register(BrightnessPlantModifier.ID, info -> Optional.of(new BrightnessPlantModifier()));
		AgriPlantModifierFactoryRegistry.register(BurnPlantModifier.ID, info -> Optional.of(new BurnPlantModifier()));
		AgriPlantModifierFactoryRegistry.register(BushyPlantModifier.ID, info -> Optional.of(new BushyPlantModifier()));
		AgriPlantModifierFactoryRegistry.register(ExperiencePlantModifier.ID, info -> Optional.of(new ExperiencePlantModifier()));
		AgriPlantModifierFactoryRegistry.register(FungusPlantModifier.ID, info -> {
			Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(info.value()));
			if (block instanceof FungusBlock fungus) {
				return Optional.of(new FungusPlantModifier(fungus));
			}
			return Optional.empty();
		});
		AgriPlantModifierFactoryRegistry.register(PoisonPlantModifier.ID, info -> Optional.of(new PoisonPlantModifier()));
		AgriPlantModifierFactoryRegistry.register(RedstonePlantModifier.ID, info -> Optional.of(new RedstonePlantModifier()));
		AgriPlantModifierFactoryRegistry.register(SummonPlantModifier.ID, info -> {
			if (BuiltInRegistries.ENTITY_TYPE.containsKey(new ResourceLocation(info.value()))) {
				return Optional.of(new SummonPlantModifier(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(info.value()))));
			}
			return Optional.empty();
		});
		AgriPlantModifierFactoryRegistry.register(ThornsPlantModifier.ID, info -> Optional.of(new ThornsPlantModifier()));
		AgriPlantModifierFactoryRegistry.register(TreePlantModifier.ID, info -> {
			Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(info.value()));
			if (block instanceof BonemealableBlock sapling) {
				return Optional.of(new TreePlantModifier(sapling));
			}
			return Optional.empty();
		});
		AgriPlantModifierFactoryRegistry.register(WitherPlantModifier.ID, info -> Optional.of(new WitherPlantModifier()));
	}

	public static class BrightnessPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:brightness";

		@Override
		public int getBrightness(AgriCrop crop) {
			return (int) (16 * crop.getGrowthPercent());
		}

	}

	public static class BurnPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:burn";

		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			entity.setSecondsOnFire(((int) crop.getGenome().getStatGenes().stream().map(AgriGenePair::getTrait).mapToInt(i -> i).average().orElse(0.0)));
		}

	}

	public static class BushyPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:bushy";

		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			entity.makeStuckInBlock(crop.getBlockState(), new Vec3(0.8, 0.75, 0.8));
		}

	}

	public static class ExperiencePlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:experience";

		@Override
		public void onHarvest(AgriCrop crop, @Nullable LivingEntity entity) {
			if (crop.getLevel() != null && !crop.getLevel().isClientSide) {
				for (int i = 0; i < crop.getGenome().getGain(); i++) {
					if (i == 0 || crop.getLevel().getRandom().nextDouble() < 0.5) {
						BlockPos pos = crop.getBlockPos();
						crop.getLevel().addFreshEntity(new ExperienceOrb(crop.getLevel(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4));
					}
				}
			}
		}

	}

	public static class FungusPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:fungus";
		private final FungusBlock fungus;

		public FungusPlantModifier(FungusBlock fungus) {
			this.fungus = fungus;
		}

		@Override
		public Optional<InteractionResult> onRightClickPre(AgriCrop crop, @NotNull ItemStack stack, @Nullable Entity entity) {
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
			return Optional.of(InteractionResult.SUCCESS);
		}

	}

	public static class PoisonPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:poison";

		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			if (entity instanceof LivingEntity livingEntity && !entity.isDiscrete() && !entity.level().isClientSide) {
				if (!livingEntity.hasEffect(MobEffects.POISON)) {
					livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, (int) (20 * crop.getGenome().getStatGenes().stream().map(AgriGenePair::getTrait).mapToInt(i -> i).average().orElse(0.0))));
				}
			}
		}

	}

	public static class RedstonePlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:redstone";

		@Override
		public int getBrightness(AgriCrop crop) {
			return (int) (15 * crop.getGrowthPercent());
		}

	}

	public static class SummonPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:summon";
		private final EntityType<?> entityType;

		public SummonPlantModifier(EntityType<?> entityType) {
			this.entityType = entityType;
		}

		@Override
		public void onHarvest(AgriCrop crop, @Nullable LivingEntity entity) {
			if (entity instanceof Player player && crop.getLevel() instanceof ServerLevel serverLevel) {
				if (entityType.spawn(serverLevel, null, player, crop.getBlockPos(), MobSpawnType.MOB_SUMMONED, true, false) != null) {
					serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, crop.getBlockPos());
				}
			}
		}

	}

	public static class ThornsPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:thorns";

		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			if (entity instanceof ItemEntity itemEntity && itemEntity.getAge() < 100) {
				return;
			}
			double damage = crop.getGrowthPercent() * crop.getGenome().getStatGenes().stream().map(AgriGenePair::getTrait).mapToInt(i -> i).average().orElse(0.0);
			entity.hurt(crop.getLevel().damageSources().cactus(), (float) damage);
		}

	}

	public static class TreePlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:tree";
		private final BonemealableBlock sapling;

		public TreePlantModifier(BonemealableBlock sapling) {
			this.sapling = sapling;
		}

		@Override
		public Optional<InteractionResult> onRightClickPre(AgriCrop crop, @NotNull ItemStack stack, @Nullable Entity entity) {
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
			CompoundTag before = crop.asBlockEntity().saveWithoutMetadata();
			sapling.performBonemeal(serverLevel, serverLevel.getRandom(), crop.getBlockPos(), state);
			if (serverLevel.getBlockState(crop.getBlockPos()).getBlock().equals(sapling)) {
				// if we couldn't grow the tree, put back the crop instead of the sapling
				serverLevel.setBlockAndUpdate(crop.getBlockPos(), crop.getBlockState());
				serverLevel.getBlockEntity(crop.getBlockPos()).load(before);
				return Optional.of(InteractionResult.FAIL);
			}
			serverLevel.levelEvent(2005, crop.getBlockPos(), 0);

			return Optional.of(InteractionResult.SUCCESS);
		}

	}

	public static class WitherPlantModifier implements IAgriPlantModifier {

		public static final String ID = "agricraft:wither";

		@Override
		public void onEntityCollision(AgriCrop crop, Entity entity) {
			if (entity instanceof LivingEntity) {
				MobEffectInstance wither = new MobEffectInstance(MobEffects.WITHER, (int) (10 * crop.getGenome().getStatGenes().stream().map(AgriGenePair::getTrait).mapToInt(i -> i).average().orElse(0.0)));
				((LivingEntity) entity).addEffect(wither);
			}
		}

	}

}
