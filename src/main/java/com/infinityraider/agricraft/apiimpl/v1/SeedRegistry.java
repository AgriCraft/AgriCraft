/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.seed.ISeedHandler;
import com.infinityraider.agricraft.api.v1.seed.ISeedRegistry;
import com.infinityraider.agricraft.api.v1.util.ItemWithMeta;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public class SeedRegistry implements ISeedRegistry {

	private final Map<ItemWithMeta, ISeedHandler> handlers;

	public SeedRegistry() {
		this.handlers = new HashMap<>();
	}
	
	public static ISeedRegistry getInstance() {
		return APIimplv1.getInstance().getSeedRegistry();
	}

	@Override
	public boolean isHandled(ItemStack stack) {
		return handlers.containsKey(new ItemWithMeta(stack));
	}

	@Override
	public boolean addSeedHandler(ItemStack stack, ISeedHandler handler) {
		handlers.put(new ItemWithMeta(stack), handler);
		return true;
	}

	@Override
	public boolean removeSeedHandler(ItemStack stack) {
		return handlers.remove(new ItemWithMeta(stack)) != null;
	}

	@Override
	public ISeedHandler getSeedHandler(ItemStack stack) {
		return stack == null ? null : handlers.get(new ItemWithMeta(stack));
	}

}
