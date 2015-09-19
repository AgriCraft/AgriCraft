package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import net.minecraft.util.StatCollector;

public class MethodParameter {
    public static final MethodParameter DIRECTION = new MethodParameter("direction");

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
