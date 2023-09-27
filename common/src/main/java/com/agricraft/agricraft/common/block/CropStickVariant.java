package com.agricraft.agricraft.common.block;

import com.agricraft.agricraft.common.item.CropSticksItem;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public enum CropStickVariant implements StringRepresentable {
	WOODEN,
	IRON,
	OBSIDIAN;

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}

	public SoundType getSound() {
		return switch (this) {
			case WOODEN -> SoundType.WOOD;
			case IRON -> SoundType.ANVIL;
			case OBSIDIAN -> SoundType.BASALT;
		};
	}

	public void playSound(Level level, BlockPos pos) {
		level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.getSound().getPlaceSound(), SoundSource.BLOCKS, (this.getSound().getVolume() + 1.0F) / 4.0F, this.getSound().getPitch() * 0.8F);
	}

	public static CropStickVariant fromItem(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof CropSticksItem item)) {
			return null;
		}
		return item.getVariant();
	}

	public static ItemStack toItem(CropStickVariant variant) {
		return switch (variant) {
			case WOODEN -> new ItemStack(ModItems.WOODEN_CROP_STICKS.get());
			case IRON -> new ItemStack(ModItems.IRON_CROP_STICKS.get());
			case OBSIDIAN -> new ItemStack(ModItems.OBSIDIAN_CROP_STICKS.get());
		};
	}

	public static ItemStack toItem(CropStickVariant variant, int amount) {
		ItemStack stack = toItem(variant);
		stack.setCount(amount);
		return stack;
	}

}
