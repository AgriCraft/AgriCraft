/*
 * A intermediate for the water channel blocks.
 */
package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.api.v1.misc.IAgriConnectable;
import com.infinityraider.agricraft.api.v1.util.AgriSideMetaMatrix;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class AbstractBlockWaterChannel<T extends TileEntityChannel> extends BlockCustomWood<T> {

    public AbstractBlockWaterChannel(String subtype) {
        super("water_channel_" + subtype);
        this.setTickRandomly(false);
    }

    @Override
    protected void addUnlistedProperties(Consumer<IUnlistedProperty> consumer) {
        super.addUnlistedProperties(consumer);
        AgriSideMetaMatrix.addUnlistedProperties(consumer);
    }

    @Override
    protected IExtendedBlockState getExtendedCustomWoodState(IExtendedBlockState state, Optional<T> tile) {
        return tile.map(IAgriConnectable::getConnections).orElseGet(AgriSideMetaMatrix::new).writeToBlockState(state);
    }
    
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // Call supermethod.
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        // Update tile.
        WorldHelper.getTile(world, pos, IAgriConnectable.class)
                .ifPresent(IAgriConnectable::refreshConnections);
        // Update neighbors.
        WorldHelper.getTileNeighbors(world, pos, IAgriConnectable.class)
                .forEach(IAgriConnectable::refreshConnections);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        WorldHelper.getTile(world, pos, IAgriConnectable.class)
                .ifPresent(IAgriConnectable::refreshConnections);
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
