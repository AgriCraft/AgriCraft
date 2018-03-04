package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.ArrayList;

public abstract class MethodBasePeripheral extends MethodBase {

    public MethodBasePeripheral(String name) {
        super(name, false, true, false);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws InvocationException {
        return new Object[0];
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<>();
    }
}
