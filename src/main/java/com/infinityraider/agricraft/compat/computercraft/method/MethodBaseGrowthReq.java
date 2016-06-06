package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.tiles.peripheral.TileEntityPeripheral;
import static com.infinityraider.agricraft.compat.computercraft.method.MethodUtilities.*;

import java.util.ArrayList;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

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

    protected abstract Object[] onMethodCalled(IAgriCraftPlant plant) throws MethodException;

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
}
