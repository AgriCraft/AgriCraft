package com.infinityraider.agricraft.compat.computer.methods;

import java.util.ArrayList;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

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
