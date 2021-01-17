package com.infinityraider.agricraft.impl.v1.plant;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AgriPlantRegistry extends AgriRegistry<IAgriPlant> {
    public static final IAgriPlant NO_PLANT = NoPlant.getInstance();

    private static final AgriPlantRegistry INSTANCE = new AgriPlantRegistry();

    public static AgriPlantRegistry getInstance() {
        return INSTANCE;
    }

    private AgriPlantRegistry() {
        super();
        // Register no plant
        this.add(NO_PLANT);
    }

    @Override
    public boolean add(@Nullable IAgriPlant object) {
        boolean result = super.add(object);
        // also register the plant's growth stages
        if(object != null && result) {
            object.getGrowthStages().forEach(stage -> AgriGrowthRegistry.getInstance().add(stage));
        }
        return result;
    }

    @Override
    public boolean remove(@Nullable IAgriPlant element) {
        // do not allow removal of the default no plant implementation
        return NO_PLANT != element && super.remove(element);
    }

    @Nonnull
    @Override
    public Stream<IAgriPlant> stream() {
        // Filter the No plant out of the stream
        return super.stream().filter(IAgriPlant::isPlant);
    }
}
