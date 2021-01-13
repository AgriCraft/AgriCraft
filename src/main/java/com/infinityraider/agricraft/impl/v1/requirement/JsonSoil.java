package com.infinityraider.agricraft.impl.v1.requirement;

import com.agricraft.agricore.plant.AgriSoil;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import net.minecraft.block.BlockState;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;

/**
 * Class wrapping the AgriCore AgriSoil.
 */
public class JsonSoil implements IAgriSoil {
    @Nonnull
    private final AgriSoil soil;
    @Nonnull
    private final Collection<BlockState> variants;

    @SuppressWarnings("unchecked")
    public JsonSoil(@Nonnull AgriSoil soil) {
        this.soil = Preconditions.checkNotNull(soil);
        this.variants = Collections.unmodifiableCollection(Preconditions.checkNotNull(this.soil.getVariants(BlockState.class)));
    }

    @Override
    @Nonnull
    public String getId() {
        return this.soil.getId();
    }

    @Override
    @Nonnull
    public String getName() {
        return this.soil.getName();
    }

    @Override
    @Nonnull
    public Collection<BlockState> getVariants() {
        return this.variants;
    }

}
