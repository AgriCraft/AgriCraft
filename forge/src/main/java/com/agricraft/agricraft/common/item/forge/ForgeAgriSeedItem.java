package com.agricraft.agricraft.common.item.forge;

import com.agricraft.agricraft.client.bewlr.AgriSeedBEWLR;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * Wrapper for the AgriSeedItem for Forge specific method
 */
public class ForgeAgriSeedItem extends AgriSeedItem {

	public ForgeAgriSeedItem(Properties properties) {
		super(properties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return AgriSeedBEWLR.INSTANCE;
			}
		});

	}

}
