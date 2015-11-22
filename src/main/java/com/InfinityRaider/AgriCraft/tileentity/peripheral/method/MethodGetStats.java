package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.farming.PlantStats;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;

import java.util.ArrayList;

public class MethodGetStats extends MethodBase {
    public MethodGetStats() {
        super("getSpecimenStats");
    }

    @Override
    protected boolean appliesToCrop() {
        return true;
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
    protected boolean appliesToPeripheral() {
        return true;
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
    protected boolean requiresJournal() {
        return false;
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<MethodParameter>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }
}
