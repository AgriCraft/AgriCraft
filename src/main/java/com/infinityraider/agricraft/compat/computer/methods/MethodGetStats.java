package com.infinityraider.agricraft.compat.computer.methods;

import java.util.ArrayList;

import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.apiimpl.StatRegistry;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import com.infinityraider.agricraft.compat.computer.tiles.TileEntityPeripheral;
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
        IAgriStat stats = StatRegistry.getInstance().valueOf(StackHelper.getTag(peripheral.getSpecimen())).orElse(null);
        return stats == null ? null : new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        ArrayList<MethodParameter> pars = new ArrayList<>();
        pars.add(MethodParameter.DIRECTION_OPTIONAL);
        return pars;
    }

}
