package com.infinityraider.agricraft.init;

import com.infinityraider.agricraft.AgriCraft;
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

    private AgriTileRegistry() {
        this.crop_sticks = InfinityTileEntityType.builder(Names.Blocks.CROP_STICKS, TileEntityCropSticks::new)
                .addBlock(AgriCraft.instance.getModBlockRegistry().crop_sticks_wood)
                .build();
    }
}
