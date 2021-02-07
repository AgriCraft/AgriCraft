package com.infinityraider.agricraft.impl.v1.crop;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AgriGrowthRegistry extends AgriRegistry<IAgriGrowthStage> implements IAgriGrowthRegistry {
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

    @Nonnull
    @Override
    public Stream<IAgriGrowthStage> stream() {
        // Filter the No growth out of the stream
        return super.stream().filter(IAgriGrowthStage::isGrowthStage);
    }

    @Override
    public IAgriGrowthStage getNoGrowth() {
        return NO_GROWTH;
    }
}
