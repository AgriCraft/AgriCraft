package com.infinityraider.agricraft.impl.v1.requirement;

import com.agricraft.agricore.plant.AgriSoil;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import net.minecraft.block.BlockState;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Class wrapping the AgriCore AgriSoil.
 */
public class JsonSoil implements IAgriSoil {
    private static final Predicate<BlockState> NO_SOIL = (state) -> false;

    @Nonnull
    private final AgriSoil soil;
    @Nonnull
    private final List<Predicate<BlockState>> variants;

    @SuppressWarnings("unchecked")
    public JsonSoil(@Nonnull AgriSoil soil) {
        this.soil = Preconditions.checkNotNull(soil);
        this.variants = ImmutableList.copyOf(Preconditions.checkNotNull(this.soil.getVariants(Predicate.class)));
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
    public Collection<Predicate<BlockState>> getVariants() {
        return Collections.unmodifiableCollection(this.variants);
    }

}
