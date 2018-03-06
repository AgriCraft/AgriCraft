package com.infinityraider.agricraft.blocks.irrigation;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannel;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannel;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockWaterChannel extends AbstractBlockWaterChannel<TileEntityChannel> {

    protected static final float MIN = Constants.UNIT * Constants.QUARTER;
    protected static final float MAX = Constants.UNIT * Constants.THREE_QUARTER;

    protected static final double EXPANSION = 1 / 64d;

    public static final AxisAlignedBB CENTER_BOX = new AxisAlignedBB(MIN, MIN, MIN, MAX, MAX, MAX).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB NORTH_BOX = new AxisAlignedBB(MIN, MIN, 0, MAX, MAX, MIN + Constants.UNIT).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB EAST_BOX = new AxisAlignedBB(MAX - Constants.UNIT, MIN, MIN, Constants.UNIT * Constants.WHOLE, MAX, MAX).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB SOUTH_BOX = new AxisAlignedBB(MIN, MIN, MAX - Constants.UNIT, MAX, MAX, Constants.UNIT * Constants.WHOLE).expand(EXPANSION, EXPANSION, EXPANSION);
    public static final AxisAlignedBB WEST_BOX = new AxisAlignedBB(0, MIN, MIN, MIN + Constants.UNIT, MAX, MAX).expand(EXPANSION, EXPANSION, EXPANSION);

    private final BlockWaterChannelValve.ItemBlockValve itemBlock;

    public BlockWaterChannel() {
        super("normal");
        this.itemBlock = new BlockWaterChannelValve.ItemBlockValve(this);
    }

    @Override
    public Optional<BlockWaterChannelValve.ItemBlockValve> getItemBlock() {
        return Optional.of(this.itemBlock);
    }

    @Override
    public TileEntityChannel createNewTileEntity(World world, int meta) {
        return new TileEntityChannel();
    }

    /*
     * Adds all intersecting collision boxes to a list. (Be sure to only add
     * boxes to the list if they intersect the mask.) Parameters: World, pos,
     * mask, list, colliding entity
     */
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, @Nullable Entity entity, boolean isActualState) {
        // Add central box.
        addCollisionBoxToList(pos, mask, list, CENTER_BOX);

        // Fetch Tile Entity
        final TileEntityChannel tile = WorldHelper.getTile(world, pos, TileEntityChannel.class).orElse(null);

        // If null stop.
        if (tile == null) {
            return;
        }

        //adjacent boxes
        if (tile.getConnections().get(EnumFacing.NORTH) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, NORTH_BOX);
        }
        if (tile.getConnections().get(EnumFacing.EAST) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, EAST_BOX);
        }
        if (tile.getConnections().get(EnumFacing.SOUTH) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, SOUTH_BOX);
        }
        if (tile.getConnections().get(EnumFacing.WEST) > 0) {
            Block.addCollisionBoxToList(pos, mask, list, WEST_BOX);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        // Fetch Tile Entity
        final TileEntityChannel tile = WorldHelper.getTile(world, pos, TileEntityChannel.class).orElse(null);

        // Define Core Bounding Box
        AxisAlignedBB selection = CENTER_BOX;

        // Expand Bounding Box
        if (tile != null) {
            if (tile.getConnections().get(EnumFacing.NORTH) > 0) {
                selection = selection.union(NORTH_BOX);
            }
            if (tile.getConnections().get(EnumFacing.EAST) > 0) {
                selection = selection.union(EAST_BOX);
            }
            if (tile.getConnections().get(EnumFacing.SOUTH) > 0) {
                selection = selection.union(SOUTH_BOX);
            }
            if (tile.getConnections().get(EnumFacing.WEST) > 0) {
                selection = selection.union(WEST_BOX);
            }
        }

        return selection;
    }

    //render methods
    //--------------
    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannel getRenderer() {
        return new RenderChannel(this, this.createNewTileEntity(null, 0));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

}
