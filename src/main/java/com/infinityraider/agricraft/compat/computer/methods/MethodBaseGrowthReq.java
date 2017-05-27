package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import static com.infinityraider.agricraft.compat.computer.methods.MethodUtilities.*;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import java.util.ArrayList;
import java.util.Optional;

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

    protected abstract Object[] onMethodCalled(Optional<IAgriPlant> plant) throws MethodException;

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
}
