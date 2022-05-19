package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.*;
import com.infinityraider.agricraft.content.irrigation.*;
import com.infinityraider.agricraft.content.world.greenhouse.BlockGreenHouseAir;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public final class AgriBlockRegistry extends ModContentRegistry implements IAgriContent.Blocks {
    private static final AgriBlockRegistry INSTANCE = new AgriBlockRegistry();

    public static AgriBlockRegistry getInstance() {
        return INSTANCE;
    }

    // crop
    public final RegistryInitializer<BlockCrop> crop;

    // analyzer
    public final RegistryInitializer<BlockSeedAnalyzer> seed_analyzer;

    // irrigation
    public final RegistryInitializer<BlockIrrigationTank> irrigation_tank;
    public final RegistryInitializer<BlockIrrigationChannelNormal> irrigation_channel;
    public final RegistryInitializer<BlockIrrigationChannelHollow> irrigation_channel_hollow;
    public final RegistryInitializer<BlockSprinkler> sprinkler;

    // Storage
    //public final RegistryInitializer<BlockBase> SEED_STORAGE;

    // Decoration
    public final RegistryInitializer<BlockGrate> grate;

    // World
    public final RegistryInitializer<BlockGreenHouseAir> greenhouse_air;

    private AgriBlockRegistry() {
        super();

        this.crop = this.block(BlockCrop::new);

        this.seed_analyzer = this.block(BlockSeedAnalyzer::new);

        this.irrigation_tank = this.block(BlockIrrigationTank::new);
        this.irrigation_channel = this.block(BlockIrrigationChannelNormal::new);
        this.irrigation_channel_hollow = this.block(BlockIrrigationChannelHollow::new);
        this.sprinkler = this.block(BlockSprinkler::new);

        this.grate = this.block(BlockGrate::new);

        this.greenhouse_air = this.block(BlockGreenHouseAir::new);
    }

    @Override
    public BlockCrop getCropBlock() {
        return this.crop.get();
    }

    @Override
    public BlockSeedAnalyzer getSeedAnalyzerBlock() {
        return this.seed_analyzer.get();
    }

    @Override
    public BlockIrrigationTank getTankBlock() {
        return this.irrigation_tank.get();
    }

    @Override
    public BlockIrrigationChannelNormal getChannelBlock() {
        return this.irrigation_channel.get();
    }

    @Override
    public BlockIrrigationChannelHollow getHollowChannelBlock() {
        return this.irrigation_channel_hollow.get();
    }

    @Override
    public BlockSprinkler getSprinklerBlock() {
        return this.sprinkler.get();
    }

    @Override
    public BlockGrate getGrateBlock() {
        return this.grate.get();
    }

    @Override
    public BlockGreenHouseAir getGreenHouseAirBlock() {
        return this.greenhouse_air.get();
    }
}
