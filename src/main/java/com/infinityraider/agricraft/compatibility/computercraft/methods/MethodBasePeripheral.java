package com.infinityraider.agricraft.compatibility.computercraft.methods;

import com.infinityraider.agricraft.tileentity.TileEntityCrop;

import java.util.ArrayList;

public abstract class MethodBasePeripheral extends MethodBase {
    public MethodBasePeripheral(String name) {
        super(name, false, true, false);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        return new Object[0];
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<>();
    }
}
