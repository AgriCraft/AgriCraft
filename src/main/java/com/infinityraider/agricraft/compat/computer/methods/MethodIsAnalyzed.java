package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.ArrayList;
import java.util.Optional;

public class MethodIsAnalyzed extends MethodBase {

    public MethodIsAnalyzed() {
        super("isAnalyzed", false, true, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws InvocationException {
        return new Object[]{Optional.ofNullable(crop.getSeed()).map(s -> s.getStat()).filter(IAgriStat::isAnalyzed).isPresent()};
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws InvocationException {
        return new Object[]{peripheral.isSpecimenAnalyzed()};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return null;
    }
}
