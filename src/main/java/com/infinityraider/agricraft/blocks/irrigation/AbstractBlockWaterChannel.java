/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

    @SuppressWarnings("unchecked")
    public static final InfinityProperty<Boolean>[] CONNECTION_PROPERTIES = new InfinityProperty[]{
        AgriProperties.CHANNEL_SOUTH,
        AgriProperties.CHANNEL_WEST,
        AgriProperties.CHANNEL_NORTH,
        AgriProperties.CHANNEL_EAST
    };

    public AbstractBlockWaterChannel(String subtype) {
        super("water_channel_" + subtype);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return CONNECTION_PROPERTIES;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        final Optional<TileEntityChannel> tile = WorldHelper.getTile(world, pos, TileEntityChannel.class);
        if (tile.isPresent()) {
            TileEntityChannel channel = tile.get();
            channel.checkConnections();
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                state = CONNECTION_PROPERTIES[facing.getHorizontalIndex()].applyToBlockState(state, channel.hasNeighbor(facing));
            }
        }
        return state;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        WorldHelper.getTile(world, pos, TileEntityChannel.class)
                .ifPresent(TileEntityChannel::checkConnections);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
    
    @Override
	public boolean isEnabled() {
		return AgriCraftConfig.enableIrrigation;
	}

}
