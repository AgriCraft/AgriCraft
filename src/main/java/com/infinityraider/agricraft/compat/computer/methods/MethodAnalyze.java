package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import java.util.ArrayList;

public class MethodAnalyze extends MethodBasePeripheral {

    public MethodAnalyze() {
        super("analyze");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws InvocationException {
        peripheral.startAnalyzing();
        return new Object[0];
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<>();
    }

}
