package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.tiles.peripheral.TileEntityPeripheral;

import java.util.ArrayList;
import com.infinityraider.agricraft.api.v3.core.IAgriStat;

public class MethodGetStats extends MethodBase {
	
    public MethodGetStats() {
        super("getSpecimenStats", false, true, true);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        if(!crop.hasPlant() || !crop.getStats().isAnalyzed()) {
            return null;
        }
        IAgriStat stats = crop.getStats();
        return new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        IAgriStat stats = new PlantStats(peripheral.getSpecimen());
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
