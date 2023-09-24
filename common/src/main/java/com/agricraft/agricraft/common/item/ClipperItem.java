package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ClipperItem extends Item {

	public ClipperItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (level.isClientSide) {
			return InteractionResult.PASS;
		}
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		return AgriApi.getCrop(level, pos).map(crop -> {
			if (!crop.allowsClipping()) {
				if (player != null) {
					player.sendSystemMessage(Component.translatable("agricraft.message.clipping_impossible"));
				}
				return InteractionResult.FAIL;
			}
			List<ItemStack> drops = new ArrayList<>();
			crop.getClippingProducts(drops::add);
			crop.setGrowthStage(0);
			for (ItemStack drop : drops) {
				level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
			}
			return InteractionResult.SUCCESS;
		}).orElse(InteractionResult.FAIL);
	}

}
