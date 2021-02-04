package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
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

    private AgriTileRegistry() {
        this.crop_sticks = InfinityTileEntityType.builder(Names.Blocks.CROP_STICKS, TileEntityCropSticks::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().crop_sticks_wood)
                .build();
        this.seed_analyzer = InfinityTileEntityType.builder(Names.Blocks.SEED_ANALYZER, TileEntitySeedAnalyzer::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().seed_analyzer)
                .build();
    }
}
