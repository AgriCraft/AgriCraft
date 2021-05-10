package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilProvider;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import net.minecraft.block.Block;

import java.util.Map;
import java.util.Optional;

public class AgriSoilRegistry extends AgriRegistry<IAgriSoil> implements IAgriSoilRegistry {
    private static final AgriSoilRegistry INSTANCE = new AgriSoilRegistry();
    private static final IAgriSoilProvider EMPTY = (world, pos, state) -> Optional.empty();

    public static AgriSoilRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Block, IAgriSoilProvider> providers;

    private AgriSoilRegistry() {
        super();
        this.providers = Maps.newHashMap();
    }

    @Nullable
    @Override
    protected AgriRegistryEvent<IAgriSoil> createEvent(IAgriSoil element) {
        return new AgriRegistryEvent.Soil(this, element);
    }

    @Override
    public void registerSoilProvider(@Nonnull Block block, @Nonnull IAgriSoilProvider soilProvider) {
        this.providers.put(block, soilProvider);
    }

    @Nonnull
    @Override
    public IAgriSoilProvider getProvider(@Nonnull Block block) {
        return providers.getOrDefault(block, EMPTY);
    }
}
