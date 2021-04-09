package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;

import javax.annotation.Nullable;

import com.infinityraider.agricraft.impl.v1.AgriRegistry;

public class AgriSoilRegistry extends AgriRegistry<IAgriSoil> implements IAgriSoilRegistry {
    private static final AgriSoilRegistry INSTANCE = new AgriSoilRegistry();

    public static AgriSoilRegistry getInstance() {
        return INSTANCE;
    }

    private AgriSoilRegistry() {
        super();
    }

    @Nullable
    @Override
    protected AgriRegistryEvent<IAgriSoil> createEvent(IAgriSoil element) {
        return new AgriRegistryEvent.Soil(this, element);
    }
}
