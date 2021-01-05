package com.infinityraider.agricraft.impl.v1.plant;

import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;

import javax.annotation.Nullable;

public class AgriWeedRegistry extends AgriRegistry<IAgriWeed> {
    public static final IAgriWeed NO_WEED = NoWeed.getInstance();

    private static final AgriWeedRegistry INSTANCE = new AgriWeedRegistry();

    public static AgriWeedRegistry getInstance() {
        return INSTANCE;
    }

    private AgriWeedRegistry() {
        super("weed", IAgriWeed.class);
        // Register no weed
        this.add(NO_WEED);
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
}
