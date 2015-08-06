package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetStatsFromCrop extends MethodCropBase {
    public MethodGetStatsFromCrop() {
        super("getStatsFromCrop");
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
    protected boolean requiresJournal() {
        return false;
    }
}
