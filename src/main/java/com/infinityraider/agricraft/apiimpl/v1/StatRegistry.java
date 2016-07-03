/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

/**
 *
 * @author RlonRyan
 */
public class StatRegistry {

	public static IAgriAdapterRegistry<IAgriStat> getInstance() {
		return APIimplv1.getInstance().getStatRegistry();
	}

}
