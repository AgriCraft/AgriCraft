/*
 */
package com.infinityraider.agricraft.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 *
 */
public class UnlistedPropertyBoolean implements IUnlistedProperty<Boolean> {

    private final String name;

    public UnlistedPropertyBoolean(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Boolean v) {
        return true;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public String valueToString(Boolean b) {
        return String.valueOf(b);
    }

}
