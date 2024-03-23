package com.agricraft.agricraft.common.item.neoforge;

import com.agricraft.agricraft.client.bewlr.AgriSeedBEWLR;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * Wrapper for the AgriSeedItem for Forge specific method
 */
public class NeoForgeAgriSeedItem extends AgriSeedItem {

	public NeoForgeAgriSeedItem(Properties properties) {
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
