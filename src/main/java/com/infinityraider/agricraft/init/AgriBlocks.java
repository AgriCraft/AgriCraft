package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.blocks.BlockGrate;
import com.infinityraider.agricraft.blocks.BlockSeedAnalyzer;
import com.infinityraider.agricraft.blocks.BlockPeripheral;
import com.infinityraider.agricraft.blocks.BlockWaterPad;
import com.infinityraider.agricraft.blocks.irrigation.BlockSprinkler;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannel;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelFull;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterChannelValve;
import com.infinityraider.agricraft.blocks.irrigation.BlockWaterTank;
import com.infinityraider.infinitylib.block.BlockBase;
import javax.annotation.Nonnull;

public class AgriBlocks {

    private static final AgriBlocks INSTANCE = new AgriBlocks();

    public static AgriBlocks getInstance() {
        return INSTANCE;
    }

    private AgriBlocks() {
        CROP = new BlockCrop();
        SEED_ANALYZER = new BlockSeedAnalyzer();
        PERIPHERAL = new BlockPeripheral();
        WATER_PAD = new BlockWaterPad();
        TANK = new BlockWaterTank();
        CHANNEL = new BlockWaterChannel();
        CHANNEL_FULL = new BlockWaterChannelFull();
        CHANNEL_VALVE = new BlockWaterChannelValve();
        SPRINKLER = new BlockSprinkler();
        //SEED_STORAGE = new BlockSeedStorage();
        GRATE = new BlockGrate();
    }

    // Crops
    @Nonnull
    public final BlockBase CROP;

    // Analyzers
    @Nonnull
    public final BlockBase SEED_ANALYZER;
    
    // Peripherals
    @Nonnull
    public final BlockBase PERIPHERAL;

    // Water Pads
    @Nonnull
    public final BlockBase WATER_PAD;

    // Irrigation
    @Nonnull
    public final BlockBase TANK;
    @Nonnull
    public final BlockBase CHANNEL;
    @Nonnull
    public final BlockBase CHANNEL_FULL;
    @Nonnull
    public final BlockBase CHANNEL_VALVE;
    @Nonnull
    public final BlockBase SPRINKLER;

    // Seed Storage
    //public final BlockBase SEED_STORAGE;
    //public static final BlockBase SEED_STORAGE_CONTROLLER = new BlockSeedStorageController();
    // Decoration
    @Nonnull
    public final BlockBase GRATE;

}
