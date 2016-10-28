package com.infinityraider.agricraft.compat.computer.methods;

import java.util.ArrayList;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;

public class MethodIsAnalyzed extends MethodBase {

    public MethodIsAnalyzed() {
        super("isAnalyzed", false, true, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        return new Object[] {crop.getStat().isAnalyzed()};
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        return new Object[] {peripheral.isSpecimenAnalyzed()};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return null;
    }
}
