/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.stat.IAgriStat;

/**
 *
 *
 */
public class StatRegistry {

    private static final IAgriAdapterRegistry<IAgriStat> INSTANCE = new AdapterRegistry<>();

    public static IAgriAdapterRegistry<IAgriStat> getInstance() {
        return INSTANCE;
    }

}
