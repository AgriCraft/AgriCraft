package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockReader;

import java.util.function.BiFunction;

public class BlockSprinkler extends BlockBaseTile<TileEntitySprinkler> {
    public BlockSprinkler(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return null;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntitySprinkler> getTileEntityFactory() {
        return null;
    }
}
