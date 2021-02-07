package com.infinityraider.agricraft.impl.v1.plant;

import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeedRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AgriWeedRegistry extends AgriRegistry<IAgriWeed> implements IAgriWeedRegistry {
    public static final IAgriWeed NO_WEED = NoWeed.getInstance();

    private static final AgriWeedRegistry INSTANCE = new AgriWeedRegistry();

    public static AgriWeedRegistry getInstance() {
        return INSTANCE;
    }

    private AgriWeedRegistry() {
        super();
        // Register no weed
        this.directAdd(NO_WEED);
    }

    @Override
    public boolean add(@Nullable IAgriWeed object) {
        boolean result = super.add(object);
        // also register the weed's growth stages
        if(object != null && result) {
            object.getGrowthStages().forEach(stage -> AgriGrowthRegistry.getInstance().add(stage));
        }
        return result;
    }

    @Override
    public boolean remove(@Nullable IAgriWeed element) {
        // do not allow removal of the default no weed implementation
        return NO_WEED != element && super.remove(element);
    }

    @Nonnull
    @Override
    public Stream<IAgriWeed> stream() {
        // Filter the No weed out of the stream
        return super.stream().filter(IAgriWeed::isWeed);
    }

    @Nullable
    @Override
    protected AgriRegistryEvent<IAgriWeed> createEvent(IAgriWeed element) {
        return new AgriRegistryEvent.Weed(this, element);
    }

    @Override
    public IAgriWeed getNoWeed() {
        return NO_WEED;
    }
}
