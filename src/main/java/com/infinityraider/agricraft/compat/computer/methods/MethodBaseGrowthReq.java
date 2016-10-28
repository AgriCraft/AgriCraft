package com.infinityraider.agricraft.compat.computer.methods;

import static com.infinityraider.agricraft.compat.computer.methods.MethodUtilities.*;

import java.util.ArrayList;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;

public abstract class MethodBaseGrowthReq extends MethodBase {

    public MethodBaseGrowthReq(String name) {
        super(name, true, true, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        return onMethodCalled(getCropPlant(crop));
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        return onMethodCalled(getCropPlant(peripheral.getSpecimen()));
    }

    protected abstract Object[] onMethodCalled(IAgriPlant plant) throws MethodException;

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
}
