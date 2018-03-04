package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import java.util.ArrayList;

public class MethodHasJournal extends MethodBasePeripheral {

    public MethodHasJournal() {
        super("hasJournal");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws InvocationException {
        return new Object[]{peripheral.getJournal() != null};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<>();
    }
}
