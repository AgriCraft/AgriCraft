package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.content.decoration.TileEntityGrate;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.content.core.TileEntityCropSticks;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import net.minecraft.tileentity.TileEntityType;

public class AgriTileRegistry {
    private static final AgriTileRegistry INSTANCE = new AgriTileRegistry();

    public static AgriTileRegistry getInstance() {
        return INSTANCE;
    }

    public final TileEntityType<TileEntityCropSticks> crop_sticks;
    public final TileEntityType<TileEntitySeedAnalyzer> seed_analyzer;
    public final TileEntityType<TileEntityIrrigationTank> irrigation_tank;
    public final TileEntityType<TileEntityIrrigationChannel> irrigation_channel;
    public final TileEntityType<TileEntityGrate> grate;

    private AgriTileRegistry() {
        this.crop_sticks = InfinityTileEntityType.builder(Names.Blocks.CROP_STICKS, TileEntityCropSticks::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().crop_sticks_wood)
                .build();

        this.seed_analyzer = InfinityTileEntityType.builder(Names.Blocks.SEED_ANALYZER, TileEntitySeedAnalyzer::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().seed_analyzer)
                .setRenderFactory(TileEntitySeedAnalyzer.createRenderFactory())
                .build();

        this.irrigation_tank = InfinityTileEntityType.builder(Names.Blocks.TANK, TileEntityIrrigationTank::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().tank)
                .setRenderFactory(TileEntityIrrigationTank.createRenderFactory())
                .build();

        this.irrigation_channel = InfinityTileEntityType.builder(Names.Blocks.CHANNEL, TileEntityIrrigationChannel::new)
                .addBlocks(
                        AgriCraft.instance.getModBlockRegistry().channel,
                        AgriCraft.instance.getModBlockRegistry().channel_hollow
                )
                .setRenderFactory(TileEntityIrrigationChannel.createRenderFactory())
                .build();

        this.grate = InfinityTileEntityType.builder(Names.Blocks.GRATE, TileEntityGrate::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().grate)
                .build();
    }
}
