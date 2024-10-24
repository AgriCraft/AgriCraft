package com.agricraft.agricraft.compat.botania;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.block.HornHarvestable;

public class AgriHornHarvestable implements HornHarvestable {

	@Override
	public boolean canHornHarvest(Level level, BlockPos pos, ItemStack stack, EnumHornType hornType, @Nullable LivingEntity user) {
		return hornType == EnumHornType.WILD && AgriApi.getCrop(level, pos).map(AgriCrop::canBeHarvested).orElse(false);
	}

	@Override
	public boolean hasSpecialHornHarvest(Level level, BlockPos pos, ItemStack stack, EnumHornType hornType, @Nullable LivingEntity user) {
		// this is always called after canHornHarvest returns true, so we are certain this is for an agricraft crop
		return true;
	}

	@Override
	public void harvestByHorn(Level level, BlockPos pos, ItemStack stack, EnumHornType hornType, @Nullable LivingEntity user) {
		if (hornType == EnumHornType.WILD) {
			AgriApi.getCrop(level, pos).ifPresent(crop -> crop.harvest(drop -> this.spawnEntity(level, pos, drop), null));
		}
	}

	private void spawnEntity(Level world, BlockPos pos, ItemStack stack) {
		double x = pos.getX() + 0.5 + 0.25 * world.getRandom().nextDouble();
		double y = pos.getY() + 0.5 + 0.25 * world.getRandom().nextDouble();
		double z = pos.getZ() + 0.5 + 0.25 * world.getRandom().nextDouble();
		world.addFreshEntity(new ItemEntity(world, x, y, z, stack));
	}

}
