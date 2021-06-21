package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.*;
import com.infinityraider.agricraft.content.irrigation.*;
import com.infinityraider.agricraft.content.world.*;

public class AgriBlockRegistry implements IAgriContent.Blocks {

    private static final AgriBlockRegistry INSTANCE = new AgriBlockRegistry();

    public static AgriBlockRegistry getInstance() {
        return INSTANCE;
    }

    // crop plant
    public final BlockCropPlant crop_plant;

    // crop sticks
    public final BlockCropSticks crop_sticks_wood;
    public final BlockCropSticks crop_sticks_iron;
    public final BlockCropSticks crop_sticks_obsidian;

    // analyzer
    public final BlockSeedAnalyzer seed_analyzer;

    // irrigation
    public final BlockIrrigationTank tank;
    public final BlockIrrigationChannelNormal channel;
    public final BlockIrrigationChannelHollow channel_hollow;
    public final BlockSprinkler sprinkler;

    // Storage
    //public final BlockBase seed_storage;

    // Decoration
    public final BlockGrate grate;

    // World
    public final BlockGreenHouseAir greenhouse_air;

    private AgriBlockRegistry() {
        this.crop_plant = new BlockCropPlant();
        this.crop_sticks_wood = new BlockCropSticks(CropStickVariant.WOOD);
        this.crop_sticks_iron = new BlockCropSticks(CropStickVariant.IRON);
        this.crop_sticks_obsidian = new BlockCropSticks(CropStickVariant.OBSIDIAN);

        this.seed_analyzer = new BlockSeedAnalyzer();

        this.tank = new BlockIrrigationTank();
        this.channel = new BlockIrrigationChannelNormal();
        this.channel_hollow = new BlockIrrigationChannelHollow();
        this.sprinkler = new BlockSprinkler();

        //this.seed_storage = new BlockSeedStorage();

        this.grate = new BlockGrate();

        this.greenhouse_air = new BlockGreenHouseAir();
    }

    @Override
    public BlockCropPlant getCropPlantBlock() {
        return this.crop_plant;
    }

    @Override
    public BlockCropSticks getWoodCropSticksBlock() {
        return this.crop_sticks_wood;
    }

    @Override
    public BlockCropSticks getIronCropSticksBlock() {
        return this.crop_sticks_iron;
    }

    @Override
    public BlockCropSticks getObsidianCropSticksBlock() {
        return this.crop_sticks_obsidian;
    }

    @Override
    public BlockSeedAnalyzer getSeedAnalyzerBlock() {
        return this.seed_analyzer;
    }

    @Override
    public BlockIrrigationTank getTankBlock() {
        return this.tank;
    }

    @Override
    public BlockIrrigationChannelNormal getChannelBlock() {
        return this.channel;
    }

    @Override
    public BlockIrrigationChannelHollow getHollowChannelBlock() {
        return this.channel_hollow;
    }

    @Override
    public BlockSprinkler getSprinklerBlock() {
        return this.sprinkler;
    }

    @Override
    public BlockGrate getGrateBlock() {
        return this.grate;
    }

    @Override
    public BlockGreenHouseAir getGreenHouseAirBlock() {
        return this.greenhouse_air;
    }
}
