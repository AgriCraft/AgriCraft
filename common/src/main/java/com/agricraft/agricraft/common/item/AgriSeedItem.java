package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AgriSeedItem extends Item {

	public AgriSeedItem(Properties properties) {
		super(properties);
	}
	public static ItemStack toStack(String machineId) {
		return toStack(machineId, 1);
	}

	public static ItemStack toStack(String seedId, int amount) {
		ItemStack stack = new ItemStack(ModItems.SEED.get(), amount);
		CompoundTag tag = new CompoundTag();
		tag.putString("seed", seedId);
		stack.setTag(tag);
		return stack;
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.getTag()==null) {
			return Component.empty();
		}
		return Component.translatable("seed.agricraft." + stack.getTag().getString("seed").replace(":", "."));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		return super.use(level, player, usedHand);
	}

}
