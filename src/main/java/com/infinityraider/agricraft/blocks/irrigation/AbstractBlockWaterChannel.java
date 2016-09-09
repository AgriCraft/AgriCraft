/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.blocks.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.utility.AgriWorldHelper;
import com.infinityraider.agricraft.utility.WorldHelper;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

	public AbstractBlockWaterChannel(String internalName) {
		super("water_channel_" + internalName);
	}

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[]{
            AgriProperties.CHANNEL_NORTH,
            AgriProperties.CHANNEL_EAST,
            AgriProperties.CHANNEL_SOUTH,
            AgriProperties.CHANNEL_WEST
        };
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntityChannel> tile = AgriWorldHelper.getTile(worldIn, pos, TileEntityChannel.class);
        if(tile.isPresent()) {
            TileEntityChannel channel = tile.get();
            state = AgriProperties.CHANNEL_NORTH.applyToBlockState(state, channel.hasNeighbourCheck(EnumFacing.NORTH));
            state = AgriProperties.CHANNEL_EAST.applyToBlockState(state, channel.hasNeighbourCheck(EnumFacing.EAST));
            state = AgriProperties.CHANNEL_SOUTH.applyToBlockState(state, channel.hasNeighbourCheck(EnumFacing.SOUTH));
            state = AgriProperties.CHANNEL_WEST.applyToBlockState(state, channel.hasNeighbourCheck(EnumFacing.WEST));
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
	
}
