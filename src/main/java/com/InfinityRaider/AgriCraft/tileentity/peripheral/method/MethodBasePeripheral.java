package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

import java.util.ArrayList;

public abstract class MethodBasePeripheral extends MethodBase {
    public MethodBasePeripheral(String name) {
        super(name);
    }

    @Override
    protected boolean appliesToCrop() {
        return false;
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        return new Object[0];
    }

    @Override
    protected boolean appliesToPeripheral() {
        return true;
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<MethodParameter>();
    }
}
