package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import net.minecraft.util.StatCollector;

public class MethodParameter {
    public static final MethodParameter DIRECTION = new MethodParameter("direction");
    public static final MethodParameter DIRECTION_OPTIONAL = new MethodParameter("direction.optional");

    private String name;

    private MethodParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return StatCollector.translateToLocal("agricraft_arg."+name);
    }

    public String getDescription() {
        return StatCollector.translateToLocal("agricraft_description.parameter."+name);
    }
}
