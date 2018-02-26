/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.plant.AgriSoil;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Class wrapping the AgriCore AgriSoil.
 */
public class JsonSoil implements IAgriSoil {

    @Nonnull
    private final AgriSoil soil;
    @Nonnull
    private final List<FuzzyStack> varients;

    public JsonSoil(@Nonnull AgriSoil soil) {
        this.soil = Preconditions.checkNotNull(soil);
        this.varients = Preconditions.checkNotNull(this.soil.getVarients(FuzzyStack.class));
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
    public Collection<FuzzyStack> getVarients() {
        return Collections.unmodifiableCollection(this.varients);
    }

}
