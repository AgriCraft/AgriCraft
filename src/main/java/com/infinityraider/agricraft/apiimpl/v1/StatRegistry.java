/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.handler.IAgriHandlerRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

/**
 *
 * @author RlonRyan
 */
public class StatRegistry {

	public static IAgriHandlerRegistry<IAgriStat> getInstance() {
		return APIimplv1.getInstance().getStatRegistry();
	}

}
