package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.apiimpl.v1.PlantStats;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetStatsFromCrop extends MethodCropBase {
    public MethodGetStatsFromCrop() {
        super("getStatsFromCrop");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        if(!crop.hasPlant() || !crop.getStats().isAnalyzed) {
            return null;
        }
        PlantStats stats = crop.getStats();
        return new Object[] {stats.growth, stats.gain, stats.strength};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
