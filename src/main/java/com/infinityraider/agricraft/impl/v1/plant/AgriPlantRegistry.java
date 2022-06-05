package com.infinityraider.agricraft.impl.v1.plant;

import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistryAbstract;
import com.infinityraider.agricraft.impl.v1.crop.AgriGrowthRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AgriPlantRegistry extends AgriRegistryAbstract<IAgriPlant> implements IAgriPlantRegistry {
    public static final IAgriPlant NO_PLANT = NoPlant.getInstance();

    private static final AgriPlantRegistry INSTANCE = new AgriPlantRegistry();

    public static AgriPlantRegistry getInstance() {
        return INSTANCE;
    }

    private AgriPlantRegistry() {
        super();
        // Register no plant
        this.directAdd(NO_PLANT);
    }

    @Override
    public boolean add(@Nullable IAgriPlant object) {
        if(object == null) {
            return false;
        }
        boolean result = super.add(object);
        // also register the plant's growth stages
        if(result) {
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

    @Nullable
    @Override
    protected AgriRegistryEvent.Register<IAgriPlant> createEvent(IAgriPlant element) {
        return new AgriRegistryEvent.Register.Plant(this, element);
    }

    @Override
    public IAgriPlant getNoPlant() {
        return NO_PLANT;
    }
}
