package com.InfinityRaider.AgriCraft.reference;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;

import java.util.Arrays;
import java.util.Collection;

public class BlockStates {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    public static final IProperty<Integer> AGE2 = new IProperty<Integer>() {
        @Override
        public String getName() {
            return "AGE";
        }

        @Override
        public Collection<Integer> getAllowedValues() {
            return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        }

        @Override
        public Class<Integer> getValueClass() {
            return Integer.class;
        }

        @Override
        public String getName(Integer value) {
            return getName();
        }
    };
}
