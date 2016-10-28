/*
 */
package com.infinityraider.agricraft.reference;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 *
 */
public class PropertyString implements IUnlistedProperty<String> {

    final String name;

    public PropertyString(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isValid(String value) {
        return true;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }



}
