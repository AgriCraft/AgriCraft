package com.infinityraider.agricraft.tiles.irrigation;

public class TileEntityChannelFull extends TileEntityChannel {

    // Not much to do here...
    public TileEntityChannelFull() {
        this(CHANNEL_FLUID_CAPACITY, CHANNEL_FLUID_HEIGHT_MIN, CHANNEL_FLUID_HEIGHT_MAX, CHANNEL_FLUID_SYNC_THRESHOLD, CHANNEL_FLUID_SYNC_TIMEOUT);
    }

    public TileEntityChannelFull(int fluidCapacity, int fluidHeightMin, int fluidHeightMax, int fluidSyncThreshold, long fluidSyncTimeout) {
        super(fluidCapacity, fluidHeightMin, fluidHeightMax, fluidSyncThreshold, fluidSyncTimeout);
    }

}
