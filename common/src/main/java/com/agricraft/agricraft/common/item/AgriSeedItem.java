package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AgriSeedItem extends BlockItem {

	public AgriSeedItem(Properties properties) {
		super(ModBlocks.CROP.get(), properties);
	}
	public static ItemStack toStack(ResourceLocation plantId) {
		return toStack(plantId, 1);
	}

	public static ItemStack toStack(ResourceLocation plantId, int amount) {
		ItemStack stack = new ItemStack(ModItems.SEED.get(), amount);
		CompoundTag tag = new CompoundTag();
		tag.putString("seed", plantId.toString());
		stack.setTag(tag);
		return stack;
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.getTag()==null) {
			return Component.translatable("seed.agricraft.agricraft.unknown");
		}
		return Component.translatable("seed.agricraft." + stack.getTag().getString("seed").replace(":", "."));
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		super.place(context);
		Level level = context.getLevel();
		if (!level.isClientSide) {
			System.out.println(level.getBlockState(context.getClickedPos()));
			BlockEntity be = level.getBlockEntity(context.getClickedPos());
			System.out.println(be);
			if (be instanceof CropBlockEntity cbe) {
				CompoundTag tag = context.getItemInHand().getTag();
				if (tag != null) {
					cbe.setSeed(tag.getString("seed"));
				}
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

}
