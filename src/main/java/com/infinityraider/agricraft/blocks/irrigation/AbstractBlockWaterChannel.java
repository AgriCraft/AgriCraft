/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks.irrigation;

import java.util.Optional;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.utility.AgriWorldHelper;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;

public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {
    @SuppressWarnings("unchecked")
    public static final InfinityProperty<Boolean>[] CONNECTION_PROPERTIES = new InfinityProperty[] {
            AgriProperties.CHANNEL_SOUTH,
            AgriProperties.CHANNEL_WEST,
            AgriProperties.CHANNEL_NORTH,
            AgriProperties.CHANNEL_EAST
    };

    public AbstractBlockWaterChannel(String internalName) {
        super("water_channel_" + internalName);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return CONNECTION_PROPERTIES;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntityChannel> tile = AgriWorldHelper.getTile(worldIn, pos, TileEntityChannel.class);
        if(tile.isPresent()) {
            TileEntityChannel channel = tile.get();
            for(EnumFacing facing : EnumFacing.HORIZONTALS) {
                state = CONNECTION_PROPERTIES[facing.getHorizontalIndex()].applyToBlockState(state, channel.hasNeighbourCheck(facing));
            }
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

}
