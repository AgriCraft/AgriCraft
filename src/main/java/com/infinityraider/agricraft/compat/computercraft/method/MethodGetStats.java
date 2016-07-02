package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.tiles.peripheral.TileEntityPeripheral;

import java.util.ArrayList;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.v1.StatRegistry;
import com.infinityraider.agricraft.utility.StackHelper;

public class MethodGetStats extends MethodBase {
	
    public MethodGetStats() {
        super("getSpecimenStats", false, true, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        if(!crop.hasPlant() || !crop.getStat().isAnalyzed()) {
            return null;
        }
        IAgriStat stats = crop.getStat();
        return new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        IAgriStat stats = StatRegistry.getInstance().getStat(StackHelper.getTag(peripheral.getSpecimen()));
        return stats == null ? null : new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
	
}
