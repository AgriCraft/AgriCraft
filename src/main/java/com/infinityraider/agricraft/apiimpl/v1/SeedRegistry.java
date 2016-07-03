/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;

/**
 *
 * @author RlonRyan
 */
public class SeedRegistry {
	
	public static IAgriAdapterRegistry<AgriSeed> getInstance() {
		return APIimplv1.getInstance().getSeedRegistry();
	}

}
