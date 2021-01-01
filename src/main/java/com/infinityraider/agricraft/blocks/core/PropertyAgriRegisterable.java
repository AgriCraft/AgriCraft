package com.infinityraider.agricraft.blocks.core;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import net.minecraft.state.Property;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PropertyAgriRegisterable<T extends IAgriRegisterable<T>> extends Property<T> {
    private final IAgriRegistry<T> registry;

    private PropertyAgriRegisterable(String name, IAgriRegistry<T> registry) {
        super(name, registry.clazz());
        this.registry = registry;
    }

    private PropertyAgriRegisterable(IAgriRegistry<T> registry) {
        this(registry.name(), registry);
    }

    @Override
    public Collection<T> getAllowedValues() {
        return this.registry.all();
    }

    @Override
    public String getName(IAgriRegisterable value) {
        return value.getId();
    }

    @Override
    public Optional<T> parseValue(String value) {
        return this.registry.get(value);
    }

    public static PropertyAgriRegisterable<IAgriPlant> getPlantProperty() {
        return getOrCreate((p) -> Properties.plant = p,
                () -> Properties.plant,
                () -> new PropertyAgriRegisterable<>(AgriApi.getPlantRegistry()));
    }

    public static PropertyAgriRegisterable<IAgriWeed> getWeedProperty() {
        return getOrCreate((p) -> Properties.weed = p,
                () -> Properties.weed,
                () -> new PropertyAgriRegisterable<>(AgriApi.getWeedRegistry()));
    }

    public static PropertyAgriRegisterable<IAgriGrowthStage> getGrowthProperty() {
        return getOrCreate((p) -> Properties.growth = p,
                () -> Properties.growth,
                () -> new PropertyAgriRegisterable<>(AgriApi.getGrowthStageRegistry()));
    }

    public static PropertyAgriRegisterable<IAgriGrowthStage> getWeedGrowthProperty() {
        return getOrCreate((p) -> Properties.weedGrowth = p,
                () -> Properties.weedGrowth,
                () -> new PropertyAgriRegisterable<>("weed_growth", AgriApi.getGrowthStageRegistry()));
    }

    private static <T extends IAgriRegisterable<T>> PropertyAgriRegisterable<T> getOrCreate(
            Consumer<PropertyAgriRegisterable<T>> setter, Supplier<PropertyAgriRegisterable<T>> getter, Supplier<PropertyAgriRegisterable<T>> creator) {
        if(getter.get() == null) {
            setter.accept(creator.get());
        }
        return getter.get();
    }

    private static final class Properties {
        private static PropertyAgriRegisterable<IAgriPlant> plant;
        private static PropertyAgriRegisterable<IAgriGrowthStage> growth;
        private static PropertyAgriRegisterable<IAgriWeed> weed;
        private static PropertyAgriRegisterable<IAgriGrowthStage> weedGrowth;
    }
}
