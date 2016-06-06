package com.infinityraider.agricraft.tiles.peripheral.method;


import net.minecraft.util.text.translation.I18n;

public class MethodParameter {
    public static final MethodParameter DIRECTION = new MethodParameter("direction");
    public static final MethodParameter DIRECTION_OPTIONAL = new MethodParameter("direction.optional");

    private String name;

    private MethodParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return I18n.translateToLocal("agricraft_arg." + name);
    }

    public String getDescription() {
        return I18n.translateToLocal("agricraft_description.parameter."+name);
    }
}
