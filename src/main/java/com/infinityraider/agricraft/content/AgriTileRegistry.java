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
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public class AgriTileRegistry extends ModContentRegistry {
    private static final AgriTileRegistry INSTANCE = new AgriTileRegistry();

    public static AgriTileRegistry getInstance() {
        return INSTANCE;
    }

    public final RegistryInitializer<InfinityTileEntityType<TileEntityCropPlant>> crop_plant;
    public final RegistryInitializer<InfinityTileEntityType<TileEntityCropSticks>> crop_sticks;
    public final RegistryInitializer<InfinityTileEntityType<TileEntitySeedAnalyzer>> seed_analyzer;
    public final RegistryInitializer<InfinityTileEntityType<TileEntityIrrigationTank>> irrigation_tank;
    public final RegistryInitializer<InfinityTileEntityType<TileEntityIrrigationChannel>> irrigation_channel;
    public final RegistryInitializer<InfinityTileEntityType<TileEntitySprinkler>> sprinkler;
    public final RegistryInitializer<InfinityTileEntityType<TileEntityGrate>> grate;

    private AgriTileRegistry() {
        super();

        this.crop_plant = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.CROP_PLANT, TileEntityCropPlant::new)
                        .addBlock(AgriApi.getAgriContent().getBlocks().getCropPlantBlock())
                        .build()
        );

        this.crop_sticks = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.CROP_STICKS, TileEntityCropSticks::new)
                        .addBlocks(
                                AgriApi.getAgriContent().getBlocks().getWoodCropSticksBlock(),
                                AgriApi.getAgriContent().getBlocks().getIronCropSticksBlock(),
                                AgriApi.getAgriContent().getBlocks().getObsidianCropSticksBlock())
                        .build()
        );

        this.seed_analyzer = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.SEED_ANALYZER, TileEntitySeedAnalyzer::new)
                        .addBlock(AgriApi.getAgriContent().getBlocks().getSeedAnalyzerBlock())
                        .setRenderFactory(TileEntitySeedAnalyzer.createRenderFactory())
                        .build()
        );

        this.irrigation_tank = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.TANK, TileEntityIrrigationTank::new)
                        .addBlock(AgriApi.getAgriContent().getBlocks().getTankBlock())
                        .setTicking()
                        .setRenderFactory(TileEntityIrrigationTank.createRenderFactory())
                        .build()
        );

        this.irrigation_channel = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.CHANNEL, TileEntityIrrigationChannel::new)
                        .addBlocks(
                                AgriApi.getAgriContent().getBlocks().getChannelBlock(),
                                AgriApi.getAgriContent().getBlocks().getHollowChannelBlock()
                        )
                        .setTicking()
                        .setRenderFactory(TileEntityIrrigationChannel.createRenderFactory())
                        .build()
        );

        this.sprinkler = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.SPRINKLER, TileEntitySprinkler::new)
                        .addBlock(AgriApi.getAgriContent().getBlocks().getSprinklerBlock())
                        .setRenderFactory(TileEntitySprinkler.createRenderFactory())
                        .setTicking()
                        .build()
        );

        this.grate = this.blockEntity(() ->
                InfinityTileEntityType.builder(Names.Blocks.GRATE, TileEntityGrate::new)
                        .addBlock(AgriApi.getAgriContent().getBlocks().getGrateBlock())
                        .build()
        );
    }
}
