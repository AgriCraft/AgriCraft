/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.stat.IAgriStat;

/**
 *
 * @author RlonRyan
 */
public class StatRegistry {

	public static IAgriAdapterRegistry<IAgriStat> getInstance() {
		return AgriApiImpl.getInstance().getStatRegistry();
	}

}
