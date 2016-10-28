/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.compat.vanilla.BonemealWrapper;

/**
 *
 *
 */
public class FertilizerRegistry {

    private static final IAgriAdapterRegistry<IAgriFertilizer> INSTANCE = new AdapterRegistry<>();

    static {
        INSTANCE.registerAdapter(new BonemealWrapper());
    }

    public static IAgriAdapterRegistry<IAgriFertilizer> getInstance() {
        return INSTANCE;
    }

}
