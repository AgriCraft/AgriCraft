/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.compat.vanilla.BonemealWrapper;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;

/**
 *
 * 
 */
public class FertilizerRegistry {

	private static final IAgriAdapterRegistry<IAgriFertilizer> INSTANCE = new AdapterRegistry<>();

	static {
		INSTANCE.registerAdapter(BonemealWrapper.INSTANCE);
	}

	public static IAgriAdapterRegistry<IAgriFertilizer> getInstance() {
		return INSTANCE;
	}

}
