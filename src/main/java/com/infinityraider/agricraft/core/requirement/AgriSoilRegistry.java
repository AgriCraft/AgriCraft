package com.infinityraider.agricraft.core.requirement;

import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import net.minecraft.block.BlockState;

public class AgriSoilRegistry extends AgriRegistry<IAgriSoil> implements IAgriSoilRegistry {
    private static final AgriSoilRegistry INSTANCE = new AgriSoilRegistry();

    public static final AgriSoilRegistry getInstance() {
        return INSTANCE;
    }

    private AgriSoilRegistry() {
        super("soil", IAgriSoil.class);
    }

    @Override
    public boolean contains(@Nullable BlockState state) {
        if(state == null) {
            return false;
        }
        return this.stream().anyMatch(soil -> soil.isVariant(state));
    }

    @Nonnull
    @Override
    public Optional<IAgriSoil> get(@Nullable BlockState state) {
        if(state == null) {
            return Optional.empty();
        }
        return this.stream().filter(soil -> soil.isVariant(state)).findAny();
    }

}
