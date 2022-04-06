package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.content.core.TileEntityCropPlant;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.content.decoration.TileEntityGrate;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
import com.infinityraider.agricraft.content.irrigation.TileEntitySprinkler;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;

public class AgriTileRegistry {
    private static final AgriTileRegistry INSTANCE = new AgriTileRegistry();

    public static AgriTileRegistry getInstance() {
        return INSTANCE;
    }

    public static final InfinityTileEntityType<TileEntityCropPlant> CROP_PLANT = InfinityTileEntityType.builder(Names.Blocks.CROP_PLANT, TileEntityCropPlant::new)
            .addBlock(AgriApi.getAgriContent().getBlocks().getCropPlantBlock())
            .build();

    public static final InfinityTileEntityType<TileEntityCropSticks> CROP_STICKS = InfinityTileEntityType.builder(Names.Blocks.CROP_STICKS, TileEntityCropSticks::new)
            .addBlocks(
                    AgriApi.getAgriContent().getBlocks().getWoodCropSticksBlock(),
                    AgriApi.getAgriContent().getBlocks().getIronCropSticksBlock(),
                    AgriApi.getAgriContent().getBlocks().getObsidianCropSticksBlock())
            .build();

    public static final InfinityTileEntityType<TileEntitySeedAnalyzer> SEED_ANALYZER = InfinityTileEntityType.builder(Names.Blocks.SEED_ANALYZER, TileEntitySeedAnalyzer::new)
            .addBlock(AgriApi.getAgriContent().getBlocks().getSeedAnalyzerBlock())
            .setRenderFactory(TileEntitySeedAnalyzer.createRenderFactory())
            .build();

    public static final InfinityTileEntityType<TileEntityIrrigationTank> IRRIGATION_TANK = InfinityTileEntityType.builder(Names.Blocks.TANK, TileEntityIrrigationTank::new)
            .addBlock(AgriApi.getAgriContent().getBlocks().getTankBlock())
            .setTicking()
            .setRenderFactory(TileEntityIrrigationTank.createRenderFactory())
            .build();

    public static final InfinityTileEntityType<TileEntityIrrigationChannel> IRRIGATION_CHANNEL = InfinityTileEntityType.builder(Names.Blocks.CHANNEL, TileEntityIrrigationChannel::new)
            .addBlocks(
                    AgriApi.getAgriContent().getBlocks().getChannelBlock(),
                    AgriApi.getAgriContent().getBlocks().getHollowChannelBlock()
            )
            .setTicking()
            .setRenderFactory(TileEntityIrrigationChannel.createRenderFactory())
            .build();

    public static final InfinityTileEntityType<TileEntitySprinkler> SPRINKLER = InfinityTileEntityType.builder(Names.Blocks.SPRINKLER, TileEntitySprinkler::new)
            .addBlock(AgriApi.getAgriContent().getBlocks().getSprinklerBlock())
            .setRenderFactory(TileEntitySprinkler.createRenderFactory())
            .setTicking()
            .build();

    public static final InfinityTileEntityType<TileEntityGrate> GRATE = InfinityTileEntityType.builder(Names.Blocks.GRATE, TileEntityGrate::new)
            .addBlock(AgriApi.getAgriContent().getBlocks().getGrateBlock())
            .build();
}
