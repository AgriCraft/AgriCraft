package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilProvider;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoilRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.impl.v1.AgriRegistryAbstract;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class AgriSoilRegistry extends AgriRegistryAbstract<IAgriSoil> implements IAgriSoilRegistry {
    public static final IAgriSoil NO_SOIL = NoSoil.getInstance();

    private static final AgriSoilRegistry INSTANCE = new AgriSoilRegistry();
    private static final IAgriSoilProvider EMPTY = (world, pos, state) -> Optional.empty();

    public static AgriSoilRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<Block, IAgriSoilProvider> providers;

    private AgriSoilRegistry() {
        super();
        this.providers = Maps.newHashMap();
        this.directAdd(NO_SOIL);
    }

    @Override
    public boolean remove(@Nullable IAgriSoil element) {
        // do not allow removal of the default no plant implementation
        return NO_SOIL != element && super.remove(element);
    }

    @Nonnull
    @Override
    public Stream<IAgriSoil> stream() {
        // Filter the No plant out of the stream
        return super.stream().filter(IAgriSoil::isSoil);
    }

    @Nullable
    @Override
    protected AgriRegistryEvent.Register<IAgriSoil> createEvent(IAgriSoil element) {
        return new AgriRegistryEvent.Register.Soil(this, element);
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

    @Override
    public IAgriSoil getNoSoil() {
        return NO_SOIL;
    }
}
