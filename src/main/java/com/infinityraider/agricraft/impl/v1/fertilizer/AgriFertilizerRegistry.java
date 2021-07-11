package com.infinityraider.agricraft.impl.v1.fertilizer;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizerProvider;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizerRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistryAbstract;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class AgriFertilizerRegistry extends AgriRegistryAbstract<IAgriFertilizer> implements IAgriFertilizerRegistry {
    public static final IAgriFertilizer NO_FERTILIZER = NoFertilizer.getInstance();

    private static final AgriFertilizerRegistry INSTANCE = new AgriFertilizerRegistry();
    private static final IAgriFertilizerProvider EMPTY = (item) -> Optional.empty();
    private final Map<Item, IAgriFertilizerProvider> providers;

    private AgriFertilizerRegistry() {
        super();
        this.providers = Maps.newHashMap();
        this.directAdd(NO_FERTILIZER);
    }

    public static AgriFertilizerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean remove(@Nullable IAgriFertilizer element) {
        // do not allow removal of the default no fertilizer implementation
        return NO_FERTILIZER != element && super.remove(element);
    }

    @Nonnull
    @Override
    public Stream<IAgriFertilizer> stream() {
        // Filter the No fertilizer out of the stream
        return super.stream().filter(IAgriFertilizer::isFertilizer);
    }

    @Nullable
    @Override
    protected AgriRegistryEvent<IAgriFertilizer> createEvent(IAgriFertilizer element) {
        return new AgriRegistryEvent.Fertilizer(this, element);
    }

    @Override
    public void registerFertilizerProvider(@Nonnull Item item, @Nonnull IAgriFertilizerProvider fertilizerProvider) {
        this.providers.put(item, fertilizerProvider);
    }

    @Nonnull
    @Override
    public IAgriFertilizerProvider getProvider(@Nonnull Item item) {
        return providers.getOrDefault(item, EMPTY);
    }

    @Override
    public IAgriFertilizer getNoFertilizer() {
        return NO_FERTILIZER;
    }
}
