package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import java.util.ArrayList;

import net.minecraft.util.StatCollector;

public abstract class MethodBase implements IMethod {
    @Override
    public String getDescription() {
        return StatCollector.translateToLocal("agricraft_method."+this.getName());
    }

    @Override
    public String signature() {
        StringBuilder signature = new StringBuilder(this.getName() + "(");
        boolean separator = false;
        for(MethodParameter parameter:getParameters()) {
            if(separator) {
                signature.append(", ");
            } else {
                separator = true;
            }
            signature.append(parameter.getName());
        }
        signature.append(")");
        return signature.toString();
    }

    protected abstract ArrayList<MethodParameter> getParameters();
}
