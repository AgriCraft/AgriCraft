/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.api.irrigation.IrrigationConnection;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

    public AbstractBlockWaterChannel(String subtype) {
        super("water_channel_" + subtype);
    }

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return IrrigationConnection.CONNECTIONS;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        final Optional<TileEntityChannel> tile = WorldHelper.getTile(world, pos, TileEntityChannel.class);
        if (!tile.isPresent()) {
            return state;
        }
        tile.get().checkConnections();
        final IrrigationConnection sides = new IrrigationConnection();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            sides.set(facing, tile.get().getConnectionType(facing));
        }
        return sides.write(state);
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

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

}
