/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.util.ItemWithMeta;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedHandler;
import com.infinityraider.agricraft.api.v1.seed.IAgriSeedRegistry;

/**
 *
 * @author RlonRyan
 */
public class SeedRegistry implements IAgriSeedRegistry {

	private final Map<ItemWithMeta, IAgriSeedHandler> handlers;

	public SeedRegistry() {
		this.handlers = new HashMap<>();
	}
	
	public static IAgriSeedRegistry getInstance() {
		return APIimplv1.getInstance().getSeedRegistry();
	}

	@Override
	public boolean isHandled(ItemStack stack) {
		return handlers.containsKey(new ItemWithMeta(stack));
	}

	@Override
	public boolean addSeedHandler(ItemStack stack, IAgriSeedHandler handler) {
		handlers.put(new ItemWithMeta(stack), handler);
		return true;
	}

	@Override
	public boolean removeSeedHandler(ItemStack stack) {
		return handlers.remove(new ItemWithMeta(stack)) != null;
	}

	@Override
	public IAgriSeedHandler getSeedHandler(ItemStack stack) {
		return stack == null ? null : handlers.get(new ItemWithMeta(stack));
	}

}
