package com.infinityraider.agricraft.compatibility.computercraft.methods;

import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import com.infinityraider.agricraft.tileentity.TileEntityPeripheral;

import static com.infinityraider.agricraft.compatibility.computercraft.methods.MethodUtilities.*;

import java.util.ArrayList;

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

    protected abstract Object[] onMethodCalled(CropPlant plant) throws MethodException;

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
}
