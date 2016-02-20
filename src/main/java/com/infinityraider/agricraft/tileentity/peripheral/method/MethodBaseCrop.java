package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.tileentity.peripheral.TileEntityPeripheral;

import java.util.ArrayList;

public abstract class MethodBaseCrop extends MethodBase {
	
    public MethodBaseCrop(String name) {
        super(name, false, false, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        return new Object[0];
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION);
        return pars;
    }
	
}
