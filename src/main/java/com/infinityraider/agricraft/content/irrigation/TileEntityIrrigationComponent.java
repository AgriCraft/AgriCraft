package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntityType;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture {
    public TileEntityIrrigationComponent(TileEntityType<?> type) {
        super(type);
    }
}
