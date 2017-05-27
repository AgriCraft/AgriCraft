/*
 */
package com.infinityraider.agricraft.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 *
 */
public class UnlistedPropertyInteger implements IUnlistedProperty<Integer> {

    private final String name;

    public UnlistedPropertyInteger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Integer v) {
        return v >= 0;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String valueToString(Integer v) {
        return String.valueOf(v);
    }

}
