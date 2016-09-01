/*
 */
package com.infinityraider.agricraft.core;

import com.agricraft.agricore.plant.AgriSoil;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;

/**
 * Class wrapping the AgriCore AgriSoil.
 */
public class JsonSoil implements IAgriSoil {
    
    private final AgriSoil soil;
    private List<ItemStack> varients;

    public JsonSoil(AgriSoil soil) {
        this.soil = soil;
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
    public Collection<ItemStack> getVarients() {
        if(this.varients == null) {
            this.varients = this.soil.getVarients().stream()
                    .filter(s -> s instanceof ItemStack)
                    .map(s -> (ItemStack)s)
                    .collect(Collectors.toList());
        }
        return this.varients;
    }
    
}
