package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.core.*;
import com.infinityraider.agricraft.content.decoration.*;
import com.infinityraider.agricraft.content.irrigation.*;
import com.infinityraider.agricraft.content.world.*;

public final class AgriBlockRegistry {
    public static final IAgriContent.Blocks ACCESSOR = new Accessor();

    // crop plant
    public static final BlockCropPlant CROP_PLANT = new BlockCropPlant();

    // crop sticks
    public static final BlockCropSticks CROP_STICKS_WOOD = new BlockCropSticks(CropStickVariant.WOOD);
    public static final BlockCropSticks CROP_STICKS_IRON = new BlockCropSticks(CropStickVariant.IRON);
    public static final BlockCropSticks CROP_STICKS_OBSIDIAN = new BlockCropSticks(CropStickVariant.OBSIDIAN);

    // analyzer
    public static final BlockSeedAnalyzer SEED_ANALYZER = new BlockSeedAnalyzer();

    // irrigation
    public static final BlockIrrigationTank TANK = new BlockIrrigationTank();
    public static final BlockIrrigationChannelNormal CHANNEL = new BlockIrrigationChannelNormal();
    public static final BlockIrrigationChannelHollow CHANNEL_HOLLOW = new BlockIrrigationChannelHollow();
    public static final BlockSprinkler SPRINKLER = new BlockSprinkler();

    // Storage
    //public static final BlockBase SEED_STORAGE;

    // Decoration
    public static final BlockGrate GRATE = new BlockGrate();

    // World
    public static final BlockGreenHouseAir GREENHOUSE_AIR = new BlockGreenHouseAir();

    private static final class Accessor implements IAgriContent.Blocks {
        private Accessor() {}

        @Override
        public BlockCropPlant getCropPlantBlock() {
            return CROP_PLANT;
        }

        @Override
        public BlockCropSticks getWoodCropSticksBlock() {
            return CROP_STICKS_WOOD;
        }

        @Override
        public BlockCropSticks getIronCropSticksBlock() {
            return CROP_STICKS_IRON;
        }

        @Override
        public BlockCropSticks getObsidianCropSticksBlock() {
            return CROP_STICKS_OBSIDIAN;
        }

        @Override
        public BlockSeedAnalyzer getSeedAnalyzerBlock() {
            return SEED_ANALYZER;
        }

        @Override
        public BlockIrrigationTank getTankBlock() {
            return TANK;
        }

        @Override
        public BlockIrrigationChannelNormal getChannelBlock() {
            return CHANNEL;
        }

        @Override
        public BlockIrrigationChannelHollow getHollowChannelBlock() {
            return CHANNEL_HOLLOW;
        }

        @Override
        public BlockSprinkler getSprinklerBlock() {
            return SPRINKLER;
        }

        @Override
        public BlockGrate getGrateBlock() {
            return GRATE;
        }

        @Override
        public BlockGreenHouseAir getGreenHouseAirBlock() {
            return GREENHOUSE_AIR;
        }
    }
}
