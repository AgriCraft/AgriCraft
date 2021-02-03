package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import net.minecraft.block.BlockState;

public class AgriSoilRegistry extends AgriRegistry<IAgriSoil> implements IAgriSoilRegistry {
    private static final AgriSoilRegistry INSTANCE = new AgriSoilRegistry();

    public static AgriSoilRegistry getInstance() {
        return INSTANCE;
    }

    private AgriSoilRegistry() {
        super();
    }

    @Override
    public boolean contains(@Nullable BlockState state) {
        if(state == null) {
            return false;
        }
        return this.stream().anyMatch(soil -> soil.isVariant(state));
    }

    @Nonnull
    @Override
    public Collection<IAgriSoil> get(@Nullable BlockState state) {
        if(state == null) {
            return Collections.emptyList();
        }
        return this.stream().filter(soil -> soil.isVariant(state)).collect(Collectors.toList());
    }

    @Nullable
    @Override
    protected AgriRegistryEvent<IAgriSoil> createEvent(IAgriSoil element) {
        return new AgriRegistryEvent.Soil(this, element);
    }

}
