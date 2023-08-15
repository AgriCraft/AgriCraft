package com.agricraft.agricraft.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class RakeItem extends Item {



	public RakeItem(Properties properties) {
		super(properties);
	}

	@NotNull
	@Override
	public InteractionResult useOn(@NotNull UseOnContext context) {
		return InteractionResult.SUCCESS;
	}

}
