/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.plant.AgriSoil;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Class wrapping the AgriCore AgriSoil.
 */
public class JsonSoil implements IAgriSoil {

    private final AgriSoil soil;
    private final List<FuzzyStack> varients;

    public JsonSoil(AgriSoil soil) {
        this.soil = soil;
        this.varients = this.soil.getVarients(FuzzyStack.class);
    }

    @Override
    public String getId() {
        return this.soil.getId();
    }

    @Override
    public String getName() {
        return this.soil.getName();
    }

    @Override
    public Collection<FuzzyStack> getVarients() {
        return Collections.unmodifiableCollection(this.varients);
    }

}
