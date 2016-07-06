/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.seed.AgriSeed;

/**
 *
 * @author RlonRyan
 */
public class SeedRegistry {
	
	public static IAgriAdapterRegistry<AgriSeed> getInstance() {
		return AgriApiImpl.getInstance().getSeedRegistry();
	}

}
