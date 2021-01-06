package com.infinityraider.agricraft.impl.v1.crop;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

import javax.annotation.Nullable;

public class AgriGrowthRegistry extends AgriRegistry<IAgriGrowthStage> {
    public static final IAgriGrowthStage NO_GROWTH = NoGrowth.getInstance();

    private static final AgriGrowthRegistry INSTANCE = new AgriGrowthRegistry();

    public static AgriGrowthRegistry getInstance() {
        return INSTANCE;
    }

    public AgriGrowthRegistry() {
        super();
        // register no growth
        this.add(NO_GROWTH);
    }

    @Override
    public boolean remove(@Nullable IAgriGrowthStage element) {
        // do not allow removal of the default no growth implementation
        return NO_GROWTH != element && super.remove(element);
    }
}
