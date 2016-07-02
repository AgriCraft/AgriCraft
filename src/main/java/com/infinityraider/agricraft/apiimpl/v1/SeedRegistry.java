/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.handler.IAgriHandlerRegistry;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;

/**
 *
 * @author RlonRyan
 */
public class SeedRegistry {
	
	public static IAgriHandlerRegistry<AgriSeed> getInstance() {
		return APIimplv1.getInstance().getSeedRegistry();
	}

}
