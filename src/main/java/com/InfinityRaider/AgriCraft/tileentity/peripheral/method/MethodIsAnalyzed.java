package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;

import java.util.ArrayList;

public class MethodIsAnalyzed extends MethodBase {
    public MethodIsAnalyzed() {
        super("isAnalyzed");
    }

    @Override
    protected boolean appliesToCrop() {
        return true;
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        return new Object[] {crop.isAnalyzed()};
    }

    @Override
    protected boolean appliesToPeripheral() {
        return true;
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        return new Object[] {peripheral.isSpecimenAnalyzed()};
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
