package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.tiles.TileEntityBase;
import net.minecraft.block.material.Material;

public abstract class BlockBaseNoTile extends BlockBase<TileEntityBase> {

    protected BlockBaseNoTile(Material mat, String internalName) {
        super(mat, internalName);
    }
}
