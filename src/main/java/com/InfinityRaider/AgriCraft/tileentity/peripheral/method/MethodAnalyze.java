package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;

import java.util.ArrayList;

public class MethodAnalyze extends MethodBasePeripheral {
    public MethodAnalyze() {
        super("analyze");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        peripheral.startAnalyzing();
        return new Object[0];
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<MethodParameter>();
    }
}
