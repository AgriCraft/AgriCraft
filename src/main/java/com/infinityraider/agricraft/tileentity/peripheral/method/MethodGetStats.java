package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.api.v1.ISeedStats;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import com.infinityraider.agricraft.tileentity.peripheral.TileEntityPeripheral;

import java.util.ArrayList;

public class MethodGetStats extends MethodBase {
	
    public MethodGetStats() {
        super("getSpecimenStats", false, true, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        if(!crop.hasPlant() || !crop.isAnalyzed()) {
            return null;
        }
        ISeedStats stats = crop.getStats();
        return new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        ISeedStats stats = PlantStats.getStatsFromStack(peripheral.getSpecimen());
        if(stats==null) {
            return null;
        }
        return new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
	
}
